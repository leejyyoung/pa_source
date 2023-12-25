package com.cware.partner.sync.service;

import java.sql.Timestamp;
import java.util.List;
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
import com.cware.partner.common.util.StringUtil;
import com.cware.partner.sync.domain.SyncResult;
import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.PaCopnGoods;
import com.cware.partner.sync.domain.entity.PaCopnGoodsAttri;
import com.cware.partner.sync.domain.entity.PaGoodsOffer;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;
import com.cware.partner.sync.domain.entity.PaSaleNoGoods;
import com.cware.partner.sync.domain.id.PaGoodsAttrSeq;
import com.cware.partner.sync.domain.id.PaGoodsId;
import com.cware.partner.sync.repository.PaCopnGoodsAttriRepository;
import com.cware.partner.sync.repository.PaCopnGoodsRepository;
import com.cware.partner.sync.repository.PaGoodsOfferRepository;
import com.cware.partner.sync.repository.PaSaleNoGoodsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SyncCoupangService extends PartnerSyncService {

	@Autowired
	private CommonService commonRepository;

	@Autowired
	private PaCopnGoodsRepository partnerGoodsRepository;

	@Autowired
	PaGoodsOfferRepository goodsOfferRepository;

	@Autowired
	PaCopnGoodsAttriRepository copnGoodsAttriRepository;

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
		tag = "쿠팡 동기화";

		target.setMediaCode(PaGroup.COUPANG.mediaCode());

		log.info("{} -------> {}", tag, target);

		Optional<PaCopnGoods> optional = partnerGoodsRepository
				.findById(new PaGoodsId(target.getPaGroupCode(), target.getPaCode(), target.getGoodsCode()));
		PaCopnGoods partnerGoods;
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
			result = syncGoodsOfferModify(goods.getOfferType(), partnerGoods.getPaLmsdKey(), target);
			isTransTarget = isTransTarget || result;

			// 상품정보변경 동기화
			result =  syncPaGoods(goods, target);
			isTransTarget = isTransTarget || result;

			// 단품정보변경 동기화
			result = syncGoodsDt(goods.getGoodsDtList(), target);
			isTransTarget = isTransTarget || result;

			// 이미지 동기화
			result = syncGoodsImage(goods.getGoodsImage(), target);
			isTransTarget = isTransTarget || result;

			// 가격변경 동기화 (상품수정에서 가격변경하지 않음)
			result = syncGoodsPriceMoidfy(goods.getGoodsPrice(),goods.getLmsdCode(), target);
//			isTransTarget = isTransTarget || result;

			// 프로모션변경 동기화 (상품수정에서 가격변경하지 않음)
			result = syncPromoModifyTarget(goods, target);
//			isTransTarget = isTransTarget || result;

			// 공지사항 동기화
			result = syncPaNotice(goods, target);
			isTransTarget = isTransTarget || result;

			// 상품재고 동기화
//			result = syncGoodsStock(goods, target);
//			isTransTarget = isTransTarget || result;

			// 업체회수지변경 동기화
			result = syncEntpReturn(goods, target);
			isTransTarget = isTransTarget || result;

			// 배송비 동기화
			result = syncShipCost(goods.getPaCustShipCost(), target);
			isTransTarget = isTransTarget || result;

			// 상품수정 타겟팅
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
		partnerGoods = PaCopnGoods.builder().goodsCode(target.getGoodsCode()).paCode(target.getPaCode())
				.paGroupCode(target.getPaGroupCode()).paSaleGb(PaSaleGb.FORSALE.code())
				.paStatus(PaStatus.REQUEST.code())
				.displayProductName(goods.getGoodsName())
				.generalProductName(goods.getGoodsNameMc())
				.transOrderAbleQty(target.getPartnerStockQty())
				.transTargetYn("1")
				.transSaleYn("0")
				.massTargetYn("0")
				.lastSyncDate(procDate)
				.insertId(Application.ID.code())
				.insertDate(procDate)
				.modifyId(Application.ID.code())
				.modifyDate(procDate)
				.build();

		target.setPartnerGoods(partnerGoods);

		// 표준카테고리매핑
		partnerGoods.setPaLmsdKey(getLmsdKey(goods.getLmsdCode(), target));
		if (partnerGoods.getPaLmsdKey() == null) return false;
		partnerGoods.setDisplayCategoryCode(partnerGoods.getPaLmsdKey());

		// 정보고시 동기화
		if (!syncGoodsOffer(goods.getOfferType(), partnerGoods.getPaLmsdKey(), target)) return false;

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

	// 정보고시동기화
	protected boolean syncGoodsOffer(String offerType, String paLmsdKey, PaGoodsTarget target) {
		String tag = "쿠팡 정보고시 동기화";

		String offerTypeName = goodsOfferRepository.getPaOfferTypeName(target.getPaGroupCode(), target.getGoodsCode());

		if (offerTypeName == null) {
			target.setExcept(true);
			target.setExceptNote("제휴사 정보고시 매핑 데이터가 없습니다.");
			log.info("{}: {} [상품:{}]", tag, target.getExceptNote(), target.getGoodsCode());
			logFilter("SYNCCOUPANG-GOODS_OFFER", target);
			return false;
		}

		String paOfferType = null;
		offerTypeName = goodsOfferRepository.getCoupangNoticeCategoryName(paLmsdKey, offerTypeName);
		if (offerTypeName == null) {
			offerTypeName = goodsOfferRepository.getCoupangNoticeCategoryEtcName(paLmsdKey);
			if (offerTypeName == null) {
				offerTypeName = goodsOfferRepository.getCoupangNoticeCategoryDefaultName(paLmsdKey);
				if (offerTypeName == null) {
					target.setExcept(true);
					target.setExceptNote("제휴사 정보고시유형(기타) 데이터가 없습니다.");
					log.info("{}: {} [상품:{}]", tag, target.getExceptNote(), target.getGoodsCode());
					logFilter("SYNCCOUPANG-GOODS_OFFER", target);
					return false;
				}
				offerTypeName = offerTypeName.split(",")[0].replace("\"", "").replace(" ", "");
				paOfferType = goodsOfferRepository.getPaOfferType(target.getPaGroupCode(), offerTypeName);
				if (paOfferType == null) {
					target.setExcept(true);
					target.setExceptNote("제휴사 정보고시유형 데이터가 없습니다.");
					log.info("{}: {} [상품:{}]", tag, target.getExceptNote(), target.getGoodsCode());
					logFilter("SYNCCOUPANG-GOODS_OFFER", target);
					return false;
				}
			} else {
				paOfferType = "0529"; // 기타 재화
			}
		}

		Timestamp currentDate = commonRepository.currentDate();

		List<PaGoodsOffer> list = null;

		if (paOfferType == null) {
			list = goodsOfferRepository.selectGoodsOffer(target.getPaGroupCode(), target.getGoodsCode(), offerType);
		} else {
			list = goodsOfferRepository.selectGoodsOfferByPaOfferType(target.getPaGroupCode(), paOfferType);
		}

		if (list.isEmpty()) {
			target.setExcept(true);
			target.setExceptNote("제휴사 정보고시 데이터가 없습니다.");
			log.info("{}: {} [상품:{}]", tag, target.getExceptNote(), target.getGoodsCode());
			logFilter("SYNCCOUPANG-GOODS_OFFER", target);
			return false;
		}

		for (PaGoodsOffer offer : list) {

			if (paOfferType != null) {
				offer.setPaOfferExt("상세설명 참조");
			}
			offer.setGoodsCode(target.getGoodsCode());
			offer.setUseYn("1");
			offer.setTransTargetYn("1");
			offer.setLastSyncDate(currentDate);
			offer.setInsertId(Application.ID.code());
			offer.setInsertDate(currentDate);
			offer.setModifyId(Application.ID.code());
			offer.setModifyDate(currentDate);
			goodsOfferRepository.save(offer);

		}

		// 쿠팡상품속성 생성
		// 최초에 한 레코드만 동일한 값으로 생성함. 레거시 로직 그대로 이관
		boolean exists = copnGoodsAttriRepository.existsById(new PaGoodsAttrSeq(target.getGoodsCode(), "001"));

		if (!exists) {

			PaCopnGoodsAttri attr = PaCopnGoodsAttri.builder()
					.goodsCode(target.getGoodsCode())
					.attributeSeq("001")
					.attributeTypeName("색상/크기/무늬/형태")
					.attributeTypeMapping("색상,사이즈,무늬,형태")
					.dataType("STRING")
					.basicUnit("없음")
					.groupNumber("NONE")
					.exposed("1")
					.requiredYn("0")
					.insertId(Application.ID.code())
					.insertDate(currentDate)
					.modifyId(Application.ID.code())
					.modifyDate(currentDate)
					.build();
			copnGoodsAttriRepository.save(attr);
		}

		return true;
	}


	// 정보고시동기화
	protected boolean syncGoodsOfferModify(String offerType, String paLmsdKey, PaGoodsTarget target) {
		String syncNote = "정보고시변경 동기화";

		boolean isDirty;

		String paGroupCode = PaGroup.COUPANG.code();

		// 이전 동기화된 offerType
		String syncOfferType = goodsOfferRepository.getOfferType(paGroupCode, target.getGoodsCode());

		if (StringUtil.compare(offerType, syncOfferType)) {
			List<PaGoodsOffer> list = goodsOfferRepository.selectGoodsOfferModify(paGroupCode, target.getGoodsCode());

			Timestamp currentDate = commonService.currentDate();

			for (PaGoodsOffer offer : list) {
				offer.setTransTargetYn("1");
				offer.setLastSyncDate(currentDate);
				offer.setModifyId(Application.ID.code());
				offer.setModifyDate(currentDate);
				goodsOfferRepository.saveAndFlush(offer);
			}

			isDirty = list.size() > 0;

			list = goodsOfferRepository.selectGoodsOfferNew(paGroupCode, target.getGoodsCode());

			for (PaGoodsOffer offer : list) {
				offer.setGoodsCode(target.getGoodsCode());
				offer.setTransTargetYn("1");
				offer.setLastSyncDate(currentDate);
				offer.setInsertId(Application.ID.code());
				offer.setInsertDate(currentDate);
				offer.setModifyId(Application.ID.code());
				offer.setModifyDate(currentDate);
				goodsOfferRepository.save(offer);
			}
			isDirty = isDirty || (list.size() > 0);

		} else if (!"38".equals(offerType) && "38".equals(syncOfferType)) {
			// 정보고시가 매핑되지 않아 기타 재화로 등록된 경우 변경여부 체크하지 않음
			return false;

		} else {
			// 이전 정보고시유형과 달라졌을 경우 기존 정보고시 비활성화 후 카테고리 매핑하여 새로 생성
			isDirty = true;
			goodsOfferRepository.disableGooodsOffer(paGroupCode, target.getGoodsCode(), Application.ID.code());
			syncGoodsOffer(offerType,  paLmsdKey, target);
		}

		if (isDirty ) {
			logSync(CdcReason.OFFER_MODIFY.code(), syncNote, target);
			return true;
		}
		return false;
	}


	// 재입점
	private boolean syncForSale (PaGoodsTarget target) {

		PaCopnGoods partnerGoods = (PaCopnGoods)target.getPartnerGoods();

		// 입점완료상태이면서 판매중지 상태인 경우 재입점 처리
		// 사용자가 연동해제한 경우 auto_yn이 0으로 설정되어 자동재입점 방지
		if (PaStatus.COMPLETE.code().equals(partnerGoods.getPaStatus())
				&& PaSaleGb.SUSPEND.code().equals(partnerGoods.getPaSaleGb())
				&& "1".equals(target.getAutoYn())) {
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
	private boolean syncPaGoods (Goods goods, PaGoodsTarget target) {

		PaCopnGoods partnerGoods = (PaCopnGoods)target.getPartnerGoods();

		if(goods.getPaGoods().getLastSyncDate().after(partnerGoods.getLastSyncDate())) {
			partnerGoods.setDisplayProductName(goods.getGoodsName());
			partnerGoods.setGeneralProductName(goods.getGoodsNameMc());
			String syncNote = "상품정보변경";
			log.info("{} {} 상품: {} ", tag, syncNote, goods.getGoodsCode() );
			logSync(CdcReason.GOODS_MODIFY.code(), syncNote, target);
			return true;
		} else if (goods.getPaGoods().getLastDescribeSyncDate().after(partnerGoods.getLastSyncDate())) {  // 기술서 변경되면 연동타겟으로 설정
			String syncNote = "상품기술서변경";
			log.info("{} {} 상품: {} ", tag, syncNote, goods.getGoodsCode() );
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

		log.info("쿠팡 판매중지 상품: {} ({})", goodsCode, result );

		return result;
	}

	// 제휴필터에서 호출
	public boolean stopSale(PaGoodsTarget target) {
		Optional<PaCopnGoods> optional = partnerGoodsRepository
				.findById(new PaGoodsId(target.getPaGroupCode(), target.getPaCode(), target.getGoodsCode()));

		if (!optional.isPresent()) return false;

		PaCopnGoods partnerGoods = optional.get();
		Timestamp currentDate = commonRepository.currentDate();


		// 입점완료이고, 판매중인 경우 판매중단연동타겟팅 및 이력생성
		if (PaStatus.COMPLETE.code().equals(partnerGoods.getPaStatus()) &&
			PaSaleGb.FORSALE.code().equals(partnerGoods.getPaSaleGb())) {

			partnerGoods.setPaSaleGb(PaSaleGb.SUSPEND.code());
			partnerGoods.setTransSaleYn("1");
			partnerGoods.setModifyDate(currentDate);
			partnerGoods.setModifyId(Application.ID.code());
			partnerGoodsRepository.save(partnerGoods);

			PaSaleNoGoods saleNo = PaSaleNoGoods.builder()
					.paGroupCode(target.getPaGroupCode())
					.paCode(target.getPaCode())
					.goodsCode(target.getGoodsCode())
					.seqNo(saleNoGoodsRepository.getNextSeq(target.getPaGroupCode(), target.getPaCode(), target.getGoodsCode()))
					.paGoodsCode(partnerGoods.getSellerProductId())
					.paSaleGb(PaSaleGb.SUSPEND.code())
					.note(target.getExceptNote())
					.insertId(Application.ID.code())
					.insertDate(currentDate)
					.build();
			saleNoGoodsRepository.save(saleNo);

			String syncNote = "제휴필터 판매중지";
			log.info("{} 상품: {} ", syncNote, target.getGoodsCode() );
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

		log.info("쿠팡 전송대상 상품: {} ({})", goodsCode, result );

		return result > 0;
	}
}
