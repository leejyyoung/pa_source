package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaMakerMapping;
import com.cware.partner.sync.domain.id.PaMakerId;

@Repository
public interface PaMakerMappingRepository extends JpaRepository<PaMakerMapping, PaMakerId> {

	@Query(value="select m.paMakerNo from PaMakerMapping m where m.paGroupCode = :paGroupCode and m.makerCode = :makerCode")
	String findMappingByPaGroupCode(String paGroupCode, String makerCode);

}
