package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaGmktMaker;

@Repository
public interface PaGmktMakerRepository extends JpaRepository<PaGmktMaker, String> {

	@Query(value="select m.makerNo from PaGmktMaker m where m.makerCode = :makerCode")
	String getMakerNo(String makerCode);

}
