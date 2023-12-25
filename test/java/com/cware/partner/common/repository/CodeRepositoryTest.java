package com.cware.partner.common.repository;

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
class CodeRepositoryTest {

	@Autowired
	private CodeRepository codeRepository;


	@Test
	void test() {
		System.out.println(codeRepository.findByCodeLgroup("B023"));
	}

	@Test
	void testCommision() {

		System.out.println(codeRepository.findCommisionByCodeLgroup("O531"));
	}


}

