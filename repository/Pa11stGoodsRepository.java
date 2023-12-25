package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.Pa11stGoods;
import com.cware.partner.sync.domain.id.PaGoodsId;

@Repository
public interface Pa11stGoodsRepository extends JpaRepository<Pa11stGoods, PaGoodsId> {

    // 판매중지처리
	// returnNote에 'BATCH'를 넣는 이유는? TODO 검토
    @Modifying
    @Query(value = "update Pa11stGoods p "
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
    @Query(value = "update Pa11stGoods p "
    		+ "  set p.transTargetYn = '1'"
    		+ " , p.modifyId = :modifyId "
    		+ " , p.modifyDate = sysdate "
    		+ " where p.goodsCode = :goodsCode "
    		+ "   and p.paCode like :paCode "
    		+ "   and p.transTargetYn = '0' ")
    int enableTransTarget(String goodsCode, String paCode, String modifyId);

    // 딜상품 전송타겟팅
    @Modifying
    @Query(value = "update tgds_alcout_deal_goods p "
    		+ "  set p.trans_target_yn = '1'"
    		+ " , p.modify_id = :modifyId "
    		+ " , p.modify_date = sysdate "
    		+ " where p.goods_code = :goodsCode "
    		+ "   and p.use_code = '00' "
    		+ "   and exists (select 1 from tgds_alcout_deal m "
    		+ "           where m.alcout_deal_code = p.alcout_deal_code "
    		+ "             and m.media_code like '%EX01%' )",
    		nativeQuery = true)
    int enableTransTargetDeal(String goodsCode, String modifyId);
}
