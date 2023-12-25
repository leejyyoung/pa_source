package com.cware.partner.sync.repository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cware.partner.sync.domain.entity.PaCustShipCost;
import com.cware.partner.sync.domain.id.PaShipCostId;

@Repository
public interface PaCustShipCostRepository extends JpaRepository<PaCustShipCost, PaShipCostId> {

	@Query(value = "  select new PaCustShipCost(cm.entpCode, cm.shipCostFlag, cm.shipCostCode, cm.shipCostBaseAmt "
			+ ", cd1.applyDate "
		    + ", cd1.ordCostAmt, cd1.retCostAmt, cd1.exchCostAmt "
			+ ", cd2.ordCostAmt, cd3.ordCostAmt) "
			+ "  from ShipCostM cm "
			+ "  inner join ShipCostDt cd1 on cm.entpCode = cd1.entpCode and cm.shipCostCode = cd1.shipCostCode and cd1.custAreaGb = '10' "
			+ "  inner join ShipCostDt cd2 on cm.entpCode = cd2.entpCode and cm.shipCostCode = cd2.shipCostCode and cd2.custAreaGb = '20' "
			+ "  inner join ShipCostDt cd3 on cm.entpCode = cd3.entpCode and cm.shipCostCode = cd3.shipCostCode and cd3.custAreaGb = '30' "
			+ " where cm.entpCode = :entpCode "
			+ "   and cm.shipCostCode = :shipCostCode "
			+ "   and cd1.applyDate = cd2.applyDate "
			+ "   and cd1.applyDate = cd3.applyDate "
			+ "   and cd1.applyDate = ( select max(cds.applyDate) from ShipCostDt cds where cds.entpCode = cm.entpCode and cds.shipCostCode = cm.shipCostCode and cds.applyDate < sysdate ) "
			)
	PaCustShipCost getCustShipCost(String entpCode, String shipCostCode);

	@Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = DataIntegrityViolationException.class)
	<S extends PaCustShipCost> S saveAndFlush(S entity);
}
