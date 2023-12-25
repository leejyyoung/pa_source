package com.cware.partner.sync.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PaPromoMarginAutoMRepositoryTest {

	@Autowired
	private PaPromoMarginAutoMRepository paPromoMarginAutoMRepository;
	
	@Test
	void test() {

		Double result = paPromoMarginAutoMRepository.getPromoMarginAuto("22412080", "EX05", "01");
		System.out.println(result);

	}

}
