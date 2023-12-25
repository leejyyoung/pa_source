package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaTmonGoods;
import com.cware.partner.sync.domain.id.PaGoodsId;

@Repository
public interface PaTmonGoodsRepository extends JpaRepository<PaTmonGoods, PaGoodsId> {

    // 판매중지처리
    @Modifying
    @Query(value = "update PaTmonGoods p "
    		+ " set p.transSaleYn = '1' "
    		+ " , p.paSaleGb = '30' "
    		+ " , p.modifyId = :modifyId "
    		+ " , p.modifyDate = sysdate "
    		+ " where p.goodsCode = :goodsCode "
    		+ "   and p.paCode like :paCode "
    		+ "   and p.paSaleGb = '20' "
    		+ "   and p.paStatus = '30' ")
    int stopSale(String goodsCode, String paCode, String modifyId);

    // 전송대상여부 활성화
    @Modifying
    @Query(value = "update PaTmonGoods p "
    		+ "  set p.transTargetYn = '1'"
    		+ " , p.modifyId = :modifyId "
    		+ " , p.modifyDate = sysdate "
    		+ " where p.goodsCode = :goodsCode "
    		+ "   and p.transTargetYn = '0' ")
    int enableTransTarget(String goodsCode, String modifyId);
}
