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
import com.cware.partner.common.code.PaGroup;
import com.cware.partner.common.code.PaSaleGb;
import com.cware.partner.common.code.PaStatus;
import com.cware.partner.common.code.SaleGb;
import com.cware.partner.common.service.CommonService;
import com.cware.partner.sync.domain.SyncResult;
import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.GoodsImage;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;
import com.cware.partner.sync.domain.entity.PaKakaoGoods;
import com.cware.partner.sync.domain.entity.PaOriginMapping;
import com.cware.partner.sync.domain.entity.PaSaleNoGoods;
import com.cware.partner.sync.domain.id.PaGoodsId;
import com.cware.partner.sync.domain.id.PaOriginId;
import com.cware.partner.sync.repository.PaBrandMappingRepository;
import com.cware.partner.sync.repository.PaKakaoGoodsImageRepository;
import com.cware.partner.sync.repository.PaKakaoGoodsRepository;
import com.cware.partner.sync.repository.PaMakerMappingRepository;
import com.cware.partner.sync.repository.PaOriginMappingRepository;
import com.cware.partner.sync.repository.PaSaleNoGoodsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SyncKakaoService extends PartnerSyncService {

	@Autowired
	private CommonService commonRepository;

	@Autowired
	private PaKakaoGoodsRepository partnerGoodsRepository;

	@Autowired
	private PaBrandMappingRepository brandMappingRepository;

	@Autowired
	private PaMakerMappingRepository makerMappingRepository;

	@Autowired
	private PaOriginMappingRepository originMappingRepository;

	@Autowired
	private PaKakaoGoodsImageRepository kakaoImageRepository;

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
		tag = "카카오쇼핑 동기화";

		target.setMediaCode(PaGroup.KAKAO.mediaCode());

		log.info("{} -------> {}", tag, target);

		Optional<PaKakaoGoods> optional = partnerGoodsRepository
				.findById(new PaGoodsId(target.getPaGroupCode(), target.getPaCode(), target.getGoodsCode()));
		PaKakaoGoods partnerGoods;
		Timestamp procDate = commonRepository.currentDate();

		// 재입점, 동기화
		if (optional.isPresent()) {
			partnerGoods = optional.get();
			target.setPartnerGoods(partnerGoods);

			boolean isTransTarget = false;
			boolean result;

			// 재입점 처리
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

			// 가격변경 동기화 (상품수정에서 가격변경하지 않음)
			result = syncGoodsPriceMoidfy(goods.getGoodsPrice(),goods.getLmsdCode(), target);
			isTransTarget = isTransTarget || result;

			// 프로모션변경 동기화 (상품수정에서 가격변경하지 않음)
			result = syncPromoModifyTarget(goods, target);
			isTransTarget = isTransTarget || result;

			// 공지사항 동기화
			result = syncPaNotice(goods, target);
			isTransTarget = isTransTarget || result;

			// 공지사항 동기화
			result = syncPaNotice(goods, target);
			isTransTarget = isTransTarget || result;

			// 배송비 동기화
			result = syncShipCost(goods.getPaCustShipCost(), target);
			isTransTarget = isTransTarget || result;

			if (isTransTarget) {
				partnerGoods.setTransTargetYn("1");
				partnerGoods.setLastSyncDate(procDate);
				partnerGoods.setModifyDate(procDate);
				partnerGoods.setModifyId(Application.ID.code());

				if (PaStatus.REJECT.code().equals(partnerGoods.getPaStatus())
						&& SaleGb.FORSALE.code().equals(goods.getSaleGb())) {
					partnerGoods.setPaSaleGb(PaSaleGb.FORSALE.code());
				}

				partnerGoodsRepository.save(partnerGoods);
			}

			return isTransTarget;

		}

		// 신규입점
		partnerGoods = PaKakaoGoods.builder().goodsCode(target.getGoodsCode()).paCode(target.getPaCode())
				.paGroupCode(target.getPaGroupCode()).paSaleGb(PaSaleGb.FORSALE.code())
				.goodsName(goods.getGoodsNameMc()).paStatus(PaStatus.REQUEST.code())
				.transOrderAbleQty(target.getPartnerStockQty()).transTargetYn("1").transSaleYn("0").massTargetYn("0")
				.lastSyncDate(procDate).insertId(Application.ID.code()).insertDate(procDate)
				.modifyId(Application.ID.code()).modifyDate(procDate).build();

		target.setPartnerGoods(partnerGoods);

		// 표준카테고리매핑
		partnerGoods.setCategoryId(getLmsdKey(goods.getLmsdCode(), target));
		if (partnerGoods.getCategoryId() == null)
			return false;

		// 정보고시 동기화
		if (!syncGoodsOffer(goods.getOfferType(), target))
			return false;

		// 원산지코드매핑
		mappingOriginCode(goods.getOriginCode(), target);

		// 브랜드매핑
		partnerGoods.setBrand(
				brandMappingRepository.findMappingByPaGroupCode(target.getPaGroupCode(), goods.getBrandCode()));

		// 제조사매핑 추가
		partnerGoods.setManufacturer(
				makerMappingRepository.findMappingByPaGroupCode(target.getPaGroupCode(), goods.getMakecoCode()));

		// 상품 기본 동기화
		partnerGoodsRepository.save(partnerGoods);

		// 가격 동기화
		syncGoodsPrice(goods.getGoodsPrice(),goods.getLmsdCode(), target);

		// 프로모션 동기화
		syncPromoTarget(goods, target);

		// 이미지 동기화
		syncGoodsImage(goods.getGoodsImage(), target);

		// 단품 동기화
		syncGoodsDt(goods.getGoodsDtList(), target);

		// 공지사항 동기화
		syncPaNotice(goods, target);

		// 배송비 동기화
		syncShipCost(goods.getPaCustShipCost(), target);

		// 제휴입점요청처리
		requestSyncPartner(target);

		return true;
	}

	private boolean mappingOriginCode(String originCode, PaGoodsTarget target) {
		PaKakaoGoods partnerGoods = (PaKakaoGoods) target.getPartnerGoods();
		Optional<PaOriginMapping> optional = originMappingRepository
				.findById(new PaOriginId(partnerGoods.getPaGroupCode(), originCode));
		if (optional.isPresent()) {
			partnerGoods.setProductOrginAreaInfo(optional.get().getPaOriginCode());
		} else {
			partnerGoods.setProductOrginAreaInfo("03"); // 혼합/기타 (상세설명참조)
		}
		return true;
	}

	// 카카오쇼핑 상품이미지 저장
	protected boolean syncGoodsImage(GoodsImage image, PaGoodsTarget target) {
		boolean isDirty = super.syncGoodsImage(image, target);

		if (isDirty) {
			// 카카오쇼핑 연동용 데이터 생성
			kakaoImageRepository.deleteByGoodsCodeAndPaGroupCode(target.getGoodsCode(), target.getPaGroupCode());
			kakaoImageRepository.insertGoodsImage(target.getGoodsCode(), target.getPaGroupCode(),
					Application.ID.code());
		}

		return isDirty;
	}

	// 재입점
	private boolean syncForSale(PaGoodsTarget target) {

		PaKakaoGoods partnerGoods = (PaKakaoGoods) target.getPartnerGoods();

		// 입점완료상태이면서 판매중지 상태인 경우 재입점 처리
		// 사용자가 연동해제한 경우 auto_yn이 0으로 설정되어 자동재입점 방지
		if (PaStatus.COMPLETE.code().equals(partnerGoods.getPaStatus())
				&& PaSaleGb.SUSPEND.code().equals(partnerGoods.getPaSaleGb()) && "1".equals(target.getAutoYn())) {
			String syncNote = "상품 재입점";
			Timestamp currentDate = commonRepository.currentDate();

			partnerGoods.setTransSaleYn("1");
			partnerGoods.setPaSaleGb(PaSaleGb.FORSALE.code());
			partnerGoods.setModifyId(Application.ID.code());
			partnerGoods.setModifyDate(currentDate);
			partnerGoodsRepository.save(partnerGoods);
			logSync(CdcReason.SALE_START.code(), syncNote, target);
			return true;
		} else if (PaStatus.REJECT.code().equals(partnerGoods.getPaStatus()) && "1".equals(target.getAutoYn())) { // 반려건 입점요청
			String syncNote = "반려상품 입점요청";
			Timestamp currentDate = commonService.currentDate();
			partnerGoods.setPaStatus(PaSaleGb.REQUEST.code());
			partnerGoods.setPaSaleGb(PaSaleGb.FORSALE.code());
			partnerGoods.setModifyId(Application.ID.code());
			partnerGoods.setModifyDate(currentDate);
			partnerGoodsRepository.save(partnerGoods);
			logSync(CdcReason.SALE_START.code(), syncNote, target);
		} else {
			if (requestSyncPartner(target))
				return true;
		}

		return false;

	}

	// 상품정보동기화
	private boolean syncPaGoods(Goods goods, PaGoodsTarget target) {

		PaKakaoGoods partnerGoods = (PaKakaoGoods) target.getPartnerGoods();

		if (goods.getPaGoods().getLastSyncDate().after(partnerGoods.getLastSyncDate())) {
			mappingOriginCode(goods.getOriginCode(), target);
			partnerGoods.setGoodsName(goods.getGoodsNameMc());
			partnerGoods.setBrand(brandMappingRepository.findMappingByPaGroupCode(partnerGoods.getPaGroupCode(),
					goods.getBrandCode()));
			partnerGoods.setManufacturer(
					makerMappingRepository.findMappingByPaGroupCode(target.getPaGroupCode(), goods.getMakecoCode()));
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
		return stopSale(goodsCode, "%");
	}

	// 판매중지처리
	public int stopSale(String goodsCode, String paCode) {
		int result = partnerGoodsRepository.stopSale(goodsCode, paCode, Application.ID.code());

		log.info("카카오쇼핑 판매중지 상품: {} ({})", goodsCode, result);

		return result;
	}

	// 제휴필터에서 호출
	public boolean stopSale(PaGoodsTarget target) {
		Optional<PaKakaoGoods> optional = partnerGoodsRepository
				.findById(new PaGoodsId(target.getPaGroupCode(), target.getPaCode(), target.getGoodsCode()));

		if (!optional.isPresent())
			return false;

		PaKakaoGoods partnerGoods = optional.get();
		Timestamp currentDate = commonRepository.currentDate();


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
					.paGoodsCode(partnerGoods.getProductId()).paSaleGb(PaSaleGb.SUSPEND.code())
					.note(target.getExceptNote()).insertId(Application.ID.code()).insertDate(currentDate).build();
			saleNoGoodsRepository.save(saleNo);

			String syncNote = "제휴필터 판매중지";
			log.info("{} 상품: {} ", syncNote, target.getGoodsCode());
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
	public boolean enableTransTarget(String goodsCode, String paCode) {
		int result = partnerGoodsRepository.enableTransTarget(goodsCode, paCode, Application.ID.code());

		log.info("카카오쇼핑 전송대상 상품: {} ({})", goodsCode, result);

		return result > 0;
	}
}
