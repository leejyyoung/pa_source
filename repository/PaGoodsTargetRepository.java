package com.cware.partner.sync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaGoodsTarget;
import com.cware.partner.sync.domain.id.PaGoodsId;

@Repository
public interface PaGoodsTargetRepository extends JpaRepository<PaGoodsTarget, PaGoodsId> {

	List<PaGoodsTarget> findByGoodsCode(String goodsCode);

    @Query(value = "select gt "
    		+ "   from PaGoodsTarget gt "
    		+ "  where gt.goodsCode = :goodsCode "
    		+ "    and gt.paGroupCode in :paGroupCodes ")
	List<PaGoodsTarget> findByGoodsCode(String goodsCode, List<String> paGroupCodes);

	@Modifying
	@Query("delete PaGoodsTarget gt where gt.goodsCode = :goodsCode and gt.paSaleGb is null")
	int deleteGoodsTarget(String goodsCode);
}
