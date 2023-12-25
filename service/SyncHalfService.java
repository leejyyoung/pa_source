package com.cware.partner.sync.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.cware.partner.sync.domain.entity.GoodsDt;
import com.cware.partner.sync.domain.entity.PaCustShipCost;
import com.cware.partner.sync.domain.entity.PaGoodsDt;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;
import com.cware.partner.sync.domain.entity.PaHalfGoods;
import com.cware.partner.sync.domain.entity.PaHalfGoodsDtMapping;
import com.cware.partner.sync.domain.entity.PaOriginMapping;
import com.cware.partner.sync.domain.entity.PaSaleNoGoods;
import com.cware.partner.sync.domain.id.PaGoodsId;
import com.cware.partner.sync.domain.id.PaOriginId;
import com.cware.partner.sync.domain.id.PaShipCostId;
import com.cware.partner.sync.repository.PaHalfBrandMappingRepository;
import com.cware.partner.sync.repository.PaHalfGoodsDtMappingRepository;
import com.cware.partner.sync.repository.PaHalfGoodsRepository;
import com.cware.partner.sync.repository.PaHalfShipInfoRepository;
import com.cware.partner.sync.repository.PaOriginMappingRepository;
import com.cware.partner.sync.repository.PaSaleNoGoodsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SyncHalfService extends PartnerSyncService {

	@Autowired
	private CommonService commonRepository;

	@Autowired
	private PaHalfGoodsRepository partnerGoodsRepository;

	@Autowired
	private PaOriginMappingRepository originMappingRepository;

	@Autowired
	private PaSaleNoGoodsRepository saleNoGoodsRepository;

	@Autowired
	private PaHalfShipInfoRepository paHalfShipInfoRepository;

	@Autowired
	private PaHalfGoodsDtMappingRepository goodsDtRepository;

	@Autowired
	PaHalfBrandMappingRepository paHalfBrandMappingRepository;

	@Async("partnerAsyncExecutor")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public CompletableFuture<SyncResult> asyncService(Goods goods, PaGoodsTarget target) {
		boolean result = syncProduct(goods, target);
		SyncResult sync = SyncResult.builder().isSync(result).target(target).build();
		return CompletableFuture.completedFuture(sync);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean syncProduct(Goods goods, PaGoodsTarget target) {
		tag = "하프클럽 동기화";

		target.setMediaCode(PaGroup.HALF.mediaCode());

		log.info("{} -------> {}", tag, target);

		Optional<PaHalfGoods> optional = partnerGoodsRepository
				.findById(new PaGoodsId(target.getPaGroupCode(), target.getPaCode(), target.getGoodsCode()));
		PaHalfGoods partnerGoods;
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
			result = syncGoodsOfferModify(goods.getOfferType(), target);  //하프클럽 정보고시 타제휴사와 다른 특이사항 존재하지 않음
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
			result = syncGoodsPriceMoidfy(goods.getGoodsPrice(), goods.getLmsdCode(),target);
			isTransTarget = isTransTarget || result;

			// 프로모션변경 동기화 (상품수정에서 가격변경하지 않음)
			result = syncPromoModifyTarget(goods, target);
			isTransTarget = isTransTarget || result;

			// 공지사항 동기화
			result = syncPaNotice(goods, target);
			isTransTarget = isTransTarget || result;

			// 상품재고 동기화
			//result = syncGoodsStock(goods, target);
			//isTransTarget = isTransTarget || result;

			// 배송비 동기화
			result = syncShipCost(goods.getPaCustShipCost(), target);
			isTransTarget = isTransTarget || result;

			if (isTransTarget) {
				partnerGoods.setTransTargetYn("1");
				partnerGoods.setLastSyncDate(procDate);
				partnerGoods.setModifyDate(procDate);
				partnerGoods.setModifyId(Application.ID.code());

				// 반려건에 대한 입점 연동후 제휴판매상태를 업데이트 하지 않아 동기화에서 수행
				if (PaStatus.REJECT.code().equals(partnerGoods.getPaStatus())
						&& SaleGb.FORSALE.code().equals(goods.getSaleGb())) {
					partnerGoods.setPaSaleGb(PaSaleGb.FORSALE.code());
				}

				partnerGoodsRepository.save(partnerGoods);
			}

			return isTransTarget;

		}

		// 신규입점
		partnerGoods = PaHalfGoods.builder().goodsCode(target.getGoodsCode()).paCode(target.getPaCode())
				.paGroupCode(target.getPaGroupCode()).paSaleGb(PaSaleGb.FORSALE.code())
				.goodsName(goods.getGoodsName()).paStatus(PaStatus.REQUEST.code())
				.transOrderAbleQty(target.getPartnerStockQty()).transTargetYn("1").transSaleYn("0")
				.lastSyncDate(procDate).insertId(Application.ID.code()).insertDate(procDate)
				.modifyId(Application.ID.code()).modifyDate(procDate).build();

		target.setPartnerGoods(partnerGoods);

		// 표준카테고리매핑
		partnerGoods.setPaLmsdKey(getLmsdKey(goods.getLmsdCode(), target));
		if (partnerGoods.getPaLmsdKey() == null)
			return false;

		// 정보고시 동기화
		if (!syncGoodsOffer(goods.getOfferType(), target))
			return false;

		// 원산지코드매핑
		mappingOriginCode(goods.getOriginCode(), target);

		// 브랜드 매핑
		partnerGoods.setPaBrandNo(paHalfBrandMappingRepository.findHalfBrandMapping(target.getPaCode(), goods.getBrandCode(), goods.getReturnNoYn() ));
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
		PaHalfGoods partnerGoods = (PaHalfGoods) target.getPartnerGoods();
		Optional<PaOriginMapping> optional = originMappingRepository
				.findById(new PaOriginId(partnerGoods.getPaGroupCode(), originCode));
		if (optional.isPresent()) {
			partnerGoods.setOriginCode(optional.get().getPaOriginCode());
		}
		return true;
	}



	// 재입점
	private boolean syncForSale(PaGoodsTarget target) {

		PaHalfGoods partnerGoods = (PaHalfGoods) target.getPartnerGoods();

		// 입점완료상태이면서 판매중지 상태인 경우 재입점 처리
		// 사용자가 연동해제한 경우 auto_yn이 0으로 설정되어 자동재입점 방지
		if (PaStatus.COMPLETE.code().equals(partnerGoods.getPaStatus())
				&& PaSaleGb.SUSPEND.code().equals(partnerGoods.getPaSaleGb()) && "1".equals(target.getAutoYn())) {
			String syncNote = "상품 재입점";
			Timestamp currentDate = commonRepository.currentDate();

			partnerGoods.setTransSaleYn	("1");
			partnerGoods.setPaSaleGb	(PaSaleGb.FORSALE.code());
			partnerGoods.setModifyId	(Application.ID.code());
			partnerGoods.setModifyDate	(currentDate);
			partnerGoodsRepository.save	(partnerGoods);
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
		PaHalfGoods partnerGoods = (PaHalfGoods) target.getPartnerGoods();

		if (goods.getPaGoods().getLastSyncDate().after(partnerGoods.getLastSyncDate())) {
			//ORIGIN_CODE
			mappingOriginCode(goods.getOriginCode(), target);
			//GOODS_NAME
			partnerGoods.setGoodsName(goods.getGoodsName());
			//하프클럽은 브랜드 특이성이 있어서 브랜드 세팅을 연동배치에서 실시
			String syncNote = "상품정보변경";
			log.info("{} {} 상품: {} ", tag, syncNote, goods.getGoodsCode());
			logSync(CdcReason.GOODS_MODIFY.code(), syncNote, target);
			return true;
		} else if (goods.getPaGoods().getLastDescribeSyncDate().after(partnerGoods.getLastSyncDate())) { // 기술서 변경되면 연동타겟으로 설정
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

		log.info("하프클럽 판매중지 상품: {} ({})", goodsCode, result);

		return result;
	}

	// 제휴필터에서 호출
	public boolean stopSale(PaGoodsTarget target) {
		Optional<PaHalfGoods> optional = partnerGoodsRepository
				.findById(new PaGoodsId(target.getPaGroupCode(), target.getPaCode(), target.getGoodsCode()));

		if (!optional.isPresent())
			return false;

		PaHalfGoods partnerGoods = optional.get();

		Timestamp currentDate = commonRepository.currentDate();

		// 입점완료이고, 판매중인 경우 판매중단연동타겟팅 및 이력생성
		if (PaStatus.COMPLETE.code().equals(partnerGoods.getPaStatus())
				&& PaSaleGb.FORSALE.code().equals(partnerGoods.getPaSaleGb())) {

			partnerGoods.setPaSaleGb(PaSaleGb.SUSPEND.code());
			partnerGoods.setTransSaleYn("1");
			partnerGoods.setModifyDate(currentDate);
			partnerGoods.setModifyId(Application.ID.code());
			partnerGoodsRepository.save(partnerGoods);

			PaSaleNoGoods saleNo = PaSaleNoGoods.builder()
					.paGroupCode(target.getPaGroupCode())
					.paCode(target.getPaCode())
					.goodsCode(target.getGoodsCode())
					.seqNo(saleNoGoodsRepository.getNextSeq(target.getPaGroupCode(), target.getPaCode(),target.getGoodsCode()))
					.paGoodsCode(partnerGoods.getProductNo())
					.paSaleGb(PaSaleGb.SUSPEND.code())
					.note(target.getExceptNote())
					.insertId(Application.ID.code())
					.insertDate(currentDate)
					.build();
			saleNoGoodsRepository.save(saleNo);

			String syncNote = "제휴필터 판매중지";
			log.info("{} 상품: {} ", syncNote, target.getGoodsCode());
			logSync(CdcReason.SALE_END.code(), syncNote, target);

			return true;

		}else if(PaStatus.COMPLETE.code().compareTo(partnerGoods.getPaStatus()) > 0
				&& PaSaleGb.FORSALE.code().equals(partnerGoods.getPaSaleGb())) {

			// 입점전 판매중 데이터가 존재하는 경우 필터메시지와 판매중단 업데이트 처리
			partnerGoods.setPaStatus(PaStatus.REJECT.code());
			partnerGoods.setPaSaleGb(PaSaleGb.SUSPEND.code());
			partnerGoods.setReturnNote(target.getExceptNote());
			partnerGoods.setModifyDate(currentDate);
			partnerGoods.setModifyId(Application.ID.code());
			partnerGoodsRepository.save(partnerGoods);

			return true;
		}

		return false;
	}

	// 전송대상설정
	public boolean enableTransTarget(String goodsCode, String paCode) {
		int result = partnerGoodsRepository.enableTransTarget(goodsCode, paCode, Application.ID.code());

		log.info("하프클럽 전송대상 상품: {} ({})", goodsCode, result);

		return result > 0;
	}

	protected boolean syncShipCost(PaCustShipCost shipCost, PaGoodsTarget target) { //PartnerSyncService 부모 함수에 녹일지 말지 고민..
		String syncNote = "배송비 동기화";

		Optional<PaCustShipCost> optional = shipCostRepository
				.findById(new PaShipCostId(target.getPaCode(), shipCost.getEntpCode(), shipCost.getShipCostCode()));

		Timestamp currentDate = commonService.currentDate();
		boolean isDirty = false;

		PaCustShipCost targetCost = new PaCustShipCost();
		BeanUtils.copyProperties(shipCost, targetCost);

		if (optional.isPresent()) {
			isDirty = optional.get().getLastSyncDate().after(target.getPartnerGoods().getLastSyncDate());
			if (!shipCost.getApplyDate().after(optional.get().getLastSyncDate())) {
				if (isDirty) logSync(CdcReason.SHIP_COST_APPLY.code(), syncNote, target);
				return isDirty;
			}
			//하프클럽은 TPAHALFSHIPCOST에서 TRANS_TARGET_YN 관리, TPACUSTSHIPCOST에는 배송비 데이터 적재만 한다.
			paHalfShipInfoRepository.enableHalfShipInfoTransTarget(shipCost.getShipCostCode(), target.getPaCode(), shipCost.getEntpCode(), currentDate, Application.ID.code());

		} else {
			targetCost.setInsertId(Application.ID.code());
			targetCost.setInsertDate(currentDate);
		}

		targetCost.setPaCode		(target.getPaCode());
		targetCost.setTransTargetYn	("0");  //하프클럽은 TPACUSTSHIPCOST의 타겟을 관리하지 않는다.
		targetCost.setLastSyncDate	(currentDate);
		targetCost.setModifyId		(Application.ID.code());
		targetCost.setModifyDate	(currentDate);

		try {
			shipCostRepository.saveAndFlush(targetCost);
		} catch (DataIntegrityViolationException ex) {
			log.info("배송비정책이 이미 생성되었습니다. 상품:{}, 제휴사:{}", target.getGoodsCode(), target.getPaCode());
		}
		logSync(CdcReason.SHIP_COST_APPLY.code(), syncNote, target);
		return true;
	}



	// 단품/재고 동기화
	protected boolean syncGoodsDt(List<GoodsDt> goodsDtList, PaGoodsTarget target) {
		String syncNote = "단품 동기화";

		Timestamp currentDate = commonService.currentDate();
		boolean isSync = false;
		double stockRate = codeService.getStockRate(target.getPaCode());


		for (GoodsDt goodsDt : goodsDtList) {
			PaGoodsDt paGoodsDt = goodsDt.getPaGoodsDt();

			if (paGoodsDt == null)
				continue;

			Optional<PaHalfGoodsDtMapping> optional = goodsDtRepository.findGoodsDtMapping(target.getPaCode(), target.getGoodsCode(), goodsDt.getGoodsdtCode());

			PaHalfGoodsDtMapping goodsDtMapping;
			int transOrderAbleQty = SaleGb.FORSALE.code().equals(goodsDt.getSaleGb()) ?
					(int) Math.ceil(goodsDt.getOrderAbleQty() * stockRate) : 0;

			if (optional.isPresent()) {
				goodsDtMapping = optional.get();

				// 단품명이 바뀌면 기존 단품 매핑 비활성화 후 이력 생성
				if (!goodsDt.getGoodsdtInfo().equals(goodsDtMapping.getGoodsdtInfo())) {
					isSync = true;
					goodsDtMapping.setUseYn("0");
					goodsDtMapping.setModifyDate(currentDate);
					goodsDtMapping.setModifyId(Application.ID.code());
					goodsDtRepository.save(goodsDtMapping);

					goodsDtMapping = PaHalfGoodsDtMapping.builder()
							.goodsCode(target.getGoodsCode()).paCode(target.getPaCode())
							.goodsdtCode(paGoodsDt.getGoodsdtCode())
							.goodsdtSeq(goodsDtRepository.getNextSeq(target.getPaCode(), target.getGoodsCode(), paGoodsDt.getGoodsdtCode()))
							.goodsdtInfo(goodsDt.getGoodsdtInfo())
							.transOrderAbleQty(transOrderAbleQty)
							.useYn("1")
							.insertDate(currentDate).insertId(Application.ID.code())
							.modifyDate(currentDate).modifyId(Application.ID.code())
							.build();
					goodsDtRepository.save(goodsDtMapping);
					logSync(CdcReason.GOODSDT_MODIFY.code(), syncNote, target);

				} else if (transOrderAbleQty != goodsDtMapping.getTransOrderAbleQty()) {
					if(transOrderAbleQty < 1  || goodsDtMapping.getTransOrderAbleQty() == 0) {
						isSync = true; // 단품이 중지되거나 살아나는 경우 -> 즉시  TPAHALFGOODS.TRANS_TARGET_YN =  1
						goodsDtMapping.setTransOrderAbleQty(transOrderAbleQty);
						goodsDtMapping.setModifyDate(currentDate);
						goodsDtMapping.setModifyId(Application.ID.code());
						goodsDtRepository.save(goodsDtMapping);
					}
					/* CDC에서 재고변경에 대해서 TORDERSTOCK.MODIFY_DATE와 TPACDCREASON.LAST_CDC_DATE를 비교해서 변경을 감지하는데
					제휴가 아닌 타 매체(예를들어 BO)에서 주문 인입 해서 TORDERSTOCK 변경할때 TORDERSTOCK.MODIFY_DATE를 업데이트 하지 않음을 발견,
					해당 룰이 변경되면 하프클럽 재고 연동도 동기화에서 진행하도록 변경 필요.
					연동배치의 FUN_GET_ORDER_ABLE_QTY 조건을 TRANS_STOCK_YN이나 TRANS_SALE_YN으로 수정 필요
					*/
				}

			} else {
				isSync = true;
				goodsDtMapping = PaHalfGoodsDtMapping.builder().goodsCode(target.getGoodsCode()).paCode(target.getPaCode())
						.goodsdtCode(paGoodsDt.getGoodsdtCode())
						.goodsdtSeq("001")
						.goodsdtInfo(goodsDt.getGoodsdtInfo())
						.transOrderAbleQty(transOrderAbleQty)
						.useYn("1")
						.insertDate(currentDate).insertId(Application.ID.code())
						.modifyDate(currentDate).modifyId(Application.ID.code())
						.build();
				goodsDtRepository.save(goodsDtMapping);
			}


		}
		return isSync; // 변경동기화 여부
	}


	/*
	protected boolean syncShipCostTemp(PaCustShipCost shipCost, PaGoodsTarget target) {
		//syncShipCost -> TPACUSTSHIPCOST의 각종 VALUE 및 TARGET_YN UPDATE/INSERT
		if(super.syncShipCost(shipCost, target)) {
			Timestamp currentDate = commonService.currentDate();
			Pageable pageable = PageRequest.of(0, 1000, Sort.by("entpCode"));

			Slice<String> slice = paHalfShipInfoRepository.findTransTargetList(target.getPaCode(), shipCost.getEntpCode(), pageable);
			List<String> targetList = slice.getContent();

			if(targetList.size() > 0) {
				//TPAHALFSHIPINFO.TRANS_TARGET_YN = 1
				paHalfShipInfoRepository.enableHalfShipInfoTransTarget(targetList, target.getPaCode(), shipCost.getEntpCode(), currentDate, Application.ID.code());

				//TPACUSTSHIPCOST.TRANS_TARGET_YN = 0 , 이부분이 마음에 들지 않음
				PaCustShipCost targetCost = new PaCustShipCost();
				BeanUtils.copyProperties(shipCost, targetCost);
				targetCost.setPaCode		(target.getPaCode());
				targetCost.setTransTargetYn	("0");
				targetCost.setLastSyncDate	(currentDate);
				targetCost.setModifyId		(Application.ID.code());
				targetCost.setModifyDate	(currentDate);
				shipCostRepository.saveAndFlush(targetCost);
			}
		}
		return true;
	}*/

}
