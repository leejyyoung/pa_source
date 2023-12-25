package com.cware.partner.sync.repository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cware.partner.sync.domain.entity.PaSsgShipCost;
import com.cware.partner.sync.domain.id.PaSsgShipCostId;

@Repository
public interface PaSsgShipCostRepository extends JpaRepository<PaSsgShipCost, PaSsgShipCostId> {

	@Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = DataIntegrityViolationException.class)
	<S extends PaSsgShipCost> S saveAndFlush(S entity);
}
