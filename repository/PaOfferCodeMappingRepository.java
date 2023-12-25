package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaOfferCodeMapping;
import com.cware.partner.sync.domain.id.PaOfferId;

@Repository
public interface PaOfferCodeMappingRepository extends JpaRepository<PaOfferCodeMapping, PaOfferId> {

}
