package com.cware.partner.sync.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EntpAddrSyncWempServiceTest {

	@Autowired
	private EntpAddrSyncWempService entpAddressSyncService;


	@Test
	void test() {
		entpAddressSyncService.executeEntpAddressSync();
	}

}
