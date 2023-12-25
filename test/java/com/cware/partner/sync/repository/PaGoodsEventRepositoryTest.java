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

import com.cware.partner.sync.domain.entity.PaGoodsEvent;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PaGoodsEventRepositoryTest {

	@Autowired
	private PaGoodsEventRepository paGoodsEventRepository;

	@Test
	void test() {

		List<PaGoodsEvent> list = paGoodsEventRepository.findByGoodsCode("20033072");
		assertThat(list.size()).isGreaterThan(0);
	}

}
