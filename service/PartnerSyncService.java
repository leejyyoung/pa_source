package com.cware.partner.sync.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cware.partner.common.code.Application;
import com.cware.partner.common.code.CdcReason;
import com.cware.partner.common.code.PaGroup;
import com.cware.partner.common.code.PaSaleGb;
import com.cware.partner.common.service.CategoryService;
import com.cware.partner.common.service.CodeService;
import com.cware.partner.common.service.CommonService;
import com.cware.partner.common.util.StringUtil;
import com.cware.partner.sync.domain.TargetExcept;
import com.cware.partner.sync.domain.entity.EntpUser;
import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.GoodsDt;
import com.cware.partner.sync.domain.entity.GoodsImage;
import com.cware.partner.sync.domain.entity.GoodsPrice;
import com.cware.partner.sync.domain.entity.PaCustShipCost;
import com.cware.partner.sync.domain.entity.PaGoodsDt;
import com.cware.partner.sync.domain.entity.PaGoodsDtMapping;
import com.cware.partner.sync.domain.entity.PaGoodsImage;
import com.cware.partner.sync.domain.entity.PaGoodsKindsMapping;
import com.cware.partner.sync.domain.entity.PaGoodsOffer;
import com.cware.partner.sync.domain.entity.PaGoodsPrice;
import com.cware.partner.sync.domain.entity.PaGoodsPriceApply;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;
import com.cware.partner.sync.domain.entity.PaNoticeApply;
import com.cware.partner.sync.domain.entity.PaNoticeTarget;
import com.cware.partner.sync.domain.entity.PaOfferCodeMapping;
import com.cware.partner.sync.domain.entity.PaPromoAddInfo;
import com.cware.partner.sync.domain.entity.PaPromoGoodsPrice;
import com.cware.partner.sync.domain.entity.PaPromoMarginExcept;
import com.cware.partner.sync.domain.entity.PaPromoTarget;
import com.cware.partner.sync.domain.entity.PartnerGoods;
import com.cware.partner.sync.domain.id.EntpUserId;
import com.cware.partner.sync.domain.id.PaGoodsDtId;
import com.cware.partner.sync.domain.id.PaGroupGoodsId;
import com.cware.partner.sync.domain.id.PaLmsdId;
import com.cware.partner.sync.domain.id.PaOfferId;
import com.cware.partner.sync.domain.id.PaShipCostId;
import com.cware.partner.sync.repository.EntpUserRepository;
import com.cware.partner.sync.repository.PaCustShipCostRepository;
import com.cware.partner.sync.repository.PaGoodsDtMappingRepository;
import com.cware.partner.sync.repository.PaGoodsImageRepository;
import com.cware.partner.sync.repository.PaGoodsKindsMappingRepository;
import com.cware.partner.sync.repository.PaGoodsOfferRepository;
import com.cware.partner.sync.repository.PaGoodsPriceApplyRepository;
import com.cware.partner.sync.repository.PaGoodsPriceRepository;
import com.cware.partner.sync.repository.PaGoodsTargetRepository;
import com.cware.partner.sync.repository.PaNoticeApplyRepository;
import com.cware.partner.sync.repository.PaOfferCodeMappingRepository;
import com.cware.partner.sync.repository.PaPromoAddInfoRepository;
import com.cware.partner.sync.repository.PaPromoGoodsPriceRepository;
import com.cware.partner.sync.repository.PaPromoMarginAutoMRepository;
import com.cware.partner.sync.repository.PaPromoMarginExceptRepository;
import com.cware.partner.sync.repository.PaPromoMinMarginRateRepository;
import com.cware.partner.sync.repository.PaPromoTargetRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author sun
 *
 */
@Slf4j
@Service
public class PartnerSyncService {

	@PersistenceContext
    EntityManager entityManager;

	@Autowired
	PaGoodsKindsMappingRepository paGoodsKindsMappingRepository;
	@Autowired
	PaOfferCodeMappingRepository paOfferCodeMappingRepository;

	@Autowired
	PaGoodsImageRepository imageRepository;

	@Autowired
	PaGoodsDtMappingRepository goodsDtRepository;

	@Autowired
	PaGoodsOfferRepository goodsOfferRepository;

	@Autowired
	PaCustShipCostRepository shipCostRepository;

	@Autowired
	PaPromoTargetRepository promoRepository;

	@Autowired
	PaPromoGoodsPriceRepository promoPriceRepository;

	@Autowired
	PaGoodsTargetRepository targetRepository;

	@Autowired
	EntpUserRepository entpUserRepository;

	@Autowired
	PaPromoMinMarginRateRepository paPromoMinMarginRateRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	CodeService codeService;
	
	@Autowired
	CategoryService categoryService;

	@Autowired
	private PaGoodsPriceRepository paGoodsPriceRepository;

	@Autowired
	private PaGoodsPriceApplyRepository priceApplyRepository;

	@Autowired
	private PaNoticeApplyRepository noticeApplyRepository;

	@Autowired
	private PaPromoMarginExceptRepository paPromoMarginExceptRepository;
	
	@Autowired
	private PaPromoMarginAutoMRepository paPromoMarginAutoMRepository;
	
	@Autowired
	private PaPromoAddInfoRepository paPromoAddInfoRepository;

	String tag = "제휴사별 공통 동기화";

	public boolean syncProduct(Goods goods, PaGoodsTarget target) {

		log.info("{} -------> {}", tag, target);

		// 정보고시 동기화 쿠팡의 경우 별도처리를 해야하기 때문에 제휴사별 처리로 이관
//		if (!syncGoodsOffer(goods.getOfferType(), target)) return false;

		return true;
	}

	// 제휴사별 표준카테고리
	protected String getLmsdKey(String lmsdCode, PaGoodsTarget target) {
		String tag = "표준카테고리";
			PaGoodsKindsMapping lmsd = paGoodsKindsMappingRepository.getById(new PaLmsdId(target.getPaGroupCode(), lmsdCode));
		try {
			return lmsd.getPaLmsdKey();
		} catch (EntityNotFoundException e) {
			target.setExcept(true);
			target.setExceptNote("제휴사 표준카테고리가 존재하지 않습니다.");
			log.info("{}: {} [상품:{} 제휴사:{}]", tag, target.getExceptNote(), target.getGoodsCode(), target.getPaGroupCode());
			logFilter("SYNC-LMSD_CODE", target);
			return null;
		}

	}

