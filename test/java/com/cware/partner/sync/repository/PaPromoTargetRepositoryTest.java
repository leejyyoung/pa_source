package com.cware.partner.sync.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cware.partner.sync.domain.TargetExcept;
import com.cware.partner.sync.domain.entity.PaGoodsPriceApply;
import com.cware.partner.sync.domain.entity.PaPromoTarget;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PaPromoTargetRepositoryTest {

	@Autowired
	private PaPromoTargetRepository paPromoTargetRepository;

	@Autowired
	private PaGoodsPriceApplyRepository goodsPriceApplyRepository;
	
	@Test
	void test() {

		List<PaPromoTarget> result = paPromoTargetRepository.selectPromoTarget("22408027");
		System.out.println(result);

	}


	@Test
	void testPaPromoTarget() {

		List<PaPromoTarget> result = paPromoTargetRepository.selectPaPromoTarget( "05", "51", "21851873");
		System.out.println(result);

	}

	@Test
	void testFindGoodsPriceApply() {

		Optional<PaGoodsPriceApply> result = goodsPriceApplyRepository.findGoodsPriceApply( "20018614","03", "22");
		System.out.println(result);

	}

	@Test
	void testPaPromoExcetpTarget() {
		TargetExcept result = paPromoTargetRepository.findTargetExcept( "21851873");
		System.out.println(result);
	}
	
	@Test
	void testPaPromoExceptEntpNBrandTarget() {
		TargetExcept result = paPromoTargetRepository.findTargetExceptEntpNBrand( "21851873");
		System.out.println(result);
	}
	

}
