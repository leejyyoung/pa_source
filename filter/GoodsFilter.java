package com.cware.partner.sync.filter;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cware.partner.common.code.InviGoodsType;
import com.cware.partner.common.code.MobGift;
import com.cware.partner.common.code.OrderMedia;
import com.cware.partner.common.code.SaleGb;
import com.cware.partner.common.code.ShipCostFlag;
import com.cware.partner.common.code.SignGb;
import com.cware.partner.common.code.SourcingMedia;
import com.cware.partner.common.code.SqcGb;
import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.id.EntpUserId;
import com.cware.partner.sync.repository.EntpUserRepository;
import com.cware.partner.sync.repository.PaStockExceptRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 제휴대상이 되는 상품의 기본 속성 체크
 *
 */
@Slf4j
@Component
public class GoodsFilter extends Filter {

	@Autowired
	EntpUserRepository entpUserRepository;
	
	@Autowired
	PaStockExceptRepository paStockExceptRepository;

	@Override
	public boolean apply(Goods goods) {
		tag = "상품필터";

		// 입점시에는 대상쿼리에서 모두 체크되는 필터로 판매이후 변경되지 않는 필드로 구분
		// 입점 이후 유효성 확인을 위한 부분
		// 입점 대상을 걸러내기 위해 메인 쿼리 로직에서 제거후 여기서 타겟팅을 제거하는 것에 대해 검토
		// 제거 조건은 해당 값이 판매이후 변경되지 않는 경우에만 해당
		if ("1".equals(goods.getTargetExcept().getPaGroupCodeAllYn())) {
			goods.setExceptNote("제휴사 연동 제외");
			log.info("{}: {} [{}]", tag, goods.getExceptNote(), goods.getGoodsCode());
			logFilter("GOODS-TARGET_EXCEPT", goods);
			return false;
		}

		if (!InviGoodsType.NORMAL.code().equals(goods.getInviGoodsType())) {
			goods.setExceptNote("일반상품이 아닙니다.");
			goods.setExcept(true);
			log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getInviGoodsType());
			logFilter("GOODS-INVI_GOODS_TYPE", goods);
			return false;
		}
		if ("1".equals(goods.getBroadSaleYn())) {
			goods.setExceptNote("방송시간외판매불가상품입니다.");
			log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getBroadSaleYn());
			logFilter("GOODS-BROAD_SALE_YN", goods);
			return false;
		}
		if ("1".equals(goods.getOmbudsmanYn())) {
			goods.setExceptNote("옴부즈맨상품입니다.");
			goods.setExcept(true);
			log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getOmbudsmanYn());
			logFilter("GOODS-OMBUDSMAN_YN", goods);
			return false;
		}

		if (!SignGb.APPROVAL.code().equals(goods.getSignGb())) {
			goods.setExceptNote("팀장승인 상태가 아닙니다.");
			log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getSignGb());
			logFilter("GOODS-SIGN_GB", goods);
			return false;
		}

		if ("1".equals(goods.getGiftYn())) {
			goods.setExceptNote("사은품입니다.");
			goods.setExcept(true);
			log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getGiftYn());
			logFilter("GOODS-GIFT_YN", goods);
			return false;
		}

		if (!(SourcingMedia.TV.code().equals(goods.getSourcingMedia())
				|| SourcingMedia.ONLINE.code().equals(goods.getSourcingMedia()))) {
			goods.setExceptNote("방송/쇼핑몰 상품이 아닙니다.");
			goods.setExcept(true);
			log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getSourcingMedia());
			logFilter("GOODS-SOURCING_MEDIA", goods);
			return false;
		}

		if (!(SqcGb.PASS.code().equals(goods.getSqcGb()) || SqcGb.EPASS.code().equals(goods.getSqcGb()))) {
			goods.setExceptNote("QA 합격한 상품이 아닙니다.");
			log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getSqcGb());
			logFilter("GOODS-SQC_GB", goods);
			return false;
		}

		if (!(SqcGb.PASS.code().equals(goods.getDescribeSqcGb())
				|| SqcGb.EPASS.code().equals(goods.getDescribeSqcGb()))) {
			goods.setExceptNote("웹QA 합격한 상품이 아닙니다.");
			log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getDescribeSqcGb());
			logFilter("GOODS-DESCRIBE_SQC_GB", goods);
			return false;
		}

