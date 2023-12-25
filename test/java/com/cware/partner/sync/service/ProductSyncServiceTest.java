package com.cware.partner.sync.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductSyncServiceTest {

	@Autowired
	private ProductSyncService productSyncService;

	@Test
	void testDevelop() {
		productSyncService.syncProduct("22410945");
	}
/*

	@Test
	void test() {
		productSyncService.syncProduct("29321835");
	}

	@Test
	void testDevelop() {
		productSyncService.syncProduct("22410259");
	}

	@Test
	void testDevelopKakao() {
		productSyncService.syncProduct("22408229");
	}

	@Test
	void testExceptNaver() {
		productSyncService.syncProduct("21067227");
	}

	@Test
	void testSaleStartEbayOffer() {
		productSyncService.syncProduct("22408746");
	}

	@Test
	void testModifyEbayImage() {
		productSyncService.syncProduct("22406743");
	}

	@Test
	void testPromoApplyEbay() {
		productSyncService.syncProduct("22408772");
	}

	@Test
	void testSaleStart11st() {
		productSyncService.syncProduct("22410092");
	}

	@Test
	void testSaleStartGmarket() {
		productSyncService.syncProduct("21067224");
	}

	@Test
	void testGoodsModifySsg() {
		productSyncService.syncProduct("21067225");
	}

	@Test
	void testDevelopSourcingExcept() {
		productSyncService.syncProduct("22409065");
	}
 */
}
