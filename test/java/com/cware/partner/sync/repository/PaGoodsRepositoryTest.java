package com.cware.partner.sync.repository;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cware.partner.sync.domain.entity.PaGoods;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PaGoodsRepositoryTest {

	@Autowired
	private PaGoodsRepository paGoodsRepository;

	@Test
	void test() {

		Optional<PaGoods> goods = paGoodsRepository.findById("22405887");
		System.out.println(goods.isPresent());

	}

	@Test
	void testFound() {

		Optional<PaGoods> goods = paGoodsRepository.findById("20017712");
		System.out.println(goods.isPresent());
		System.out.println(goods.get());
	}


}
