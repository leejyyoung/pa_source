package com.cware.partner.sync.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cware.partner.sync.domain.entity.PaWempEntpSlip;
import com.cware.partner.sync.domain.id.PaEntpCostId;

@Repository
public interface PaWempEntpSlipRepository extends JpaRepository<PaWempEntpSlip, PaEntpCostId> {

	// 업체주소변경리스트
	@Query(value = "select p "
			+ " from PaWempEntpSlip p "
			+ "inner join EntpUser e on p.entpCode = e.entpCode and p.entpManSeq = e.entpManSeq and p.lastSyncDate <= e.modifyDate "
			)
	Slice<PaWempEntpSlip> findModifyTargetList(Pageable pageRequest);

	// 변경주소 상품목록
	@Query(value = "select p.goodsCode "
			+ " from PaGoods p "
			+ " inner join EntpGoods g on p.goodsCode = g.goodsCode "
			+ " inner join PaGoodsTarget t on p.goodsCode = t.goodsCode and t.paCode = :paCode "
			+ " where g.entpCode = :entpCode and :entpManSeq in (g.shipManSeq, g.returnManSeq) "
			)
	Slice<String> findGoodsTargetList(String entpCode, String entpManSeq,  String paCode, Pageable pageRequest);

	// 당사배송 변경주소 상품목록
	@Query(value = "select p.goodsCode "
			+ " from PaGoods p "
			+ " inner join EntpGoods g on p.goodsCode = g.goodsCode "
			+ " inner join PaGoodsTarget t on p.goodsCode = t.goodsCode and t.paCode = :paCode "
			+ " where g.delyType = '10' "
			)
	Slice<String> findCenterGoodsTargetList(String paCode, Pageable pageRequest);

    // 관련 상품전송대상여부 활성화 (위메프)
	@Transactional
    @Modifying
    @Query(value = "update PaWempGoods p "
    		+ "  set p.transTargetYn = '1'"
    		+ " , p.modifyId = :modifyId "
    		+ " , p.modifyDate = :syncDate "
    		+ " where p.goodsCode in :goodsCodes  "
    		+ "   and p.paCode = :paCode ")
    int enableWempGoodsTransTarget(List<String> goodsCodes, String paCode, Timestamp syncDate, String modifyId);

}