	// 제휴사별 정보고시코드 (미사용)
	protected PaOfferCodeMapping getOfferCode(String paGroupCode, String offerType, String offerCode) {

		PaOfferCodeMapping offerMapping = paOfferCodeMappingRepository
				.getById(new PaOfferId(paGroupCode, offerType, offerCode));

		return offerMapping;

	}

	// 상품가격
	protected void syncGoodsPrice(GoodsPrice price, String lmsdCode, PaGoodsTarget target, boolean isIdentical) {
		String syncNote = "가격 동기화";

		Timestamp currentDate = commonService.currentDate();

		double salePrice = price.getSalePrice();
		double dcAmt = price.getArsDcAmt() + price.getLumpSumDcAmt();
		double commision = 0;
		
		if(target.getPaGroupCode().equals(PaGroup.COUPANG.code())) { //쿠팡 카테고리 수수료율 적용
			Double catCommission = categoryService.getCopnCatCommission(lmsdCode);
			commision = (catCommission == null) ? target.getPartnerBase().getCommission() : catCommission;
		}else {
			commision = codeService.getCommision(target.getPaCode());			
		}
		
		double supplyPrice = calcSupplyPrice(salePrice, dcAmt, commision, target.getPaGroupCode());

		PaGoodsPrice partnerPrice = PaGoodsPrice.builder().goodsCode(target.getGoodsCode()).paCode(target.getPaCode())
				.applyDate(price.getApplyDate()).priceSeq(price.getPriceSeq()).salePrice(salePrice)
				.dcAmt(price.getArsDcAmt())
				.commision(commision).supplyPrice(supplyPrice)
				.lumpSumDcAmt(price.getLumpSumDcAmt())
				.lumpSumEntpDcAmt(price.getLumpSumEntpDcAmt())
				.lumpSumOwnDcAmt(price.getLumpSumOwnDcAmt())
				.insertId(Application.ID.code()).insertDate(currentDate)
				.modifyId(Application.ID.code()).modifyDate(currentDate).build();

		if(isIdentical) {
			partnerPrice.setTransDate(currentDate);
			partnerPrice.setTransId(Application.ID.code());
		}

		paGoodsPriceRepository.save(partnerPrice);

		logSync(CdcReason.PRICE_APPLY.code(), syncNote, target);

	}

	protected void syncGoodsPrice(GoodsPrice price, String lmsdCode, PaGoodsTarget target) {
		syncGoodsPrice(price, lmsdCode, target, false);
	}

	// 상품가격변경
	protected boolean syncGoodsPriceMoidfy(GoodsPrice price, String lmsdCode, PaGoodsTarget target) {
		Optional<PaGoodsPrice> optional = paGoodsPriceRepository.findApplyGoodsPrice(target.getPaCode(), target.getGoodsCode());
		boolean isIdentical = false;

		if (optional.isPresent()) {
			PaGoodsPrice paPrice = optional.get();
			if (paPrice.getApplyDate().equals(price.getApplyDate())) {
				return false;
			}
			// 실제 가격정보가 변경되었는지 체크하여 변경되지 않은 경우 전송된걸로 처리
			if (paPrice.getTransDate() != null &&  paPrice.getSalePrice() == price.getSalePrice() && paPrice.getDcAmt() == price.getArsDcAmt()
					&& paPrice.getLumpSumDcAmt() == price.getLumpSumDcAmt()
					&& paPrice.getLumpSumOwnDcAmt() == price.getLumpSumOwnDcAmt()
					&& paPrice.getLumpSumEntpDcAmt() == price.getLumpSumEntpDcAmt()) {
				isIdentical = true;
			}
		}
		syncGoodsPrice(price, lmsdCode, target, isIdentical);
		return true;
	}

	private double calcSupplyPrice(double salePrice, double dcAmt, double commision, String paGroupCode) {

		// 11번가, 쿠팡, 위메프면 할인금액 적용 먼저 적용 후 수수료율 반영,
		// 그외에는 할인 적용 전 가격에 수수료율 반영 후 할인액 반영
		if (paGroupCode.equals(PaGroup.SK11ST.code())
				|| paGroupCode.equals(PaGroup.COUPANG.code())
				|| paGroupCode.equals(PaGroup.WEMP.code())) {
			return Math.round((salePrice - dcAmt) * ((100 - commision) / 100.0));
		}
		return  Math.round((salePrice) * ((100 - commision) / 100.0) - dcAmt);
	}

	// 상품이미지 저장
	protected boolean syncGoodsImage(GoodsImage image, PaGoodsTarget target) {
		String syncNote = "이미지 동기화";

		Timestamp currentDate = commonService.currentDate();

		Optional<PaGoodsImage> optional = imageRepository
				.findById(new PaGroupGoodsId(target.getPaGroupCode(), target.getGoodsCode()));

		PaGoodsImage goodsImage;

		if (optional.isPresent()) {
			goodsImage = optional.get();
		} else {
			goodsImage = PaGoodsImage.builder()
					.goodsCode(target.getGoodsCode())
					.paGroupCode(target.getPaGroupCode())
					.insertDate(currentDate)
					.insertId(Application.ID.code())
					.build();
		}

		if (goodsImage.getLastSyncDate() == null ||
				image.getModifyDate().after(goodsImage.getLastSyncDate())) {
			goodsImage.setImageP(image.getImageC() == null ? image.getImageG() : image.getImageC());
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
			logSync(CdcReason.IMAGE_MODIFY.code(), syncNote, target);
			return true;
		}

		return false;
	}


	// 상품재고동기화
	protected boolean syncGoodsStock(Goods goods, PaGoodsTarget target) {

		PartnerGoods partnerGoods = target.getPartnerGoods();

		// 재고변동 체크
		if (partnerGoods.getTransOrderAbleQty() != target.getPartnerStockQty()) {
			String syncNote = "재고변경";
			log.info("{} 상품: {} ", syncNote, goods.getGoodsCode() );
			partnerGoods.setTransOrderAbleQty(target.getPartnerStockQty());
			logSync(CdcReason.STOCK_CHANGE.code(), syncNote, target);
			return true;
		}

		return false;
	}

