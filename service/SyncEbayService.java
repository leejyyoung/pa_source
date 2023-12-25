package com.cware.partner.sync.service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cware.partner.common.code.Application;
import com.cware.partner.common.code.CdcReason;
import com.cware.partner.common.code.Origin;
import com.cware.partner.common.code.PaGroup;
import com.cware.partner.common.code.PaSaleGb;
import com.cware.partner.common.code.PaStatus;
import com.cware.partner.common.code.SaleGb;
import com.cware.partner.sync.domain.EbayCategory;
import com.cware.partner.sync.domain.SyncResult;
import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.GoodsImage;
import com.cware.partner.sync.domain.entity.GoodsInfoImage;
import com.cware.partner.sync.domain.entity.PaGmktGoods;
import com.cware.partner.sync.domain.entity.PaGmktOriginMapping;
import com.cware.partner.sync.domain.entity.PaGoodsImage;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;
import com.cware.partner.sync.domain.entity.PaSaleNoGoods;
import com.cware.partner.sync.domain.id.PaGoodsId;
import com.cware.partner.sync.domain.id.PaGroupGoodsId;
import com.cware.partner.sync.repository.PaBrandMappingRepository;
import com.cware.partner.sync.repository.PaGmktGoodsRepository;
import com.cware.partner.sync.repository.PaGmktMakerRepository;
import com.cware.partner.sync.repository.PaGmktOriginMappingRepository;
import com.cware.partner.sync.repository.PaSaleNoGoodsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SyncEbayService extends PartnerSyncService {

	@Autowired
	private PaGmktOriginMappingRepository originMappingRepository;

	@Autowired
	private PaBrandMappingRepository brandMappingRepository;

	@Autowired
	private PaGmktMakerRepository makerRepository;

	@Autowired
	private PaGmktGoodsRepository partnerGoodsRepository;

	@Autowired
	private PaSaleNoGoodsRepository saleNoGoodsRepository;

	@Async("partnerAsyncExecutor")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public CompletableFuture<SyncResult> asyncService(Goods goods, PaGoodsTarget target) {
		boolean result = syncProduct(goods, target);
		SyncResult sync = SyncResult.builder().isSync(result).target(target).build();
		return CompletableFuture.completedFuture(sync);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean syncProduct(Goods goods, PaGoodsTarget target) {
		tag = "이베이 동기화";

		target.setMediaCode(PaGroup.GMARKET.code().equals(target.getPaGroupCode()) ? PaGroup.GMARKET.mediaCode()
				: PaGroup.AUCTION.mediaCode());

		log.info("{} -------> {}", tag, target);

		Optional<PaGmktGoods> optional = partnerGoodsRepository
				.findById(new PaGoodsId(target.getPaGroupCode(), target.getPaCode(), target.getGoodsCode()));
		PaGmktGoods partnerGoods;
		Timestamp procDate = commonService.currentDate();

		// 재입점, 동기화
		if (optional.isPresent()) {
			partnerGoods = optional.get();
			target.setPartnerGoods(partnerGoods);

			// 마지막 동기화일시 마이그레이션
			if (partnerGoods.getLastSyncDate() == null) {
				partnerGoods.setLastSyncDate(partnerGoods.getModifyDate().after(goods.getPaGoods().getLastSyncDate())
						? goods.getPaGoods().getLastSyncDate()
						: partnerGoods.getModifyDate());
			}

			boolean isTransTarget = false;
			boolean result;

			// 재입점 처리 (지마켓/옥션 분리)
			isTransTarget = syncForSale(target);

			// 정보고시 동기화
			result = syncGoodsOfferModify(goods.getOfferType(), target);
			isTransTarget = isTransTarget || result;

			// 상품정보변경 동기화
			result = syncPaGoods(goods, target);
			isTransTarget = isTransTarget || result;

			// 단품정보변경 동기화
			result = syncGoodsDt(goods.getGoodsDtList(), target);
			isTransTarget = isTransTarget || result;

			// 이미지 동기화
			result = syncGoodsImage(goods.getGoodsImage(), target);
			isTransTarget = isTransTarget || result;

			// 가격변경 동기화
			result = syncGoodsPriceMoidfy(goods.getGoodsPrice(),goods.getLmsdCode(), target);
			isTransTarget = isTransTarget || result;

			// 프로모션변경 동기화 (지마켓/옥션 분리)
			result = syncPromoModifyTarget(goods, target);
			isTransTarget = isTransTarget || result;

			// 공지사항 동기화 (지마켓/옥션 분리)
			result = syncPaNotice(goods, target);
			isTransTarget = isTransTarget || result;

//			// 상품재고 동기화
//			result = syncGoodsStock(goods, target);
//			isTransTarget = isTransTarget || result;

			// 배송비 동기화
			result = syncShipCost(goods.getPaCustShipCost(), target);
			isTransTarget = isTransTarget || result;

			if (isTransTarget) {
				partnerGoods.setTransTargetYn("1");
				partnerGoods.setLastSyncDate(procDate);
				partnerGoods.setModifyDate(procDate);
				partnerGoods.setModifyId(Application.ID.code());

				// 이베이는 동시 입점이 기본이기 때문에 판매중 상태로 수정시 입점 요청 될 수 있으므로 자동입점여부 한번더 체크
				if (PaStatus.REJECT.code().equals(partnerGoods.getPaStatus())
						&& SaleGb.FORSALE.code().equals(goods.getSaleGb())
						&& "1".equals(target.getAutoYn())) {
					partnerGoods.setPaSaleGb(PaSaleGb.FORSALE.code());
				}

				partnerGoodsRepository.save(partnerGoods);
			}

			return isTransTarget;

		}

		// 신규입점
		partnerGoods = PaGmktGoods.builder().goodsCode(target.getGoodsCode()).paCode(target.getPaCode())
				.paGroupCode(target.getPaGroupCode()).paSaleGb(PaSaleGb.FORSALE.code())
				.paStatus(PaStatus.REQUEST.code()).transTargetYn("1").transDiscountYn("1").transSaleYn("0")
				.massTargetYn("0").paSgroup("1").transOrderAbleQty(target.getPartnerStockQty()).lastSyncDate(procDate)
				.insertId(Application.ID.code()).insertDate(procDate).modifyId(Application.ID.code())
				.modifyDate(procDate).build();

		target.setPartnerGoods(partnerGoods);

		// 원산지코드매핑
		mappingOriginCode(goods.getOriginCode(), target);

		// 카테고리 매핑 (지마켓/옥션 분리)
		if (!mappingCategoryCode(goods.getLmsdCode(), target))
			return false;

		// 정보고시 동기화
		if (!syncGoodsOffer(goods.getOfferType(), target))
			return false;

		// 브랜드매핑
		partnerGoods.setBrandNo(brandMappingRepository.findGmktMapping(goods.getBrandCode()));
		// 제조사매핑
		partnerGoods.setMakerNo(makerRepository.getMakerNo(goods.getMakecoCode()));

		// 상품 기본 동기화
		partnerGoodsRepository.save(partnerGoods);

		// 가격 동기화
		syncGoodsPrice(goods.getGoodsPrice(),goods.getLmsdCode(), target);

		// 프로모션 동기화 (지마켓/옥션 분리)
		syncPromoTarget(goods, target);

		// 이미지 동기화
		syncGoodsImage(goods.getGoodsImage(), target);

		// 단품 동기화
		syncGoodsDt(goods.getGoodsDtList(), target);

		// 공지사항 동기화 (지마켓/옥션 분리)
		syncPaNotice(goods, target);

		// 배송비 동기화
		syncShipCost(goods.getPaCustShipCost(), target);

		// 제휴입점요청처리 (지마켓/옥션 분리)
		requestSyncPartner(target);

		return true;
	}

	private boolean mappingOriginCode(String originCode, PaGoodsTarget target) {

		PaGmktGoods partnerGoods = (PaGmktGoods) target.getPartnerGoods();
		if (Origin.ETC.code().equals(originCode)) {
			partnerGoods.setOriginEnum("40");
		} else if (Origin.KOREA.code().equals(originCode)) {
			partnerGoods.setOriginEnum("20");
		} else {
			partnerGoods.setOriginEnum("30");
			Optional<PaGmktOriginMapping> optional = originMappingRepository.findById(originCode);
			if (optional.isPresent())
				partnerGoods.setOriginDt(optional.get().getOrgnTypDtlsCd());
		}
		return true;
	}

	protected boolean mappingCategoryCode(String lmsdCode, PaGoodsTarget target) {
		String tag = "이베이 카테고리 매핑";

		EbayCategory category;
		PaGmktGoods partnerGoods = (PaGmktGoods) target.getPartnerGoods();
		try {
			category = paGoodsKindsMappingRepository.getEbayCategory(partnerGoods.getPaGroupCode(), lmsdCode);
			partnerGoods.setEsmCategoryCode(category.getEsmCategoryCode());
			partnerGoods.setSiteCategoryCode(category.getSiteCategoryCode());
		} catch (NullPointerException e) {
			target.setExcept(true);
			target.setExceptNote("제휴사 표준카테고리가 존재하지 않습니다.");
			log.info("{}: {} [상품:{}]", tag, target.getExceptNote(), target.getGoodsCode());
			logFilter("SYNCEBAY-LMSD_CODE", target);
			return false;
		}
		return true;

	}

	// 이베이 이미지 동기화
	protected boolean syncGoodsImage(GoodsImage image, PaGoodsTarget target) {
		String syncNote = "이미지 동기화";

		Timestamp currentDate = commonService.currentDate();
		String syncCode = null;

		Optional<PaGoodsImage> optional = imageRepository
				.findById(new PaGroupGoodsId(PaGroup.GMARKET.code(), target.getGoodsCode()));

		PaGoodsImage goodsImage;

		if (optional.isPresent()) {
			goodsImage = optional.get();
		} else {
			goodsImage = PaGoodsImage.builder()
					.goodsCode(target.getGoodsCode())
					.paGroupCode(PaGroup.GMARKET.code())
					.insertDate(currentDate)
					.insertId(Application.ID.code())
					.build();
		}

		// 이베이 대표이미지
		GoodsInfoImage infoImage = imageRepository.getEbayGoodsImage(target.getGoodsCode());

		if (goodsImage.getLastSyncDate() == null ||
				image.getModifyDate().after(goodsImage.getLastSyncDate())) {

			syncCode = CdcReason.IMAGE_MODIFY.code();
			// 이베이 딜 상품 이미지는 TGOODSINFOIMAGE (INFO_IMAGE_TYPE 200) 으로 관리
			if (infoImage != null) {
				goodsImage.setImageP(infoImage.getInfoImageFile1());
			} else {
				goodsImage.setImageP(image.getImageC() == null ? image.getImageG() : image.getImageC());
			}

		} else {
			// 대표이미지 변경여부 체크
			if (goodsImage.getImageP().contains("info")) {
				if (infoImage == null) {
					syncCode = CdcReason.INFO_IMAGE_MODIFY.code();
					goodsImage.setImageP(image.getImageC() == null ? image.getImageG() : image.getImageC());
				} else if (infoImage.getModifyDate().after(goodsImage.getLastSyncDate())) {
					syncCode = CdcReason.INFO_IMAGE_MODIFY.code();
					goodsImage.setImageP(infoImage.getInfoImageFile1());
				}
			} else if (infoImage != null) {
				syncCode = CdcReason.INFO_IMAGE_MODIFY.code();
				goodsImage.setImageP(infoImage.getInfoImageFile1());
			}

		}

		if (syncCode != null) {
			goodsImage.setImageAp(image.getImageAt() == null ? image.getImageAg() : image.getImageAt());
			goodsImage.setImageBp(image.getImageBt() == null ? image.getImageBg() : image.getImageBt());
			goodsImage.setImageCp(image.getImageCt() == null ? image.getImageCg() : image.getImageCt());
			goodsImage.setImageDp(image.getImageDt() == null ? image.getImageDg() : image.getImageDt());
			goodsImage.setImageUrl(image.getImageUrl());
			goodsImage.setTransTargetYn("1");
			goodsImage.setLastSyncDate(currentDate);
			goodsImage.setModifyId(Application.ID.code());
			goodsImage.setModifyDate(currentDate);
			imageRepository.save(goodsImage);
			logSync(syncCode, syncNote, target);
			return true;
		}

		return false;
	}

	// 재입점
	private boolean syncForSale(PaGoodsTarget target) {

		PaGmktGoods partnerGoods = (PaGmktGoods) target.getPartnerGoods();

		// 입점완료상태이면서 판매중지 상태인 경우 재입점 처리
		if (PaStatus.COMPLETE.code().equals(partnerGoods.getPaStatus())
				&& PaSaleGb.SUSPEND.code().equals(partnerGoods.getPaSaleGb()) && "1".equals(target.getAutoYn())) {
			String syncNote = "상품 재입점";
			Timestamp currentDate = commonService.currentDate();

			partnerGoods.setTransSaleYn("1");
			partnerGoods.setPaSaleGb(PaSaleGb.FORSALE.code());
			partnerGoods.setModifyId(Application.ID.code());
			partnerGoods.setModifyDate(currentDate);
			partnerGoodsRepository.save(partnerGoods);
			logSync(CdcReason.SALE_START.code(), syncNote, target);
			return true;
		} else if (PaStatus.REJECT.code().equals(partnerGoods.getPaStatus()) && "1".equals(target.getAutoYn())) { // 반려건 입점요청
			if (partnerGoods.getReturnNote() == null || (!partnerGoods.getReturnNote().contains("504 Gateway Time-out")
					&& !partnerGoods.getReturnNote().contains("Read timed out"))) {
				String syncNote = "반려상품 입점요청";
				Timestamp currentDate = commonService.currentDate();
				partnerGoods.setPaStatus(PaSaleGb.REQUEST.code());
				partnerGoods.setPaSaleGb(PaSaleGb.FORSALE.code());
				partnerGoods.setModifyId(Application.ID.code());
				partnerGoods.setModifyDate(currentDate);
				partnerGoodsRepository.save(partnerGoods);
				logSync(CdcReason.SALE_START.code(), syncNote, target);
			}
		} else {
			if (requestSyncPartner(target))
				return true;
		}

		return false;
	}

	// 상품정보동기화
	private boolean syncPaGoods(Goods goods, PaGoodsTarget target) {

		PaGmktGoods partnerGoods = (PaGmktGoods) target.getPartnerGoods();

		if (goods.getPaGoods().getLastSyncDate().after(partnerGoods.getLastSyncDate())) {
			mappingOriginCode(goods.getOriginCode(), target);
			partnerGoods.setBrandNo(brandMappingRepository.findGmktMapping(goods.getBrandCode()));
			partnerGoods.setMakerNo(makerRepository.getMakerNo(goods.getMakecoCode()));
			String syncNote = "상품정보변경";
			log.info("{} {} 상품: {} ", tag, syncNote, goods.getGoodsCode());
			logSync(CdcReason.GOODS_MODIFY.code(), syncNote, target);
			return true;
		} else if (goods.getPaGoods().getLastDescribeSyncDate().after(partnerGoods.getLastSyncDate())) { // 기술서 변경되면
																											// 연동타겟으로 설정
			String syncNote = "상품기술서변경";
			log.info("{} {} 상품: {} ", tag, syncNote, goods.getGoodsCode());
			logSync(CdcReason.DESCRIBE_MODIFY.code(), syncNote, target);
			return true;
		}

		return false;

	}

	// 판매중지처리
	public int stopSale(String goodsCode) {
		return stopSale(goodsCode, "%", "%");

	}

	// 판매중지처리
	public int stopSale(String goodsCode, String paGroupCode, String paCode) {
		int result = partnerGoodsRepository.stopSale(goodsCode, paGroupCode, paCode, Application.ID.code());

		log.info("이베이 판매중지 상품: {} ({})", goodsCode, result);

		return result;
	}

	// 제휴필터에서 호출
	public boolean stopSale(PaGoodsTarget target) {
		Optional<PaGmktGoods> optional = partnerGoodsRepository
				.findById(new PaGoodsId(target.getPaGroupCode(), target.getPaCode(), target.getGoodsCode()));

		if (!optional.isPresent())
			return false;

		PaGmktGoods partnerGoods = optional.get();
		Timestamp currentDate = commonService.currentDate();

		// 입점완료이고, 판매중인 경우 판매중단연동타겟팅 및 이력생성
		if (PaStatus.COMPLETE.code().equals(partnerGoods.getPaStatus())
				&& PaSaleGb.FORSALE.code().equals(partnerGoods.getPaSaleGb())) {

			partnerGoods.setPaSaleGb(PaSaleGb.SUSPEND.code());
			partnerGoods.setTransSaleYn("1");
			partnerGoods.setModifyDate(currentDate);
			partnerGoods.setModifyId(Application.ID.code());
			partnerGoodsRepository.save(partnerGoods);

			PaSaleNoGoods saleNo = PaSaleNoGoods.builder().paGroupCode(target.getPaGroupCode())
					.paCode(target.getPaCode()).goodsCode(target.getGoodsCode())
					.seqNo(saleNoGoodsRepository.getNextSeq(target.getPaGroupCode(), target.getPaCode(),
							target.getGoodsCode()))
					.paGoodsCode(partnerGoods.getItemNo()).paSaleGb(PaSaleGb.SUSPEND.code())
					.note(target.getExceptNote()).insertId(Application.ID.code()).insertDate(currentDate).build();
			saleNoGoodsRepository.save(saleNo);

			String syncNote = "제휴필터 판매중지";
			log.info("{} {} 상품: {} ", tag, syncNote, target.getGoodsCode());
			logSync(CdcReason.SALE_END.code(), syncNote, target);

			return true;
		} else if (PaStatus.COMPLETE.code().compareTo(partnerGoods.getPaStatus()) > 0
				&& PaSaleGb.FORSALE.code().equals(partnerGoods.getPaSaleGb())) {
			// 입점전 판매중 데이터가 존재하는 경우 필터메시지와 판매중단 업데이트 처리
			partnerGoods.setPaStatus(PaStatus.REJECT.code());
			partnerGoods.setPaSaleGb(PaSaleGb.SUSPEND.code());
			partnerGoods.setReturnNote(target.getExceptNote());
			partnerGoods.setModifyDate(currentDate);
			partnerGoods.setModifyId(Application.ID.code());
			partnerGoodsRepository.save(partnerGoods);
		}

		return false;
	}

	// 전송대상설정
	public boolean enableTransTarget(String goodsCode, String paGroupCode, String paCode) {
		int result = partnerGoodsRepository.enableTransTarget(goodsCode, paGroupCode, paCode, Application.ID.code());

		log.info("이베이 전송대상 상품: {} ({})", goodsCode, paCode, result);

		return result > 0;
	}
}
