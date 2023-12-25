package com.cware.partner.sync.filter;

import org.springframework.beans.factory.annotation.Autowired;

import com.cware.partner.common.service.CommonService;
import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;
import com.cware.partner.sync.repository.PaGoodsFilterLogRepository;

public abstract class Filter {

	@Autowired
	PaGoodsFilterLogRepository goodsFilterLogRepository;

	@Autowired
	CommonService commonService;

	String tag = "필터";

	public boolean apply(Goods goods) {
		return true;
	}

	// 필터로그생성
	protected void logFilter(String goodsCode, String filterType, String filterNote, String paGroupCode, long goodsSyncNo) {
		commonService.logFilter(goodsCode, filterType, filterNote, paGroupCode, goodsSyncNo);
	}

	protected void logFilter(String filterType, PaGoodsTarget target) {
		commonService.logFilter(filterType, target);
	}

	public void logFilter(String filterType, Goods goods) {
		commonService.logFilter(filterType, goods);
	}

}
