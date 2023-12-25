package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.GoodsPrice;

@Repository
public interface GoodsPriceRepository extends JpaRepository<GoodsPrice, String> {

	@Query(value = "select gp "
			+ " from GoodsPrice gp "
			+ "where gp.goodsCode = :goodsCode "
			+ " and gp.applyDate = (select max(sgp.applyDate) from GoodsPrice sgp where sgp.goodsCode = gp.goodsCode and sgp.applyDate <= current_date) "
			)
	GoodsPrice findApplyGoodsPrice(String goodsCode);
}
