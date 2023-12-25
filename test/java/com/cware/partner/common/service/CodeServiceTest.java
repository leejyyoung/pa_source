package com.cware.partner.common.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cware.partner.common.code.PaCode;

@SpringBootTest
class CodeServiceTest {

	@Autowired
	private CodeService codeService;

	@Test
	void test() {

		System.out.println(codeService.getOriginName("0027"));
		System.out.println(codeService.getOriginName("0047"));

	}

	@Test
	void testPartnerMap() {

		System.out.println(codeService.getMinMarginRate(PaCode.SK11ST_ONLINE.code()));
		System.out.println(codeService.getCommision(PaCode.COUPANG_ONLINE.code()));

	}

}
