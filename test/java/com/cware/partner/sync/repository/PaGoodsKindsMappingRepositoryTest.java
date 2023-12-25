package com.cware.partner.sync.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cware.partner.sync.domain.EbayCategory;
import com.cware.partner.sync.domain.entity.PaGoodsKindsMapping;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PaGoodsKindsMappingRepositoryTest {

	@Autowired
	private PaGoodsKindsMappingRepository paGoodsKindsMappingRepository;

	@Test
	void test() {

		List<PaGoodsKindsMapping> list = paGoodsKindsMappingRepository.findByLmsdCode("10020202");
		assertThat(list.size()).isGreaterThan(0);

	}

	@Test
	void testEbay() {

		EbayCategory category = paGoodsKindsMappingRepository.getEbayCategory("02", "10020202");
		System.out.println(category);;

	}

}
