package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaGmktOriginMapping;

@Repository
public interface PaGmktOriginMappingRepository extends JpaRepository<PaGmktOriginMapping, String> {

}
