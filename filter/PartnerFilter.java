package com.cware.partner.sync.filter;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.cware.partner.common.code.ExceptSourcingCode;
import com.cware.partner.common.code.PaGroup;
import com.cware.partner.common.service.CategoryService;
import com.cware.partner.common.service.CodeService;
import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.GoodsPrice;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;

import lombok.extern.slf4j.Slf4j;

/**
 * 제휴대상이 되는 상품의 기본 속성 체크
 *
 */
@Slf4j
@Component
public class PartnerFilter extends Filter {

	@Autowired
	CodeService codeService;

	@Autowired
	CategoryService categoryService;
	
	public boolean apply(Goods goods, PaGoodsTarget target) {
		tag = "파트너필터";

		// 제휴사별 연동 제외
		if (StringUtils.hasLength(goods.getTargetExcept().getPaGroupCode())) {
			if (goods.getTargetExcept().getPaGroupCode().contains(target.getPaGroupCode())) {
				target.setExceptNote("제휴사 연동 제외");
				log.info("{}: {} [{}-{}]", tag, target.getExceptNote(), target.getGoodsCode(), target.getPaGroupCode());
				logFilter("PARTNER-TARGET_EXCEPT", target);
				return false;
			}
		}

		// 제외소싱상품 예외 입점
		if (Arrays.stream(ExceptSourcingCode.values()).anyMatch(v -> v.name().equals(goods.getSourcingCode()))) {
			if (goods.getSourcingExceptInput() == null || (!goods.getSourcingExceptInput().getPaAllYn().equals("1")
					&& !goods.getSourcingExceptInput().getPaGroupCode().contains(target.getPaGroupCode()))) {
				target.setExceptNote("제휴연동 대상 소싱 상품이 아닙니다: " + goods.getSourcingCode());
				target.setExcept(true);
				log.info("{}: {} [{}-{}]", tag, target.getExceptNote(), target.getGoodsCode(), target.getPaGroupCode());
				logFilter("PARTNER-SOURCING_CODE", target);
				return false;
			}
		}

		// 제휴사 재고연동시에 직사입타창고에 대한 고려 되어 있지 않고, 안전재고기준 1 미만으로 체크
		target.setPartnerStockQty((int) Math
				.ceil(goods.getOrderAbleQty() * target.getPartnerBase().getAbleStockRate()));
		if (target.getPartnerStockQty() < 1) {
			target.setExcept(true);
			target.setExceptNote("제휴사 주문가능재고 부족");
			log.info("{}: {} [{}-{}] 상품재고: {}, 제휴사재고: {}", tag, target.getExceptNote(), target.getGoodsCode(), target.getPaGroupCode(), goods.getOrderAbleQty(), target.getPartnerStockQty());
			logFilter("PARTNER-ORDER_ABLE_QTY", target);
			return false;
		}

		GoodsPrice goodsPrice = goods.getGoodsPrice();
		
		// 마진체크 예외처리된 제외소싱상품인지 체크
		if(goods.getSourcingExceptInput() == null || !"1".equals(goods.getSourcingExceptInput().getMarginExceptYn())) { // 제외소싱&마진체크예외 상품 아닌 경우 마진체크 진행
			
			if (target.getEventYn().equals("1")) { // 행사상품인 경우 행사최소마진율 체크
				double minMarginRate = codeService.getEventMinMarginRate();
				if (goodsPrice.getMarginRate() < codeService.getEventMinMarginRate() ) {
					target.setExcept(true);
					target.setExceptNote("상품마진이 행사 최소마진보다 작습니다.");
					log.info("{}: {} [{}-{}] 상품마진: {}, 최저마진: {}", tag, target.getExceptNote(), target.getGoodsCode(), target.getPaGroupCode(), goodsPrice.getMarginRate(), minMarginRate);
					logFilter("PARTNER-EVENT_LIMIT_MARGIN", target);
					return false;
				}
			} else { // 행사상품이 아닌경우 제휴사별 마진/최저판매가 체크
				double minMarginRate = 0;
				if(target.getPaGroupCode().equals(PaGroup.SSG.code()) && "0".equals(goods.getTaxYn())) { //ssg면서 비과세인 경우
					minMarginRate = target.getPartnerBase().getTaxFreeMarginRate();
				}else if(target.getPaGroupCode().equals(PaGroup.COUPANG.code())){ //쿠팡 카테고리별 적용
					/* 20230316 적용
					   쿠팡 카테고리 수수료(VAT포함) 9% 이하 : 입점 수수료 허들 10%
					   쿠팡 카테고리 수수료(VAT포함) 9% 초과 : 입점 수수료 허들 12%
					*/
		            Double catMinMarginRate = categoryService.getCopnCatMinMarginRate(goods.getLmsdCode());
		            minMarginRate = (catMinMarginRate == null) ? target.getPartnerBase().getMinMarginRate() : catMinMarginRate;
				}else {
					minMarginRate = target.getPartnerBase().getMinMarginRate();
				}
				
				if (goodsPrice.getMarginRate() < minMarginRate) {
					// 예정된 행사가 없는 경우 타겟 제거
					target.setExcept(target.getPreEventYn().equals("0"));
					target.setExceptNote("상품마진이 제휴사 최소마진보다 작습니다.");
					log.info("{}: {} [{}-{}] 상품마진: {}, 최저마진: {}", tag, target.getExceptNote(), target.getGoodsCode(), target.getPaGroupCode(), goodsPrice.getMarginRate(), minMarginRate);
					logFilter("PARTNER-MIN_MARGIN_RATE", target);
					return false;
				}
				double minSalePrice = target.getPartnerBase().getMinSalePrice();
				if (goodsPrice.getSalePrice() < minSalePrice) {
					target.setExcept(true);
					target.setExceptNote("상품가격이 제휴사 최저가격보다 작습니다.");
					log.info("{}: {} [{}-{}] 상품가격: {}, 최저가격: {}", tag, target.getExceptNote(), target.getGoodsCode(), target.getPaGroupCode(), goodsPrice.getSalePrice(), minSalePrice);
					logFilter("PARTNER-MIN_SALE_PRICE", target);
					return false;
				}
			}
		}
		
		if ((!("02".equals(target.getPaGroupCode()) || "03".equals(target.getPaGroupCode())) && goods.getUsedEntpCnt() > 0) ||
			(goods.getUsedEntpCnt() == 0 && ("1".equals(goods.getGoodsAddInfo().getGoodsStts()) || "2".equals(goods.getGoodsAddInfo().getGoodsStts())))) {
			target.setExceptNote("중고/리퍼 상품입니다.");
			target.setExcept(true);
			log.info("{}: {} [{}-{}]", tag, target.getExceptNote(), target.getGoodsCode(), target.getPaGroupCode());
			logFilter("PARTNER-GOODS-STTS", target);
			return false;
		}

		return true;
	}

	@Override
	public boolean apply(Goods goods) {
		return false;
	}

}
