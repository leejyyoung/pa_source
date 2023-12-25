package com.cware.partner.sync.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cware.partner.sync.domain.entity.PaCopnGoods;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PaCopnGoodsRepositoryTest {

	@Autowired
	private PaCopnGoodsRepository goodsRepository;

	@Test
	void testFindTransTargetList() {

		Pageable pageable = PageRequest.of(0, 100);
		Slice<PaCopnGoods> slice = goodsRepository.findTransTargetList(pageable);
		System.out.println(slice.getContent());
		assertThat(slice.getSize()).isGreaterThan(0);

	}

}
