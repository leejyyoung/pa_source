package com.cware.partner.sync.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cware.partner.common.code.Application;
import com.cware.partner.common.code.PaGroup;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PaKakaoGoodsImageRepositoryTest {

	@Autowired
	private PaKakaoGoodsImageRepository kakaoGoodsImageRepository;

	@Test
	void test() {
		kakaoGoodsImageRepository.insertGoodsImage("22408367", PaGroup.KAKAO.code(), Application.ID.code());

	}
}
