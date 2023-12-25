package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaNaverOriginMapping;

@Repository
public interface PaNaverOriginMappingRepository extends JpaRepository<PaNaverOriginMapping, String> {

}
