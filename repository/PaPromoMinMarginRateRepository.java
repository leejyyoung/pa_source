package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaPromoMinMarginRate;
import com.cware.partner.sync.domain.id.PaPromoMinMarginId;

@Repository
public interface PaPromoMinMarginRateRepository extends JpaRepository<PaPromoMinMarginRate, PaPromoMinMarginId> {

	@Query(value = "select mr.minMarginRate "
			+ " from PaPromoMinMarginRate mr "
			+ " where mr.paGroupCode = :paGroupCode "
			+ " and mr.paCode = :paCode "
			+ " and rownum = 1"
			)
	Double getPromoMinMarginRate(String paGroupCode, String paCode);
	
}
