package com.cware.partner.sync.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaCustCnShipCost;

@Repository
public interface PaCustCnShipCostRepository extends JpaRepository<PaCustCnShipCost, String> {

	// 레거시 로직이 잘못됨. 이 메소드 필요없음.. 삭제예정
	@Query(value = "select distinct p.entpCode "
			+ " from PaCustCnShipCost p "
			+ " where p.transCnCostYn = '1' "
			+ " and p.shipCostCode like 'CN%' "
			)
	Slice<String> findTargetList(Pageable pageRequest);

	Optional<PaCustCnShipCost> findOneByPaCodeAndEntpCodeAndEntpManSeqAndShipCostCodeOrderByCnCostSeqDesc(String paCode,
			String entpCode, String entpManSeq, String shipCostCode);

	@Query(value = "to_char(sysdate,'yyyymmdd')||ltrim(to_char(seq_cn_cost_seq.nextval,'000')) FROM dual", nativeQuery = true)
	String getNextCnCostSeq();
}
