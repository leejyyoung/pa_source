package com.cware.partner.common.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommonServiceTest {

	@Autowired
	private CommonService commonService;


	@Test
	void test() {

		System.out.println(commonService.currentDate());
	}

}

