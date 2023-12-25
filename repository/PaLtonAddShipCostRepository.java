package com.cware.partner.sync.repository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cware.partner.sync.domain.entity.PaLtonAddShipCost;
import com.cware.partner.sync.domain.id.PaLtonShipCostId;

@Repository
public interface PaLtonAddShipCostRepository extends JpaRepository<PaLtonAddShipCost, PaLtonShipCostId> {

	@Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = DataIntegrityViolationException.class)
	<S extends PaLtonAddShipCost> S saveAndFlush(S entity);
}
