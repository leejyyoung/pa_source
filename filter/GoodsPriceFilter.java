package com.cware.partner.sync.filter;

import org.springframework.stereotype.Component;

import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.GoodsPrice;

import lombok.extern.slf4j.Slf4j;

/**
 * 상품가격 필터
 *
 */
@Slf4j
@Component
public class GoodsPriceFilter extends Filter {

	@Override
	public boolean apply(Goods goods) {
		tag = "가격필터";

		GoodsPrice goodsPrice = goods.getGoodsPrice();
		if (goodsPrice == null) {
			goods.setExceptNote("적용된 상품가격이 없습니다.");
			log.info("{}: {} [{}]", tag, goods.getExceptNote(), goods.getGoodsCode());
			logFilter("GOODS-PRICE", goods);
			return false;
		}

		goodsPrice.setBestPrice(goodsPrice.getSalePrice() - goodsPrice.getArsDcAmt() - goodsPrice.getLumpSumDcAmt());

		// 현업 요청으로 마진율 기준금액을 상품가격으로 변경, 소수점버림
		// 상품마진 = 판매가-매입가로 변경
		double marginRate = (goodsPrice.getSalePrice() - goodsPrice.getBuyPrice()) / goodsPrice.getSalePrice() * 100;
		goodsPrice.setMarginRate(Math.floor(marginRate));

		// 고마진상품인 경우 마진이 21이상이어야함.
		if ("1".equals(goods.getGoodsAddInfo().getMobileEtvYn()) && goodsPrice.getMarginRate() < 21) {
			goods.setExcept(true);
			goods.setExceptNote("고마진상품의 경우 상품마진이 21%이상이어야 합니다.");
			log.info("{}: {} [{}] 상품마진: {}", tag, goods.getExceptNote(), goods.getGoodsCode(),
					goodsPrice.getMarginRate());
			logFilter("GOODS-ETV_MARGIN", goods);
			return false;
		}
		return true;
	}

}
