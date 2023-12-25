package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaGoodsDtMapping;
import com.cware.partner.sync.domain.id.PaGoodsDtId;

@Repository
public interface PaGoodsDtMappingRepository extends JpaRepository<PaGoodsDtMapping, PaGoodsDtId> {

	// 단품 조합형여부
    @Query(value = "select case when count(distinct goodsdtInfoKind) = 1 then '1' else '0' end "
    		+ " from PaGoodsDt d where d.goodsCode = :goodsCode "
    		+ " and exists (select 1 "
    		+ "  from PaGoodsDt ds "
    		+ " where ds.goodsCode = d.goodsCode and ds.goodsdtInfoKind = '색상/사이즈/' and rownum = 1) "
    		)
	String getCombinationYn(String goodsCode);

    // 전송대상여부 활성화
    @Modifying
    @Query(value = "update PaGoodsDtMapping dt set dt.transTargetYn = '1'"
    		+ " , dt.modifyId = :modifyId "
    		+ " , dt.modifyDate = sysdate "
    		+ " where dt.paCode = :paCode "
    		+ "   and dt.goodsCode = :goodsCode "
    		+ "   and dt.transTargetYn = '0' ")
    int enableTransTarget(String paCode, String goodsCode, String modifyId);
}
