package com.cware.partner.sync.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cware.partner.sync.domain.entity.PaEntpSlip;
import com.cware.partner.sync.domain.id.PaEntpUserId;

@Repository
public interface PaEntpSlipRepository extends JpaRepository<PaEntpSlip, PaEntpUserId> {

	// 업체주소변경리스트
	@Query(value = "select p "
			+ " from PaEntpSlip p "
			+ " inner join EntpUser e on p.entpCode = e.entpCode and p.entpManSeq = e.entpManSeq and p.lastSyncDate <= e.modifyDate "
			+ " where p.paCode in (:paCodes) "
			)
	Slice<PaEntpSlip> findModifyTargetList(Pageable pageRequest, List<String> paCodes);

	// 변경주소 상품목록
	@Query(value = "select distinct p.goodsCode "
			+ " from PaGoods p "
			+ " inner join EntpGoods g on p.goodsCode = g.goodsCode "
			+ " inner join PaGoodsTarget t on p.goodsCode = t.goodsCode and t.paCode = :paCode "
			+ " where g.entpCode = :entpCode "
			+ "  and (g.shipManSeq = :entpManSeq or g.returnManSeq = :entpManSeq) "
			+ "  and g.saleGb = '00' "
			)
	Slice<String> findGoodsTargetList(String entpCode, String entpManSeq, String paCode, Pageable pageRequest);

	// 당사배송 변경주소 상품목록
	@Query(value = " select distinct p.goodsCode "
			+ " from PaGoods p "
			+ " inner join EntpGoods g on p.goodsCode = g.goodsCode "
			+ " inner join PaGoodsTarget t on p.goodsCode = t.goodsCode and t.paCode = :paCode  "
			+ " where g.delyType = '10' and g.saleGb = '00' "
			)
	Slice<String> findCenterGoodsTargetList( String paCode, Pageable pageRequest);

    // 관련 상품전송대상여부 활성화 (11번가)
	@Transactional
    @Modifying
    @Query(value = "update Pa11stGoods p "
    		+ "  set p.transTargetYn = '1'"
    		+ " , p.modifyId = :modifyId "
    		+ " , p.modifyDate = :syncDate "
    		+ " where p.goodsCode in :goodsCodes "
    		+ "   and p.transTargetYn = '0' "
    		+ "   and p.paCode = :paCode ")
    int enable11stGoodsTransTarget(List<String> goodsCodes, String paCode, Timestamp syncDate, String modifyId);

    // 관련 상품전송대상여부 활성화 (이베이)
	@Transactional
    @Modifying
    @Query(value = "update PaGmktGoods p "
    		+ "  set p.transTargetYn = '1'"
    		+ " , p.modifyId = :modifyId "
    		+ " , p.modifyDate = :syncDate "
    		+ " where p.goodsCode in :goodsCodes  "
    		+ "   and p.transTargetYn = '0' "
    		+ "   and p.paCode = :paCode ")
    int enableEbayGoodsTransTarget(List<String> goodsCodes, String paCode, Timestamp syncDate, String modifyId);

    // 관련 상품전송대상여부 활성화 (네이버)
	@Transactional
    @Modifying
    @Query(value = "update PaNaverGoods p "
    		+ "  set p.transTargetYn = '1'"
    		+ " , p.modifyId = :modifyId "
    		+ " , p.modifyDate = :syncDate "
    		+ " where p.goodsCode in :goodsCodes  "
    		+ "   and p.transTargetYn = '0' "
    		+ "   and p.paCode = :paCode ")
    int enableNaverGoodsTransTarget(List<String> goodsCodes, String paCode, Timestamp syncDate, String modifyId);

    // 관련 상품전송대상여부 활성화 (쿠팡)
	@Transactional
    @Modifying
    @Query(value = "update PaCopnGoods p "
    		+ "  set p.transTargetYn = '1'"
    		+ " , p.modifyId = :modifyId "
    		+ " , p.modifyDate = :syncDate "
    		+ " where p.goodsCode in :goodsCodes  "
    		+ "   and p.transTargetYn = '0' "
    		+ "   and p.paCode = :paCode ")
    int enableCopnGoodsTransTarget(List<String> goodsCodes, String paCode, Timestamp syncDate, String modifyId);

    // 관련 상품전송대상여부 활성화 (인터파크)
	@Transactional
    @Modifying
    @Query(value = "update PaIntpGoods p "
    		+ "  set p.transTargetYn = '1'"
    		+ " , p.modifyId = :modifyId "
    		+ " , p.modifyDate = :syncDate "
    		+ " where p.goodsCode in :goodsCodes  "
    		+ "   and p.transTargetYn = '0' "
    		+ "   and p.paCode = :paCode ")
    int enableIntpGoodsTransTarget(List<String> goodsCodes, String paCode, Timestamp syncDate, String modifyId);

    // 관련 상품전송대상여부 활성화 (롯데온)
	@Transactional
    @Modifying
    @Query(value = "update PaLtonGoods p "
    		+ "  set p.transTargetYn = '1'"
    		+ " , p.modifyId = :modifyId "
    		+ " , p.modifyDate = :syncDate "
    		+ " where p.goodsCode in :goodsCodes  "
    		+ "   and p.transTargetYn = '0' "
    		+ "   and p.paCode = :paCode ")
    int enableLtonGoodsTransTarget(List<String> goodsCodes, String paCode, Timestamp syncDate, String modifyId);

    // 관련 상품전송대상여부 활성화 (티몬)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    @Modifying
    @Query(value = "update PaTmonGoods p "
    		+ "  set p.transTargetYn = '1'"
    		+ " , p.modifyId = :modifyId "
    		+ " , p.modifyDate = :syncDate "
    		+ " where p.goodsCode in :goodsCodes  "
    		+ "   and p.transTargetYn = '0' "
    		+ "   and p.paCode = :paCode ")
    int enableTmonGoodsTransTarget(List<String> goodsCodes, String paCode, Timestamp syncDate, String modifyId);

    // 관련 상품전송대상여부 활성화 (쓱닷컴)
	@Transactional
    @Modifying
    @Query(value = "update PaSsgGoods p "
    		+ "  set p.transTargetYn = '1'"
    		+ " , p.modifyId = :modifyId "
    		+ " , p.modifyDate = :syncDate "
    		+ " where p.goodsCode in :goodsCodes  "
    		+ "   and p.transTargetYn = '0' "
    		+ "   and p.paCode = :paCode ")
    int enableSsgGoodsTransTarget(List<String> goodsCodes, String paCode, Timestamp syncDate, String modifyId);
}

