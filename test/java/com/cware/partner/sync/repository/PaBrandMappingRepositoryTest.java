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
class PaBrandMappingRepositoryTest {

	@Autowired
	private PaBrandMappingRepository paBrandMappingRepository;

	@Test
	void test() {
		String brand = paBrandMappingRepository.findMappingByPaGroupCode("10", "0000");
		System.out.println(brand);
	}

}
