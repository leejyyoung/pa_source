package com.cware.partner.sync.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CdcGoodsSyncServiceTest {

	@Autowired
	private CdcGoodsSyncService cdcGoodsSyncService;


	@Test
	void test() {
		cdcGoodsSyncService.executeProductSync();
	}

}
