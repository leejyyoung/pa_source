package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.Pa11stOriginMapping;

@Repository
public interface Pa11stOriginMappingRepository extends JpaRepository<Pa11stOriginMapping, String> {

}
