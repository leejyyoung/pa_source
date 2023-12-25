package com.cware.partner.sync.filter;

import org.springframework.stereotype.Component;

import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;

import lombok.extern.slf4j.Slf4j;

/**
 * 인터파크 입점 제약조건 체크
 *
 */
@Slf4j
@Component
public class InterparkFilter extends Filter {

	public boolean apply(Goods goods, PaGoodsTarget target) {

		tag = "인터파크필터";

		// 도서산간/제주배송비는 10원단위 이상
		if (Math.floorMod((long)goods.getPaCustShipCost().getIslandCost(), 10) > 0 ||
				Math.floorMod((long)goods.getPaCustShipCost().getJejuCost(), 10) > 0) {
			target.setExcept(true);
			target.setExceptNote("도서산간 또는 제주배송비는 10원단위 이상이어야합니다.");
			log.info("{}: {} 상품: {}, 도서산간비: {}, 제주비: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getPaCustShipCost().getIslandCost(), 	goods.getPaCustShipCost().getJejuCost());
			logFilter("INTERPARK-ISLAND_COST", target);
			return false;
		}

		return true;
	}

	@Override
	public boolean apply(Goods goods) {
		return false;
	}

}
