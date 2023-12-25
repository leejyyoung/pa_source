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

import com.cware.partner.sync.domain.entity.PaGoodsTarget;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PaGoodsTargetRepositoryTest {

	@Autowired
	private PaGoodsTargetRepository paGoodsTargetRepository;

	@Test
	void test() {

		List<PaGoodsTarget> list = paGoodsTargetRepository.findByGoodsCode("22407742");
		System.out.println(list);
		assertThat(list.size()).isGreaterThan(0);

//		System.out.println(paGoodsTargetRepository.findAllById("22407742"));
	}

}
