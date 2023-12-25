package com.cware.partner.common.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CategoryServiceTest {

	@Autowired
	private CategoryService categoryService;

	@Test
	void test() {

		System.out.println(categoryService.getCopnCatCommission("55011104")); //10.8
		System.out.println(categoryService.getCopnCatCommission("30020802")); //9.6

		System.out.println(categoryService.getCopnCatMinMarginRate("55011104")); //12
		System.out.println(categoryService.getCopnCatMinMarginRate("30020802")); //12

	}

 
}
