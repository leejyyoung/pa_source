package com.cware.partner.sync.service;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
class EntpAddrSyncServiceTest {

	@Autowired
	private EntpAddrSyncService entpAddressSyncService;
	@Autowired
	private EntpAddrSyncHalfService entpAddrSyncHalfService;

	@Test
	//@Transactional
	void test() {
		entpAddressSyncService.executeEntpAddressSync();
	}
	
	@Test
	//@Transactional
	//@Rollback(false) 
	void testHalf() {
		entpAddrSyncHalfService.executeEntpAddressSync();
	}
}