//		// 소싱코드 제약조건은 제휴사필터로 이동
//		if (Arrays.stream(ExceptSourcingCode.values()).anyMatch(v -> v.name().equals(goods.getSourcingCode()))) {
//			goods.setExceptNote("제휴연동 대상 소싱 상품이 아닙니다.");
//			goods.setExcept(true);
//			log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getSourcingCode());
//			logFilter("GOODS-SOURCING_CODE", goods);
//			return false;
//		}

		if ("1".equals(goods.getGoodsAddInfo().getEmGoodsYn())) {
			goods.setExceptNote("구성원 전용 상품입니다.");
			log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getGoodsAddInfo().getEmGoodsYn());
			logFilter("GOODS-EM_GOODS_YN", goods);
			return false;
		}
		if ("1".equals(goods.getGoodsAddInfo().getGlobalDelyYn())) {
			goods.setExceptNote("해외직배송 상품입니다.");
			goods.setExcept(true);
			log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getGoodsAddInfo().getGlobalDelyYn());
			logFilter("GOODS-GLOBAL_DELY_YN", goods);
			return false;
		}
		if ("1".equals(goods.getGoodsAddInfo().getDawnYn())) {
			goods.setExceptNote("새벽배송 상품입니다.");
			goods.setExcept(true);
			log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getGoodsAddInfo().getDawnYn());
			logFilter("GOODS-DAWN_YN", goods);
			return false;
		}
		if ("1".equals(goods.getGoodsAddInfo().getAlcoholYn()) && !"1".equals(goods.getAdultYn())) {
			goods.setExceptNote("주류상품이지만 성인상품이 아닙니다.");
			goods.setExcept(true);
			log.info("{}: {} [{}]", tag, goods.getExceptNote(), goods.getGoodsCode());
			logFilter("GOODS-ALCOHOL_YN", goods);
			return false;
		}

		// 입점 필수 조건 필터 - end


		// 주문매체는 입점 필수 대상쿼리에 포함되어 있지만 변경 가능하므로 매번 체크
		if (!"1".equals(goods.getOrderMediaAllYn())) {
			if (!(goods.getOrderMedia().contains(OrderMedia.PCWEB.code())
					|| goods.getOrderMedia().contains(OrderMedia.MOBILE.code()))) {
				goods.setExceptNote("쇼핑몰 주문매체 상품이 아닙니다.");
				log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getOrderMedia());
				logFilter("GOODS-ORDER_MEDIA", goods);
				return false;
			}
		}

		// 정보고시 등록여부
		if (goods.getOfferType() == null) {
			goods.setExcept(true);
			goods.setExceptNote("등록된 상품정보고시가 없습니다.");
			log.info("{}: {} [{}]", tag, goods.getExceptNote(), goods.getGoodsCode());
			logFilter("GOODS-OFFER", goods);
			return false;
		}

		// 상품기술서 등록여부
		if (goods.getDescribeModifyDate() == null) {
			goods.setExcept(true);
			goods.setExceptNote("등록된 상품기술서 내용이 없습니다.");
			log.info("{}: {} [{}]", tag, goods.getExceptNote(), goods.getGoodsCode());
			logFilter("GOODS-DESCRIBE", goods);
			return false;
		}

		// 이미지조회
		try {
			if (goods.getGoodsImage().getImageC() == null && goods.getGoodsImage().getImageG() == null ) {
				goods.setExcept(true);
				goods.setExceptNote("유효한 이미지가 없습니다.");
				log.info("{}: {} [{}]", tag, goods.getExceptNote(), goods.getGoodsCode());
				logFilter("GOODS-IMAGE", goods);
				return false;
			}
		} catch (EntityNotFoundException e) {
			goods.setExcept(true);
			goods.setExceptNote("이미지 등록이 안되어 있습니다.");
			log.info("{}: {} [{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), null, goods.getGoodsSyncNo());
			logFilter("GOODS-IMAGE", goods);
			return false;
		}

		if (!MobGift.NONE.code().equals(goods.getGoodsAddInfo().getMobGiftGb())) {
			goods.setExcept(true);
			goods.setExceptNote("모바일이용권 상품입니다.");
			log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getGoodsAddInfo().getMobGiftGb());
			logFilter("GOODS-MOB_GIFT_GB", goods);
			return false;
		}

		if (!SaleGb.FORSALE.code().equals(goods.getSaleGb())) {
			goods.setExceptNote("판매 상태가 아닙니다.");
			log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getSaleGb());
			logFilter("GOODS-SALE_GB", goods);
			return false;
		}

		if (!"1".equals(goods.getForSale())) {
			goods.setExceptNote("판매기간이 아닙니다.");
			log.info("{}: {} [{}-{}~{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getSaleStartDate(), goods.getSaleEndDate());
			logFilter("GOODS-SALE_DATE", goods);
			return false;
		}
		
		// 2023-06-22 중고,리퍼 구분값 추가
//		if("1".equals(goods.getGoodsAddInfo().getGoodsStts()) || "2".equals(goods.getGoodsAddInfo().getGoodsStts())) {
//			goods.setExceptNote("중고/리퍼 상품입니다.");
//			log.info("{}: {} [{}]", tag, goods.getExceptNote(), goods.getGoodsCode());
//			logFilter("GOODS-STTS", goods);
//			return false;
//		}

//		if (!BuyMed.OWN_EXTERNAL.code().equals(goods.getBuyMed()) && goods.getOrderAbleQty() < 3) {
//			goods.setExcept(true);
//			goods.setExceptNote("주문가능한 재고가 없습니다.");
//			log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getOrderAbleQty());
//			logFilter("GOODS-ORDER_ABLE_QTY", goods);
//			return false;
//		}

		// 제휴연동시 직사입타창고에 대한 고려가 없음. 연동 기준으로 재고 처리 변경
		// 단품별로 합산하여 3보다 작으면 입점불가 처리
		// 재고허들 예외 업체의 경우 pass
		if(!paStockExceptRepository.existsByTargetGbAndTargetCodeAndUseYn("20", goods.getEntpCode(), "1")) {
			if (goods.getOrderAbleQty() < 3) {
				goods.setExcept(true);
				goods.setExceptNote("입점 가능한 재고가 부족합니다.");
				log.info("{}: {} [{}-{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getOrderAbleQty());
				logFilter("GOODS-ORDER_ABLE_QTY", goods);
				return false;
			}
		}

		// 배송비 데이터정합성 오류 방어로직
		if (goods.getPaCustShipCost() == null) {
			goods.setExcept(true);
			goods.setExceptNote("고객부담배송비 정책이 등록되어 있지 않습니다.");
			log.info("{}: {} 상품: {}", tag, goods.getExceptNote(), goods.getGoodsCode());
			logFilter("GOODS-SHIPCOST", goods);
			return false;
		}

		if (goods.getShipCostCode().contains(ShipCostFlag.QTY.code())) {
			goods.setExcept(true);
			goods.setExceptNote("수량단위 배송비정책은 제휴연동에서 제외됩니다.");
			log.info("{}: {} [상품:{} 정책코드:{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getShipCostCode());
			logFilter("GOODS-SHIP_COST_CODE", goods);
			return false;
		}

		// 무료배송이 아닌경우 주문배송비가 100원이상이어함
		if (!ShipCostFlag.FREE.code().equals(goods.getPaCustShipCost().getShipCostFlag())) {

			if (goods.getPaCustShipCost().getOrdCost() < 100) {
				goods.setExcept(true);
				goods.setExceptNote("주문배송비는 100원 이상이어야합니다.");
				log.info("{}: {} 상품: {}, 배송비 : {}", tag, goods.getExceptNote(), goods.getGoodsCode(),
						goods.getPaCustShipCost().getOrdCost());
				logFilter("GOODS-ORD_COST", goods);
				return false;
			}

			// 조건부나 개별조건부일때 배송비기준금액이 100원 단위보다 커야하고 기준금액이 100원이상이어야함.
			if (ShipCostFlag.BASEAMT.code().equals(goods.getPaCustShipCost().getShipCostFlag())
					|| ShipCostFlag.BASEAMT_CODE.code().equals(goods.getPaCustShipCost().getShipCostFlag())) {
				if (goods.getPaCustShipCost().getShipCostBaseAmt() < 100) {
					goods.setExcept(true);
					goods.setExceptNote("무료배송기준금액은 100원 이상이어야합니다.");
					log.info("{}: {} 상품: {}, 기준금액: {}", tag, goods.getExceptNote(), goods.getGoodsCode(),
							goods.getPaCustShipCost().getShipCostBaseAmt());
					logFilter("GOODS-SHIP_COST_BASE_AMT", goods);
					return false;
				}
				if (Math.floorMod(goods.getPaCustShipCost().getShipCostBaseAmt(), 100) > 0) {
					goods.setExcept(true);
					goods.setExceptNote("무료배송기준금액은 100원단위 이상이어야합니다.");
					log.info("{}: {} 상품: {}, 기준금액: {}", tag, goods.getExceptNote(), goods.getGoodsCode(),
							goods.getPaCustShipCost().getShipCostBaseAmt());
					logFilter("GOODS-SHIP_COST_BASE_AMT", goods);
					return false;
				}

			}
		}
		
		// 상품명 직구/해외배송 포함여부 체크
		if(goods.getGoodsName().contains("직구") || goods.getGoodsName().contains("해외배송")) {
			goods.setExcept(true);
			goods.setExceptNote("해외배송 상품은 제휴OUT 연동 대상에서 제외됩니다.");
			log.info("{}: {} [상품:{} 상품명:{}]", tag, goods.getExceptNote(), goods.getGoodsCode(), goods.getGoodsName());
			logFilter("GOODS-GLOBAL_DELY_WORD", goods);
			return false;
		}

		//착불상품일 경우 완전무료배송이어야함
		if("1".equals(goods.getCollectYn())) {
			if( (goods.getPaCustShipCost().getOrdCost() > 0) ||
			    (goods.getPaCustShipCost().getChangeCost() > 0) ||
			    (goods.getPaCustShipCost().getReturnCost() > 0) ) {
				goods.setExcept(true);
				goods.setExceptNote("착불상품인 경우 유료배송비 설정불가합니다.");
				log.info("{}: {} [{}]", tag, goods.getExceptNote(), goods.getGoodsCode());
				logFilter("GOODS-COLLECT_SHIPCOST", goods);
				return false;
			}
		}
		
		// 출고지조회
		try {
			goods.setShipManUser(entpUserRepository.getById(new EntpUserId(goods.getShipEntpCode(),
					 goods.getDelyType().equals("10") ? "003" : goods.getShipManSeq())));
		} catch (EntityNotFoundException e) {
			goods.setExcept(true);
			goods.setExceptNote("상품에 등록된 출고지가 없습니다.");
			log.info("{}: {} 상품: {}, 출고업체: {}-{}", tag, goods.getExceptNote(), goods.getGoodsCode(),
					goods.getShipEntpCode(), goods.getShipManSeq());
			logFilter("GOODS-SHIP_MAN_SEQ", goods);
			return false;
		}

		// 회수지조회
		try {
			goods.setReturnManUser(entpUserRepository.getById(new EntpUserId(goods.getShipEntpCode(),
					 goods.getDelyType().equals("10") ? "002" : goods.getReturnManSeq())));
		} catch (EntityNotFoundException e) {
			goods.setExcept(true);
			goods.setExceptNote("상품에 등록된 회수지가 없습니다.");
			log.info("{}: {} 상품: {}, 회수업체: {}-{}", tag, goods.getExceptNote(), goods.getGoodsCode(),
					goods.getShipEntpCode(), goods.getReturnManSeq());
			logFilter("GOODS-RETURN_MAN_SEQ", goods);
			return false;
		}
		return true;
	}

}
