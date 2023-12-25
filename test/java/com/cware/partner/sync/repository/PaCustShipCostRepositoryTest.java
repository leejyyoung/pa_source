package com.cware.partner.sync.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cware.partner.sync.domain.entity.PaCustShipCost;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PaCustShipCostRepositoryTest {

	@Autowired
	private PaCustShipCostRepository paCustShipCostRepository;

	@Test
	void test() {

		PaCustShipCost result = paCustShipCostRepository.getCustShipCost("109165", "FR001");
		System.out.println(result);

	}


}
