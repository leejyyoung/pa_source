package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaEntpSlipChange;
import com.cware.partner.sync.domain.id.PaEntpChangeId;

@Repository
public interface PaEntpSlipChangeRepository extends JpaRepository<PaEntpSlipChange, PaEntpChangeId> {

	int countByEntpCodeAndEntpManSeq(String entpCode, String entpManSeq);
}

