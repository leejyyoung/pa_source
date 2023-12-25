package com.cware.partner.sync.repository;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cware.partner.sync.domain.entity.GoodsPrice;
import com.cware.partner.sync.domain.entity.PaGoodsPrice;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class GoodsPriceRepositoryTest {

	@Autowired
	private GoodsPriceRepository goodsPriceRepository;
	@Autowired
	private PaGoodsPriceRepository paGoodsPriceRepository;

	@Test
	void test() {

		GoodsPrice price = goodsPriceRepository.findApplyGoodsPrice("25487984");
		System.out.println(price);

		Optional<PaGoodsPrice> optional = paGoodsPriceRepository.findApplyGoodsPrice("52", "25487984");

		if (optional.isPresent()) {
			PaGoodsPrice paPrice = optional.get();
			System.out.println("salePrice:" + (paPrice.getSalePrice() == price.getSalePrice()));
			System.out.println("getDcAmt:" + (paPrice.getDcAmt() == price.getArsDcAmt()));
			System.out.println("getLumpSumDcAmt:" + (paPrice.getLumpSumDcAmt() == price.getLumpSumDcAmt()));
		}
	}

}
