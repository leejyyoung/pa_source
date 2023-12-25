package com.cware.partner.sync.filter;

import org.springframework.stereotype.Component;

import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;

import lombok.extern.slf4j.Slf4j;

/**
 * 롯데온 입점 제약조건 체크
 *
 */
@Slf4j
@Component
public class LotteonFilter extends Filter {

	public boolean apply(Goods goods, PaGoodsTarget target) {

		tag = "롯데온필터";

		// 단품개수가 500개를 초과하면 제외
		if (goods.getGoodsDtList().size() > 500) {
			target.setExcept(true);
			target.setExceptNote("단품개수가 500개를 초과했습니다.");
			log.info("{}: {} [{}-{}] 상품: {}, 단품개수 : {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getGoodsDtList().size());
			logFilter("LOTTEON-GOODSDT_SIZE", target);
			return false;
		}

		return true;
	}

	@Override
	public boolean apply(Goods goods) {
		return false;
	}

}
