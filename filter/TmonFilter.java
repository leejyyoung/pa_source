package com.cware.partner.sync.filter;

import org.springframework.stereotype.Component;

import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;

import lombok.extern.slf4j.Slf4j;

/**
 * 티몬 입점 제약조건 체크
 *
 */
@Slf4j
@Component
public class TmonFilter extends Filter {

	public boolean apply(Goods goods, PaGoodsTarget target) {

		tag = "티몬필터";

//		// 상품명 길이는 60이하여야 함.
//		if (goods.getGoodsName().length() > 60) {
//			target.setExcept(true);
//			target.setExceptNote("상품명 길이가 60을 초과하면 안됩니다.");
//			log.info("{}: {} [{}-{}] 상품: {}, 상품명 : {}", tag, target.getExceptNote(), target.getGoodsCode(),
//					goods.getGoodsName());
//			logFilter("TMON-GOODS_NAME", target);
//			return false;
//		}

		// 단품개수가 200개를 초과하면 제외
		if (goods.getGoodsDtList().size() > 200) {
			target.setExcept(true);
			target.setExceptNote("단품개수가 200개를 초과했습니다.");
			log.info("{}: {} [{}-{}] 상품: {}, 단품개수 : {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getGoodsDtList().size());
			logFilter("TMON-GOODSDT_SIZE", target);
			return false;
		}

		// 도서산간/제주 추가배송비 30만원 초과시 제외
		if ((goods.getPaCustShipCost().getIslandCost() - goods.getPaCustShipCost().getOrdCost()) > 300_000
				|| (goods.getPaCustShipCost().getJejuCost() - goods.getPaCustShipCost().getOrdCost()) > 300_000) {
			target.setExcept(true);
			target.setExceptNote("도서산간 또는 제주 추가배송비는 30만원을 초과하면 안됩니다.");
			log.info("{}: {} 상품: {}, 도서산간비: {}, 제주비: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getPaCustShipCost().getIslandCost(), 	goods.getPaCustShipCost().getJejuCost());
			logFilter("TMON-ISLAND_COST", target);
			return false;
		}

		return true;
	}

	@Override
	public boolean apply(Goods goods) {
		return false;
	}

}
