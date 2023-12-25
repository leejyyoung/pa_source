package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaGoodsKindsRequired;
import com.cware.partner.sync.domain.id.PaGoodsKindsRequiredId;

@Repository
public interface PaSsgRequiredInfoRepository extends JpaRepository<PaGoodsKindsRequired, PaGoodsKindsRequiredId> {

	// SSG 표시단위 입력 필수 카테고리
	@Query(value = "select count(kr.lmsdCode) "
			+ " from PaGoodsKindsRequired kr "
			+ " where kr.paGroupCode = :paGroupCode "
			+ " and kr.requiredGb = '01' "
			+ " and kr.useYn = '1' "
			+ " and kr.lmsdCode = :lmsdCode")
	int countSsgRequiredInfo(String paGroupCode, String lmsdCode);
}
