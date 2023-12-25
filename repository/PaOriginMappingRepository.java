package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaOriginMapping;
import com.cware.partner.sync.domain.id.PaOriginId;

@Repository
public interface PaOriginMappingRepository extends JpaRepository<PaOriginMapping, PaOriginId> {

}