	// 단품/재고 동기화
	protected boolean syncGoodsDt(List<GoodsDt> goodsDtList, PaGoodsTarget target) {
		String syncNote = "단품 동기화";

		Timestamp currentDate = commonService.currentDate();
		boolean isSync = false;

		for (GoodsDt goodsDt : goodsDtList) {
			PaGoodsDt paGoodsDt = goodsDt.getPaGoodsDt();

			if (paGoodsDt == null)
				continue;

			Optional<PaGoodsDtMapping> optional = goodsDtRepository
					.findById(new PaGoodsDtId(target.getPaCode(), target.getGoodsCode(), goodsDt.getGoodsdtCode()));


			PaGoodsDtMapping goodsDtMapping;

			double stockRate = target.getPartnerBase().getAbleStockRate();
			int transOrderAbleQty = goodsDt.getOrderAbleQty() > 0 ?
					(int) Math.ceil(goodsDt.getOrderAbleQty() * stockRate) : 0;

			if (optional.isPresent()) {
				goodsDtMapping = optional.get();

				boolean isDirty = false;
				boolean isStock = false;

				// 단품정보변경
				if(goodsDt.getModifyDate().after(goodsDtMapping.getLastSyncDate())) {
					isDirty = true;
				}

				// 재고변경
				if (transOrderAbleQty != goodsDtMapping.getTransOrderAbleQty()) {
					// 이베이의 경우 단품 재고 유무가 변경되는 경우 상품 타겟팅 (품절이거나 재고가 증가할 때)
					// (transOrderAbleQty == 0 || transOrderAbleQty > goodsDtMapping.getTransOrderAbleQty())
					// 이베이 상품수정API 최근 안정화되어 재고변경시 상품 타겟팅 2022.12.22
					// 네이버 V3는 옵션재고 수정 API가 없고 상품수정 API로 사용해야됨 2023.07.27
					if (target.getPaGroupCode().equals(PaGroup.GMARKET.code())
							|| target.getPaGroupCode().equals(PaGroup.AUCTION.code())
							|| target.getPaGroupCode().equals(PaGroup.NAVER.code())) {
						isDirty = true;
					}
					goodsDtMapping.setTransStockYn("1");
					goodsDtMapping.setTransOrderAbleQty(transOrderAbleQty);
					isStock = true;
				}

				if (isDirty) {
					isSync = true;
					goodsDtMapping.setTransTargetYn("1");
					goodsDtMapping.setLastSyncDate(currentDate);
					goodsDtMapping.setModifyDate(currentDate);
					goodsDtMapping.setModifyId(Application.ID.code());
					goodsDtRepository.save(goodsDtMapping);
					logSync(CdcReason.GOODSDT_MODIFY.code(),
							syncNote + "[" + goodsDt.getGoodsdtCode() + "]" + (isStock ? "재고" : ""), target);
				} else if (isStock) { // 재고변경만
					goodsDtMapping.setLastSyncDate(currentDate);
					goodsDtMapping.setModifyDate(currentDate);
					goodsDtMapping.setModifyId(Application.ID.code());
					goodsDtRepository.save(goodsDtMapping);
					logSync(CdcReason.STOCK_CHANGE.code(), "재고동기화["+goodsDt.getGoodsdtCode()+"]", target);
				}
			} else {
				isSync = true;

				String combinationYn = "0";

				if (target.getPaGroupCode().equals(PaGroup.GMARKET.code())
						|| target.getPaGroupCode().equals(PaGroup.AUCTION.code())) {
					combinationYn = goodsDtRepository.getCombinationYn(target.getGoodsCode());
				}

				goodsDtMapping = PaGoodsDtMapping.builder()
						.goodsCode(target.getGoodsCode())
						.paCode(target.getPaCode())
						.goodsdtCode(paGoodsDt.getGoodsdtCode())
						.transOrderAbleQty(transOrderAbleQty)
						.transTargetYn("1")
						.combinationYn(combinationYn)
						.lastSyncDate(currentDate)
						.insertDate(currentDate)
						.insertId(Application.ID.code())
						.modifyDate(currentDate)
						.modifyId(Application.ID.code())
						.build();

				goodsDtRepository.save(goodsDtMapping);
				logSync(CdcReason.GOODSDT_MODIFY.code(), syncNote+"["+goodsDt.getGoodsdtCode()+"]", target);
			}
		}


		return isSync; // 변경동기화 여부
	}

	// 정보고시동기화
	protected boolean syncGoodsOffer(String offerType, PaGoodsTarget target) {
		String tag = "정보고시 동기화";

		// 한 상품이 한 제휴사의 여러 제휴코드에 입점되지 않기 때문에 체크 불필요
//		// 제휴그룹별 생성이기 때문에 이전에 이미 생성되었으면 처리하지 않음
//		if (goodsOfferRepository.findOneByGoodsCodeAndPaGroupCode(
//				target.getPaGroupCode(), target.getGoodsCode()).isPresent()) return true;

		String paGroupCode = target.getPaGroupCode().equals(PaGroup.AUCTION.code()) ? PaGroup.GMARKET.code()
				: target.getPaGroupCode();
		Timestamp currentDate = commonService.currentDate();

		List<PaGoodsOffer> list = goodsOfferRepository
				.selectGoodsOffer(paGroupCode, target.getGoodsCode(), offerType);
		if (list.isEmpty()) {
			target.setExcept(true);
			target.setExceptNote("제휴사 정보고시 매핑 데이터가 없습니다.");
			log.info("{}: {} [상품:{} 제휴사:{}]", tag, target.getExceptNote(), target.getGoodsCode(), target.getPaGroupCode());
			logFilter("SYNC-GOODS_OFFER", target);
			return false;
		}

		for (PaGoodsOffer offer : list) {
			offer.setGoodsCode(target.getGoodsCode());
			offer.setUseYn("1");
			offer.setTransTargetYn("1");
			offer.setLastSyncDate(currentDate);
			offer.setInsertId(Application.ID.code());
			offer.setInsertDate(currentDate);
			offer.setModifyId(Application.ID.code());
			offer.setModifyDate(currentDate);
		}
		log.info("{}: [상품:{} 제휴사:{}]", tag, target.getGoodsCode(), target.getPaGroupCode());
		goodsOfferRepository.saveAll(list);
		return true;
	}

