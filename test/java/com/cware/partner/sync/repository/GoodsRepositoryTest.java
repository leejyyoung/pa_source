package com.cware.partner.sync.repository;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.PaGoods;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class GoodsRepositoryTest {

	@Autowired
	private GoodsRepository goodsRepository;
	@Autowired
	private PaGoodsRepository paGoodsRepository;

	@Test
	void test() {

		Optional<Goods> goods = goodsRepository.findById("22409930");
		System.out.println(goods.get().getDoNotIslandDelyYn());
		System.out.println(goods.get().getDelyType());
		System.out.println(goods.get().getEntpCode());
		System.out.println(goods.get().getShipCostCode());
		System.out.println(goods.get().getDescribeModifyDate());
		System.out.println(goods.get().isFoodInfo());
//		System.out.println("next.....");
//		System.out.println(goods.get().getGoodsImage());
//		System.out.println(goods.get().getGoodsDtList());
//		System.out.println(goods.get().getGoodsDtList().get(0).getPaGoodsDt());
//		System.out.println(goods.get().getOfferList());
//		System.out.println("next.....");
//		System.out.println(goods.get().getGoodsAddInfo());

	}

	@Test
	void testNew() {

		Optional<Goods> goods = goodsRepository.findById("22405887");
		System.out.println(goods.get().getGoodsName());
		System.out.println(goods.get().getForSale());
		System.out.println("next.....");
		try {

			System.out.println(goods.get().getGoodsImage());

		} catch (EntityNotFoundException e) {
			System.out.println("이미지없음");
		}
	}

	@Test
	@Transactional
	@Rollback(value = false)
	void testDirty() {

		Optional<Goods> goods = goodsRepository.findById("22407470");
		PaGoods pagoods = goods.get().getPaGoods();
		System.out.println(pagoods);

		pagoods.setGoodsName("동기화테스트12");
		PaGoods pagoods1 = paGoodsRepository.save(pagoods);
		System.out.println(pagoods1.equals(pagoods));

	}

}
