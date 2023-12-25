package com.cware.partner.sync.repository;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cware.partner.sync.domain.entity.Pa11stOriginMapping;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class Pa11stOriginMappingTest {

	@Autowired
	private Pa11stOriginMappingRepository pa11stOriginMappingRepository;


	@Test
	void test() {

		Optional<Pa11stOriginMapping> result = pa11stOriginMappingRepository.findById("0960");
		System.out.println(result);
		System.out.println("next.....");
//		System.out.println(goods.get().getPaGoodsTargetList());
//		System.out.println("next.....");
//		System.out.println(goods.get().getGoodsAddInfo());


	}

}

