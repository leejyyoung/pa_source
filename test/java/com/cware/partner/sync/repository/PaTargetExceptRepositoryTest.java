package com.cware.partner.sync.repository;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cware.partner.common.code.ExceptSourcingCode;
import com.cware.partner.sync.domain.TargetExcept;
import com.cware.partner.sync.domain.entity.PaTargetExcept;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PaTargetExceptRepositoryTest {

	@Autowired
	private PaTargetExceptRepository paTargetExceptRepository;

	@Test
	void test() {

		Optional<PaTargetExcept> except = paTargetExceptRepository.findByTargetGbAndTargetCode("00", "22407917");
		System.out.println(except);
		System.out.println(Arrays.stream(ExceptSourcingCode.values()).anyMatch(v -> v.name().equals("HALFCLUB1")));

	}

	@Test
	void testTargetExcept() {

		TargetExcept except = paTargetExceptRepository.findTargetExcept("20007449", "100316", "000083", "61");
		System.out.println(except.getPaGroupCodeAllYn());
		System.out.println(except.getPaGroupCode());

	}

	@Test
	void testNaverTargetExcept() {

		System.out.println(paTargetExceptRepository.countNaverExceptCode("60032602"));

	}
}
