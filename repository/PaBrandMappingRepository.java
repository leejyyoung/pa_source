package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaBrandMapping;
import com.cware.partner.sync.domain.id.PaBrandId;

@Repository
public interface PaBrandMappingRepository extends JpaRepository<PaBrandMapping, PaBrandId> {

	@Query(value="select b.paBrandNo from PaBrandMapping b where b.paGroupCode = :paGroupCode and b.brandCode = :brandCode")
	String findMappingByPaGroupCode(String paGroupCode, String brandCode);

	@Query(value="select b.brand_no from tpa11stbrand b where b.brand_code = :brandCode", nativeQuery = true)
	String find11stMapping(String brandCode);

	@Query(value="select b.brand_no from tpagmktbrand b where b.brand_code = :brandCode", nativeQuery = true)
	String findGmktMapping(String brandCode);

	@Query(value="select b.brand_no from tpawempbrand b where b.brand_code = :brandCode", nativeQuery = true)
	String findWempMapping(String brandCode);
}
