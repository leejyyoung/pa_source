package com.cware.partner.sync.repository;

import java.sql.Timestamp;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cware.partner.sync.domain.entity.PaHalfShipInfo;
import com.cware.partner.sync.domain.id.PaHalfShipInfoId;

@Repository
public interface PaHalfShipInfoRepository extends JpaRepository<PaHalfShipInfo, PaHalfShipInfoId> {

	@Query(value = "select s.shipCostCode "
			+ " from PaHalfShipInfo s "
			+ " inner join PaCustShipCost p on s.entpCode = p.entpCode and s.paCode = p.paCode and s.shipCostCode = p.shipCostCode "
			+ " where p.transTargetYn = '1' "
			+ "   and s.transTargetYn = '0' "
			+ "   and s.paCode 		  = :paCode   "
			+ "   and s.entpCode 	  = :entpCode "
			)
	Slice<String> findTransTargetList(String paCode, String entpCode, Pageable pageRequest);
	/*	
    // TPAHALFSHIPINFO.TRANS_TARGET_YN 활성화
	@Transactional
    @Modifying
    @Query(value = "update PaHalfShipInfo p "
    		+ "  set p.transTargetYn = '1'"
    		+ " , p.modifyId = :modifyId "
    		+ " , p.modifyDate = :syncDate "
    		+ " where p.paCode 		 = :paCode   "
    		+ "   and p.entpCode 	 = :entpCode "
    		+ "   and p.shipCostCode in :shipCostCode" )
    int enableHalfShipInfoTransTarget(List<String> shipCostCode, String paCode, String entpCode , Timestamp syncDate, String modifyId);
	*/
	
    // TPAHALFSHIPINFO.TRANS_TARGET_YN 활성화
	@Transactional
    @Modifying
    @Query(value = "update PaHalfShipInfo p "
    		+ "  set p.transTargetYn = '1'"
    		+ " , p.modifyId = :modifyId "
    		+ " , p.modifyDate = :syncDate "
    		+ " , p.lastShipCostSyncDate = :syncDate "
    		+ " where p.paCode 		 = :paCode   "
    		+ "   and p.entpCode 	 = :entpCode "
    		+ "   and p.shipCostCode in :shipCostCode" )
    int enableHalfShipInfoTransTarget(String shipCostCode, String paCode, String entpCode , Timestamp syncDate, String modifyId);
	
	// 업체주소변경리스트
	@Query(value = "select p "
			+ "  from PaHalfShipInfo p "
			+ " inner join EntpUser e on p.entpCode = e.entpCode and p.shipManSeq   = e.entpManSeq " //and p.lastSyncDate <= e.modifyDate "
			+ " inner join EntpUser r on p.entpCode = r.entpCode and p.returnManSeq = r.entpManSeq " //and p.lastSyncDate <= r.modifyDate "
			+ " where (p.lastEntpSyncDate  <= e.modifyDate) OR  (p.lastEntpSyncDate  <= r.modifyDate)"
			)
	Slice<PaHalfShipInfo> findModifyTargetList(Pageable pageRequest);
}