	// 정보고시동기화
	protected boolean syncGoodsOfferModify(String offerType, PaGoodsTarget target) {
		String syncNote = "정보고시변경 동기화";

		boolean isDirty;

		Timestamp currentDate = commonService.currentDate();

		String paGroupCode = target.getPaGroupCode().equals(PaGroup.AUCTION.code()) ? PaGroup.GMARKET.code() : target.getPaGroupCode();

		// 이전 동기화된 offerType
		String syncOfferType = goodsOfferRepository.getOfferType(paGroupCode, target.getGoodsCode());

		if (StringUtil.compare(offerType, syncOfferType)) {
			List<PaGoodsOffer> list = goodsOfferRepository.selectGoodsOfferModify(paGroupCode, target.getGoodsCode());

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
		} else {
			// 이전 정보고시유형과 달라졌을 경우 기존 정보고시 비활성화 후 카테고리 매핑하여 새로 생성
			goodsOfferRepository.disableGooodsOffer(paGroupCode, target.getGoodsCode(), Application.ID.code());
			syncGoodsOffer(offerType, target);
			isDirty = true;
		}

		if (isDirty) {
			logSync(CdcReason.OFFER_MODIFY.code(), syncNote, target);
			return true;
		}
		return false;
	}

	// 고객부담배송비
	protected boolean syncShipCost(PaCustShipCost shipCost, PaGoodsTarget target) {
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
		} else {
			targetCost.setInsertId(Application.ID.code());
			targetCost.setInsertDate(currentDate);
		}

		targetCost.setPaCode(target.getPaCode());
		targetCost.setTransTargetYn("1");
		targetCost.setLastSyncDate(currentDate);
		targetCost.setModifyId(Application.ID.code());
		targetCost.setModifyDate(currentDate);

