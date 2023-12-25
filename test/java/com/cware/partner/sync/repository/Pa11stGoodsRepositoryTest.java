package com.cware.partner.sync.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cware.partner.sync.domain.entity.Pa11stGoods;
import com.cware.partner.sync.domain.id.PaGoodsId;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class Pa11stGoodsRepositoryTest {

	@Autowired
	private Pa11stGoodsRepository pa11stGoodsRepository;

	@Test
	void test() {

		Pa11stGoods goods = pa11stGoodsRepository.getById(new PaGoodsId("01", "11","20017642"));
		System.out.println(goods);

	}

	@Test
	void testNotFound() {

		Pa11stGoods goods = pa11stGoodsRepository.getById(new PaGoodsId("01", "11","22405887"));
		System.out.println(goods);

	}


}
