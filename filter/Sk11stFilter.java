package com.cware.partner.sync.filter;

import org.springframework.stereotype.Component;

import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;

/**
 * 11번가 입점 제약조건 체크
 *
 */
@Component
public class Sk11stFilter extends Filter {

	public boolean apply(Goods goods, PaGoodsTarget target) {
		return true;
	}

	@Override
	public boolean apply(Goods goods) {
		return false;
	}

}