		try {
			shipCostRepository.saveAndFlush(targetCost);
		} catch (DataIntegrityViolationException ex) {
			log.info("배송비정책이 이미 생성되었습니다. 상품:{}, 제휴사:{}", target.getGoodsCode(), target.getPaCode());
		}
		logSync(CdcReason.SHIP_COST_APPLY.code(), syncNote, target);
		return true;
	}

	// 프로모션 동기화
	protected void syncPromoTarget(Goods goods, PaGoodsTarget target) {
		String syncNote = "프로모션 동기화";

		GoodsPrice goodsPrice =  goods.getGoodsPrice();
		Timestamp currentDate = commonService.currentDate();
		
		double commission = 0;
		
		if(target.getPaGroupCode().equals(PaGroup.COUPANG.code())) { //쿠팡 카테고리 수수료율 적용
			Double catCommission = categoryService.getCopnCatCommission(goods.getLmsdCode());
			commission = (catCommission == null) ? target.getPartnerBase().getCommission() : catCommission;
		}else {
			commission = target.getPartnerBase().getCommission();			
		}

		// 최종가격 설정
		PaGoodsPriceApply priceApply = PaGoodsPriceApply.builder().goodsCode(target.getGoodsCode())
				.paGroupCode(target.getPaGroupCode())
				.paCode(target.getPaCode())
				.applyDate(goodsPrice.getApplyDate())
				.priceApplySeq(1)
				.priceSeq(goodsPrice.getPriceSeq())
				.salePrice(goodsPrice.getSalePrice())
				.arsDcAmt(goodsPrice.getArsDcAmt())
				.arsOwnDcAmt(goodsPrice.getArsOwnDcAmt())
				.arsEntpDcAmt(goodsPrice.getArsEntpDcAmt())
				.lumpSumDcAmt(goodsPrice.getLumpSumDcAmt())
				.lumpSumOwnDcAmt(goodsPrice.getLumpSumOwnDcAmt())
				.lumpSumEntpDcAmt(goodsPrice.getLumpSumEntpDcAmt())
				.commission(commission)
				.insertId(Application.ID.code())
				.insertDate(currentDate)
				.build();

		List<PaPromoTarget> promoList = goods.getPaPromoTargetList();

		// 제휴사별 프로모션 연동 제외
		TargetExcept promoExcept = goods.getPromoTargetExcept();
		TargetExcept promoEntpBrandExcept = goods.getPromoEntpBrandExcept();

		 if (promoEntpBrandExcept != null &&   StringUtils.hasLength(promoEntpBrandExcept.getPaGroupCode())
				&& promoEntpBrandExcept.getPaGroupCode().contains(target.getPaGroupCode())) {
			log.info("프로모션 동기화: 연동 제외 [상품코드:{}, 제휴사:{}]({})", target.getGoodsCode(), target.getPaGroupCode(), target.getGoodsSyncNo());

		} else if (promoList != null){

			double ownPrice, marginRate, exceptCheckPrice;
			boolean isMarginPass = false;
			boolean isExceptMarginPass = false;

			// 프로모션 마진 체크 예외 상품여부
			PaPromoMarginExcept promoMarginPass = paPromoMarginExceptRepository.findByGoodsCodeAndPaGroupCodeAndUseYn(target.getGoodsCode(), target.getPaGroupCode(), "1");

			for (PaPromoTarget promo : promoList) {
				if (!promo.getMediaCode().contains(target.getMediaCode())) continue;

				PaPromoTarget targetPromo = new PaPromoTarget();
				BeanUtils.copyProperties(promo, targetPromo);

				targetPromo.setPaCode(target.getPaCode());
				targetPromo.setPaGroupCode(target.getPaGroupCode());
				targetPromo.setSeq(promoRepository.getNextSeq(targetPromo.getPromoNo(), targetPromo.getPaCode(), targetPromo.getGoodsCode()));
				targetPromo.setProcGb("I");
				targetPromo.setInsertId(Application.ID.code());
				targetPromo.setInsertDate(currentDate);
				targetPromo.setModifyId(Application.ID.code());
				targetPromo.setModifyDate(currentDate);
				promoRepository.saveAndFlush(targetPromo);

				logSync(CdcReason.PROMO_APPLY.code(), syncNote, target);
				
				Double promoMinMarginRate = null;
				//프로모션 실마진 허들 자동화에 등록된 행사 우선적용
				Double promoMinMarginRateAuto = paPromoMarginAutoMRepository.getPromoMarginAuto(target.getGoodsCode(),target.getMediaCode(),target.getPaCode());				
				promoMinMarginRate = (promoMinMarginRateAuto == null) ? codeService.getPromoMinMarginRate(target.getPaCode()) : promoMinMarginRateAuto;

				// 프로모션 마진체크 예외대상이 아닌경우 마진 계산
				if (promoMarginPass == null && !"1".equals(targetPromo.getPaMarginExceptYn()) ) {
					ownPrice = goodsPrice.getSalePrice() - goodsPrice.getArsOwnDcAmt() - goodsPrice.getLumpSumOwnDcAmt() - promo.getOwnCost();
					// 현업요청으로 마진 기준금액 판매가로 변경, 프로모션마진 소수점이하 버림
					marginRate = ownPrice > 0 ? Math.floor((ownPrice - goodsPrice.getBuyPrice()) / goodsPrice.getSalePrice() * 100) : 0;
					isMarginPass = marginRate >= promoMinMarginRate;
				} else {
					isMarginPass = true;
				}

				// 프로모션 예외상품 마진률 체크
				if(promoExcept != null) {
					if (promoExcept.getPaGroupCode().contains(target.getPaGroupCode())) {
						exceptCheckPrice = goodsPrice.getSalePrice() - goodsPrice.getArsDcAmt() - goodsPrice.getLumpSumDcAmt() - promo.getDoAmt();
						isExceptMarginPass = exceptCheckPrice >= promoExcept.getPaExceptMargin() ;
					}else {
						isExceptMarginPass = true;
					}

				} else {
					isExceptMarginPass = true;
				}


				// 최종혜택가 설정
				if (isMarginPass && isExceptMarginPass &&
						(priceApply.getCouponDcAmt() < promo.getDoAmt()
								|| (priceApply.getCouponDcAmt() == promo.getDoAmt()
										&& priceApply.getCouponOwnCost() > promo.getOwnCost()))) {
					priceApply.setCouponPromoNo(promo.getPromoNo());
					priceApply.setCouponDcAmt(promo.getDoAmt());
					priceApply.setCouponOwnCost(promo.getOwnCost());
					priceApply.setCouponEntpCost(promo.getEntpCost());
				}
			}

		}

		double dcAmt = priceApply.getArsDcAmt() + priceApply.getLumpSumDcAmt() + priceApply.getCouponDcAmt();
		priceApply.setBestPrice(priceApply.getSalePrice() - dcAmt);
		priceApply.setSupplyPrice(calcSupplyPrice(priceApply.getSalePrice(), dcAmt, priceApply.getCommission(), target.getPaGroupCode()));
		
		double ownAmt = priceApply.getSalePrice() - priceApply.getArsOwnDcAmt() - priceApply.getLumpSumOwnDcAmt() - priceApply.getCouponOwnCost();
		double realMarginRate = Math.floor((ownAmt - goodsPrice.getBuyPrice())/priceApply.getSalePrice() * 100);
		priceApply.setMarginRate(realMarginRate);
		
		priceApplyRepository.save(priceApply);

		// 쓱닷컴 프로모션개선 별도 추가 처리 (PaGoodsPriceApply 사용하게되면 삭제예정)
		if (PaGroup.SSG.code().equals(target.getPaGroupCode())  && StringUtils.hasLength(priceApply.getCouponPromoNo())) {
			PaPromoGoodsPrice promoPrice = PaPromoGoodsPrice.builder()
					.goodsCode(target.getGoodsCode())
					.paCode(target.getPaCode())
					.applyDate(goodsPrice.getApplyDate())
					.promoSeq("0001")
					.alcoutPromoYn("1")
					.promoNo(priceApply.getCouponPromoNo())
					.doAmt(priceApply.getCouponDcAmt())
					.ownCost(priceApply.getCouponOwnCost())
					.entpCost(priceApply.getCouponEntpCost())
					.insertId(Application.ID.code())
					.insertDate(currentDate)
					.build();
			promoPriceRepository.save(promoPrice);

		}
	}

	// 프로모션변경 동기화
	protected boolean syncPromoModifyTarget(Goods goods, PaGoodsTarget target) {
		String syncNote = "프로모션변경 동기화";

		List<PaPromoTarget> promoList = goods.getPaPromoTargetList();
		TargetExcept promoExcept = goods.getPromoTargetExcept();
		TargetExcept promoBrandEntp = goods.getPromoEntpBrandExcept();
		boolean isDirty = false;

		List<PaPromoTarget> paPromoList = promoRepository.selectPaPromoTarget(target.getPaGroupCode(),
				target.getPaCode(), target.getGoodsCode());

		boolean isExcept = false;
		// 제휴사별 프로모션 연동 제외
		if (promoBrandEntp != null) {
			if (StringUtils.hasLength(promoBrandEntp.getPaGroupCode())) {
				if (promoBrandEntp.getPaGroupCode().contains(target.getPaGroupCode())) {
					log.info("프로모션변경 동기화: 연동 제외 [상품코드:{}, 제휴사:{}]", target.getGoodsCode(), target.getPaGroupCode());
					isExcept = true;
				}
			}
		}

		Optional<PaPromoTarget> promoTarget;
		Timestamp currentDate = commonService.currentDate();
		GoodsPrice goodsPrice = goods.getGoodsPrice();

		for (PaPromoTarget paPromo : paPromoList) {
			
			Optional<PaPromoAddInfo> optional = paPromoAddInfoRepository.findById(paPromo.getPromoNo());
			if (optional.isPresent()) {
				PaPromoAddInfo paPromoAddInfo = optional.get();
				paPromo.setTalkDealYn(paPromoAddInfo.getTalkDealYn());

			} else {
				paPromo.setTalkDealYn("0");
			}
			
			if (isExcept || promoList == null) {
				// 'D'가 아니면 'D'를 생성
				if (!"D".equals(paPromo.getProcGb())) {
					// 카카오 톡딜 프로모션일 경우 상품 판매중 처리, 프로모션 마진체크 예외 해제
					if("11".equals(target.getPaGroupCode()) && "1".equals(paPromo.getTalkDealYn())) {
						target.getPartnerGoods().setPaSaleGb("20");
						target.getPartnerGoods().setPaStatus("30");
						paPromoMarginExceptRepository.disablePromoMarginExcept(paPromo.getPromoNo(), paPromo.getGoodsCode(), PaGroup.KAKAO.code(), Application.ID.code());
					}
					isDirty = createPaPromoTarget(paPromo, "D", goodsPrice, currentDate);
					logSync(CdcReason.PROMO_PA_EXCEPT.code(), syncNote, target);
				}
			} else {
				promoTarget = promoList.stream().filter(promo -> promo.getMediaCode().contains(target.getMediaCode()) && promo.getPromoNo().equals(paPromo.getPromoNo())).findFirst();

				// 유효한 프로모션이 존재하는 경우
				if (promoTarget.isPresent()) {


					// 이전 데이터가 'D'이면 U로 신규 생성
					if ("D".equals(paPromo.getProcGb())) {
						PaPromoTarget targetPromo = new PaPromoTarget();
						BeanUtils.copyProperties(promoTarget.get(), targetPromo);
						targetPromo.setPaCode(target.getPaCode());
						targetPromo.setPaGroupCode(target.getPaGroupCode());
						isDirty = createPaPromoTarget(targetPromo, "U", goodsPrice, currentDate);
						logSync(CdcReason.PROMO_STATUS.code(), syncNote, target);
					} else {
						// 데이터 비교하여 다르면 U생성 ownCost, entpCost, useCode, promoEdate
						if (paPromo.getOwnCost() != promoTarget.get().getOwnCost()
								|| paPromo.getEntpCost() != promoTarget.get().getEntpCost()
								|| !paPromo.getUseCode().equals(promoTarget.get().getUseCode())
								|| !paPromo.getPromoEdate().equals(promoTarget.get().getPromoEdate())) {
							PaPromoTarget targetPromo = new PaPromoTarget();
							BeanUtils.copyProperties(promoTarget.get(), targetPromo);
							targetPromo.setPaCode(target.getPaCode());
							targetPromo.setPaGroupCode(target.getPaGroupCode());
							isDirty = createPaPromoTarget(targetPromo, "U", goodsPrice, currentDate);
							logSync(CdcReason.PROMO_STATUS.code(), syncNote, target);
						}

					}
				} else {
					// 'D'가 아니면 'D'를 생성
					if (!"D".equals(paPromo.getProcGb())) {
						// 카카오 톡딜 프로모션일 경우 상품 판매중 처리, 프로모션 마진체크 예외 해제
						if("11".equals(target.getPaGroupCode()) && "1".equals(paPromo.getTalkDealYn())) {
							target.getPartnerGoods().setPaSaleGb("20");
							target.getPartnerGoods().setPaStatus("30");
							paPromoMarginExceptRepository.disablePromoMarginExcept(paPromo.getPromoNo(), paPromo.getGoodsCode(), PaGroup.KAKAO.code(), Application.ID.code());
						}
						isDirty = createPaPromoTarget(paPromo, "D", goodsPrice, currentDate);
						logSync(CdcReason.PROMO_END.code(), syncNote, target);
					}
				}
			}
		}

		String promoNo = "";
		double doAmt = 0;
		double ownCost = 0;
		double entpCost = 0;
		boolean isTalkDeal = false; 
		Timestamp promoBdate = null;

		if (!isExcept && promoList != null) {

			double ownPrice, marginRate, exceptCheckPrice;
			boolean isMarginPass = false;
			boolean isExceptMarginPass = false;

			// 프로모션 마진 체크 예외 상품여부
			PaPromoMarginExcept promoMarginPass = paPromoMarginExceptRepository.findByGoodsCodeAndPaGroupCodeAndUseYn(target.getGoodsCode(), target.getPaGroupCode(), "1");

			for (PaPromoTarget promo : promoList) {

				if (!promo.getMediaCode().contains(target.getMediaCode())) continue;

				promoTarget = paPromoList.stream().filter(paPromo -> paPromo.getPromoNo().equals(promo.getPromoNo())).findFirst();
				if (!promoTarget.isPresent()) {
					PaPromoTarget targetPromo = new PaPromoTarget();
					BeanUtils.copyProperties(promo, targetPromo);
					targetPromo.setPaCode(target.getPaCode());
					targetPromo.setPaGroupCode(target.getPaGroupCode());
					isDirty = createPaPromoTarget(targetPromo, "I", goods.getGoodsPrice(), currentDate);
					logSync(CdcReason.PROMO_APPLY.code(), syncNote, target);
				}
				
				Double promoMinMarginRate = null;
				//프로모션 실마진 허들 자동화에 등록된 행사 우선적용
				Double promoMinMarginRateAuto = paPromoMarginAutoMRepository.getPromoMarginAuto(target.getGoodsCode(),target.getMediaCode(),target.getPaCode());				
				promoMinMarginRate = (promoMinMarginRateAuto == null) ? codeService.getPromoMinMarginRate(target.getPaCode()) : promoMinMarginRateAuto;
				
				// 프로모션 마진체크 예외대상이 아닌경우 마진 계산
				if  (promoMarginPass == null && !"1".equals(promo.getPaMarginExceptYn()) ) {
					ownPrice = goodsPrice.getSalePrice() - goodsPrice.getArsOwnDcAmt() - goodsPrice.getLumpSumOwnDcAmt() - promo.getOwnCost();
					// 현업요청으로 마진 기준금액 판매가로 변경, 프로모션마진 소수점이하 버림
					marginRate = ownPrice > 0 ? Math.floor((ownPrice - goodsPrice.getBuyPrice()) / goodsPrice.getSalePrice() * 100) : 0;
					isMarginPass = marginRate >= promoMinMarginRate;
				} else {
					isMarginPass = true;
				}

				// 프로모션 예외상품 마진률 체크
				if(promoExcept != null) {
					if (promoExcept.getPaGroupCode().contains(target.getPaGroupCode())) {
						exceptCheckPrice 	= goodsPrice.getSalePrice() - goodsPrice.getArsDcAmt() - goodsPrice.getLumpSumDcAmt() - promo.getDoAmt();
						isExceptMarginPass  = exceptCheckPrice >= promoExcept.getPaExceptMargin() ;
					}else {
						isExceptMarginPass = true;
					}

				} else {
					isExceptMarginPass = true;
				}

				// 최종혜택가 설정
				if (isMarginPass && isExceptMarginPass &&
						(doAmt < promo.getDoAmt() || (doAmt == promo.getDoAmt() && ownCost > promo.getOwnCost()) ||
								"1".equals(promo.getTalkDealYn()))) {
					promoNo = promo.getPromoNo();
					doAmt = promo.getDoAmt();
					ownCost = promo.getOwnCost();
					entpCost = promo.getEntpCost();
					
					if("1".equals(promo.getTalkDealYn())) { // 톡딜 프로모션 우선 적용
						isTalkDeal = true;
						promoBdate = (Timestamp) promo.getPromoBdate();
						break;
					}
				}
			}
		}

		Optional<PaGoodsPriceApply> optional = priceApplyRepository.findGoodsPriceApply(target.getGoodsCode(),
				target.getPaGroupCode(), target.getPaCode());

		boolean isApply = false;
		boolean isIdentical = false;

		if (optional.isPresent()) {

			PaGoodsPriceApply curPriceApply = optional.get();

			if (!promoNo.equals(Optional.ofNullable(curPriceApply.getCouponPromoNo()).orElse(""))) {
				isApply = true;
			}
			
			if ( !curPriceApply.getApplyDate().equals(goodsPrice.getApplyDate()) || curPriceApply.getMarginRate() == null) {
				if (!isApply) {
					// 실제 가격정보가 변경되었는지 체크하여 변경되지 않은 경우 전송된걸로 처리
					if (curPriceApply.getTransDate() != null
							&&  curPriceApply.getSalePrice() == goodsPrice.getSalePrice()
							&& curPriceApply.getArsDcAmt() == goodsPrice.getArsDcAmt()
							&& curPriceApply.getArsOwnDcAmt() == goodsPrice.getArsOwnDcAmt()
							&& curPriceApply.getArsEntpDcAmt() == goodsPrice.getArsEntpDcAmt()
							&& curPriceApply.getLumpSumDcAmt() == goodsPrice.getLumpSumDcAmt()
							&& curPriceApply.getLumpSumOwnDcAmt() == goodsPrice.getLumpSumOwnDcAmt()
							&& curPriceApply.getLumpSumEntpDcAmt() == goodsPrice.getLumpSumEntpDcAmt()
							&& StringUtil.compare(curPriceApply.getCouponPromoNo(), promoNo )
							&& curPriceApply.getCouponDcAmt() == doAmt
							&& curPriceApply.getCouponOwnCost() == ownCost
							&& curPriceApply.getCouponEntpCost() == entpCost ) {
						isIdentical = true;
					}
				}
				isApply = true;
			}
			
		} else {
			isApply = true;
		}

		if (isApply) {
			
			double commission = 0;
			
			if(target.getPaGroupCode().equals(PaGroup.COUPANG.code())) { //쿠팡 카테고리 수수료율 적용
				Double catCommission = categoryService.getCopnCatCommission(goods.getLmsdCode());
				commission = (catCommission == null) ? target.getPartnerBase().getCommission() : catCommission;
			}else {
				commission = target.getPartnerBase().getCommission();			
			}
			
			// 가격적용생성
			PaGoodsPriceApply priceApply = PaGoodsPriceApply.builder().goodsCode(target.getGoodsCode())
						.paGroupCode(target.getPaGroupCode())
						.paCode(target.getPaCode())
						.applyDate(goodsPrice.getApplyDate())
						.priceSeq(goodsPrice.getPriceSeq())
						.salePrice(goodsPrice.getSalePrice())
						.arsDcAmt(goodsPrice.getArsDcAmt())
						.arsOwnDcAmt(goodsPrice.getArsOwnDcAmt())
						.arsEntpDcAmt(goodsPrice.getArsEntpDcAmt())
						.lumpSumDcAmt(goodsPrice.getLumpSumDcAmt())
						.lumpSumOwnDcAmt(goodsPrice.getLumpSumOwnDcAmt())
						.lumpSumEntpDcAmt(goodsPrice.getLumpSumEntpDcAmt())
						.commission(commission)
						.couponPromoNo(promoNo)
						.couponDcAmt(doAmt)
						.couponOwnCost(ownCost)
						.couponEntpCost(entpCost)
						.insertId(Application.ID.code())
						.insertDate(currentDate)
						.build();

			priceApply.setPriceApplySeq(priceApplyRepository.getNextSeq(priceApply));

			double dcAmt = priceApply.getArsDcAmt() + priceApply.getLumpSumDcAmt() + priceApply.getCouponDcAmt();
			priceApply.setBestPrice(priceApply.getSalePrice() - dcAmt);
			priceApply.setSupplyPrice(calcSupplyPrice(priceApply.getSalePrice(), dcAmt, priceApply.getCommission(), target.getPaGroupCode()));

			double ownAmt = priceApply.getSalePrice() - priceApply.getArsOwnDcAmt() - priceApply.getLumpSumOwnDcAmt() - priceApply.getCouponOwnCost();
			double realMarginRate = Math.floor((ownAmt - goodsPrice.getBuyPrice())/priceApply.getSalePrice() * 100);
			priceApply.setMarginRate(realMarginRate);
			
			if(isTalkDeal) {
				priceApply.setTransDate(promoBdate);
				priceApply.setTransId(Application.ID.code());
			} else if(isIdentical) {
				priceApply.setTransDate(currentDate);
				priceApply.setTransId(Application.ID.code());
			} else {
				isDirty = true;
			}

			priceApplyRepository.save(priceApply);

			// 쓱닷컴 프로모션개선 별도 추가 처리 (PaGoodsPriceApply 사용하게 되면 삭제예정)
			if (PaGroup.SSG.code().equals(target.getPaGroupCode())) {
				Optional<PaPromoGoodsPrice> ssg = promoPriceRepository.findPaPromoGoodsPrice(target.getGoodsCode(),
						target.getPaCode(), goodsPrice.getApplyDate());
				PaPromoGoodsPrice promoPrice;
				if (StringUtils.hasLength(priceApply.getCouponPromoNo())) {
					if (ssg.isPresent()) {
						promoPrice = new PaPromoGoodsPrice();
						BeanUtils.copyProperties(ssg.get(), promoPrice);
						promoPrice.setPromoSeq(promoPriceRepository.getNextSeq(promoPrice));
					} else {
						promoPrice = PaPromoGoodsPrice.builder().goodsCode(target.getGoodsCode())
								.paCode(target.getPaCode()).applyDate(goodsPrice.getApplyDate()).promoSeq("0001")
								.alcoutPromoYn("1").build();
					}
					promoPrice.setPromoNo(priceApply.getCouponPromoNo());
					promoPrice.setDoAmt(priceApply.getCouponDcAmt());
					promoPrice.setOwnCost(priceApply.getCouponOwnCost());
					promoPrice.setEntpCost(priceApply.getCouponEntpCost());
					promoPrice.setInsertId(Application.ID.code());
					promoPrice.setInsertDate(currentDate);
					promoPriceRepository.save(promoPrice);
				} else {
					if (ssg.isPresent() && ssg.get().getDoAmt() > 0) {
						promoPrice = new PaPromoGoodsPrice();
						BeanUtils.copyProperties(ssg.get(), promoPrice);
						promoPrice.setPromoSeq(promoPriceRepository.getNextSeq(promoPrice));
						promoPrice.setDoAmt(0);
						promoPrice.setOwnCost(0);
						promoPrice.setEntpCost(0);
						promoPrice.setInsertId(Application.ID.code());
						promoPrice.setInsertDate(currentDate);
						promoPriceRepository.save(promoPrice);
					}
				}
			}
		}

		return isDirty;

	}

	private boolean createPaPromoTarget(PaPromoTarget paPromo, String procGb, GoodsPrice goodsPrice, Timestamp currentDate) {
		entityManager.detach(paPromo);
		paPromo.setSeq(promoRepository.getNextSeq(paPromo.getPromoNo(), paPromo.getPaCode(), paPromo.getGoodsCode()));
		paPromo.setProcGb(procGb);
		paPromo.setInsertId(Application.ID.code());
		paPromo.setInsertDate(currentDate);
		paPromo.setModifyId(Application.ID.code());
		paPromo.setModifyDate(currentDate);
		paPromo.setTransDate(null);
		promoRepository.saveAndFlush(paPromo);

		return true;
	}

	// 제휴입점요청처리
	protected boolean requestSyncPartner(PaGoodsTarget target) {
		String syncNote = "제휴입점요청처리";
		if (target.getPaSaleGb() == null) {
			Timestamp currentDate = commonService.currentDate();
			target.setPaSaleGb(PaSaleGb.REQUEST.code());
			target.setModifyId(Application.ID.code());
			target.setModifyDate(currentDate);
			targetRepository.save(target);

			log.info("{} 상품: {} 제휴사:{} ", syncNote, target.getGoodsCode(), target.getPaCode());
			logSync(CdcReason.SALE_START.code(), syncNote, target);
			return true;
		}
		return false;
	}

	// 업체회수지동기화
	protected boolean syncEntpReturn(Goods goods, PaGoodsTarget target) {

		PartnerGoods partnerGoods = target.getPartnerGoods();
		EntpUser entpUser = entpUserRepository.getById(new EntpUserId(goods.getEntpCode(), goods.getReturnManSeq()));

		// 회수지변경체크
		if (entpUser.getModifyDate().after(partnerGoods.getLastSyncDate())) {
			String syncNote = "업체회수지변경";
			log.info("{} 상품: {} ", syncNote, goods.getGoodsCode() );
			logSync(CdcReason.ENTPUSER_MODIFY.code(), syncNote, target);
			return true;
		}

		return false;
	}

	// 공지사항 동기화
	protected boolean syncPaNotice(Goods goods, PaGoodsTarget target) {
		String syncNote = "공지사항 동기화";

		List<PaNoticeTarget> noticeTargetList = noticeApplyRepository.selecNoticeTarget(target.getGoodsCode(), target.getPaGroupCode());

		boolean isDirty = false;

		List<PaNoticeApply> noticeApplyList = noticeApplyRepository.findByGoodsCodeAndPaGroupCode(target.getGoodsCode(), target.getPaGroupCode());

		Optional<PaNoticeTarget> noticeTarget;
		Timestamp currentDate = commonService.currentDate();

		for (PaNoticeApply noticeApply : noticeApplyList) {
			noticeTarget = noticeTargetList.stream().filter(notice -> notice.getNoticeNo().equals(noticeApply.getNoticeNo())).findFirst();
			// 공지사항이 종료되거나 대상에서 삭제된 경우 동기화데이터에서 제거
			if (!noticeTarget.isPresent()) {
				noticeApplyRepository.delete(noticeApply);
				isDirty = true;
				logSync(CdcReason.NOTICE_END.code(), syncNote, target);
			}
		}


		Optional<PaNoticeApply> optional;

		for (PaNoticeTarget notice : noticeTargetList) {

			optional = noticeApplyList.stream().filter(noticeApply -> noticeApply.getNoticeNo().equals(notice.getNoticeNo())).findFirst();
			// 공지사항이 새로 적용된 경우 동기화데이터 생성
			if (!optional.isPresent()) {
				PaNoticeApply noticeApply = PaNoticeApply.builder()
						.noticeNo(notice.getNoticeNo())
						.paGroupCode(target.getPaGroupCode())
						.goodsCode(notice.getGoodsCode())
						.insertId(Application.ID.code()).insertDate(currentDate)
						.modifyId(Application.ID.code()).modifyDate(currentDate).build();
				noticeApplyRepository.save(noticeApply);
				isDirty = true;
				logSync(CdcReason.NOTICE_APPLY.code(), syncNote, target);
			}
		}

		return isDirty;
	}


	// 필터로그생성
	protected void logFilter(String filterType, PaGoodsTarget target) {
		commonService.logFilter(filterType, target);
	}

	// 동기화로그생성
	protected void logSync(String cdcReasonCode, String syncNote, PaGoodsTarget target) {
		commonService.logSync(cdcReasonCode, syncNote, target);
	}

}
