package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaPromoMarginExcept;
import com.cware.partner.sync.domain.id.PaPromoMarginExceptId;

@Repository
public interface PaPromoMarginExceptRepository extends JpaRepository<PaPromoMarginExcept, PaPromoMarginExceptId> {

	PaPromoMarginExcept findByGoodsCodeAndPaGroupCodeAndUseYn(String goodsCode, String paGroupCode, String useYn);
	
	// 프로모션 마진체크 예외 해제 (톡딜 전용)
    @Modifying
    @Query(value = "update PaPromoMarginExcept p "
    		+ " set p.useYn = '0' "
    		+ " , p.modifyId = :modifyId "
    		+ " , p.modifyDate = sysdate "
    		+ " where p.goodsCode = :goodsCode "
    		+ "   and p.talkDealPromoNo = :promoNo "
    		+ "   and p.paGroupCode = :paGroupCode ")
    int disablePromoMarginExcept(String promoNo, String goodsCode, String paGroupCode, String modifyId);
}
