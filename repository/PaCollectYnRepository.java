package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaCollectYn;

@Repository
public interface PaCollectYnRepository extends JpaRepository<PaCollectYn, String> {
	
	@Query(value="select count(cy.pa_lmsd_key) "
			+ " from tpacollectyncate cy "
			+ " inner join tpagoodskindsmapping km on km.pa_lmsd_key = cy.pa_lmsd_key and km.pa_group_code = '05' "
			+ " where km.lmsd_code = :lmsdCode ", nativeQuery = true)
	int countCopnCollecyLmsdCnt(String lmsdCode);
}
