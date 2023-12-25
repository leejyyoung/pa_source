package com.cware.partner.sync.filter;

import org.springframework.stereotype.Component;

import com.cware.partner.common.code.ShipCostFlag;
import com.cware.partner.sync.domain.entity.EntpUser;
import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;

import lombok.extern.slf4j.Slf4j;

/**
 * 위메프 입점 제약조건 체크
 *
 */
@Slf4j
@Component
public class WempFilter extends Filter {

	public boolean apply(Goods goods, PaGoodsTarget target) {

		tag = "위메프필터";

		// 단품개수가 200개를 초과하면 제외
		if (goods.getGoodsDtList().size() > 200) {
			target.setExcept(true);
			target.setExceptNote("단품개수가 200개를 초과했습니다.");
			log.info("{}: {} [{}-{}] 상품: {}, 단품개수 : {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getGoodsDtList().size());
			logFilter("WEMP-GOODSDT_SIZE", target);
			return false;
		}

		EntpUser returnMan = goods.getReturnManUser();

		// 업체회수지 유효성 체크
		if ((returnMan.getStdPostAddr1() == null && returnMan.getPostAddr() == null) ||
				(returnMan.getStdPostAddr2() == null && returnMan.getAddr() == null)) {
			target.setExcept(true);
			target.setExceptNote("업체회수지 주소가 유효하지 않습니다.");
			log.info("{}: {} [{}-{}] 상품: {}, 업체 : {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getEntpCode());
			logFilter("WEMP-RETURN_MAN_SEQ", target);
			return false;
		}

		// 조건부 무료 기준금액 체크
		if (ShipCostFlag.BASEAMT.code().equals(goods.getPaCustShipCost().getShipCostFlag())
				|| ShipCostFlag.BASEAMT_CODE.code().equals(goods.getPaCustShipCost().getShipCostFlag())) {
			if (goods.getPaCustShipCost().getShipCostBaseAmt() >= 10_000_000) {
				target.setExcept(true);
				target.setExceptNote("무료배송기준금액은 천만원 미만이어야합니다.");
				log.info("{}: {} 상품: {}, 기준금액: {}", tag, goods.getExceptNote(), goods.getGoodsCode(),
						goods.getPaCustShipCost().getShipCostBaseAmt());
				logFilter("WEMP-SHIP_COST_BASE_AMT_MAX", target);
				return false;
			}
		}
		
		// 위메프 주류 상품 반려
		if ("1".equals(goods.getGoodsAddInfo().getAlcoholYn())) {
			target.setExcept(true);
			target.setExceptNote("위메프 주류상품은 입점할 수 없습니다.");
			log.info("{}: {} 상품: {}", tag, target.getExceptNote(), target.getGoodsCode());
			logFilter("WEMP-ALCOHOL-YN", target);
			return false;
		}

		return true;
	}

	@Override
	public boolean apply(Goods goods) {
		return false;
	}

}
