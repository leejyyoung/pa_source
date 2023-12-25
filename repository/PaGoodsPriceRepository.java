package com.cware.partner.sync.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaGoodsPrice;
import com.cware.partner.sync.domain.id.PaGoodsPriceId;

@Repository
public interface PaGoodsPriceRepository extends JpaRepository<PaGoodsPrice, PaGoodsPriceId> {

	@Query(value = "select gp "
			+ " from PaGoodsPrice gp "
			+ "where gp.paCode = :paCode "
			+ " and gp.goodsCode = :goodsCode "
			+ " and gp.applyDate = (select max(sgp.applyDate) "
			+ " from PaGoodsPrice sgp where sgp.goodsCode = gp.goodsCode and sgp.paCode = gp.paCode and sgp.applyDate <= current_date) "
			)
	Optional<PaGoodsPrice> findApplyGoodsPrice(String paCode, String goodsCode);


	@Query(value = "select gp "
			+ " from PaGoodsPrice gp "
			+ "where gp.paCode = :paCode "
			+ " and gp.goodsCode = :goodsCode "
			+ " and gp.applyDate = (select max(sgp.applyDate) "
			+ " from PaGoodsPrice sgp where sgp.goodsCode = gp.goodsCode and sgp.paCode = gp.paCode "
			+ " and sgp.applyDate <= current_date and sgp.transDate is not null ) "
			)
	Optional<PaGoodsPrice> findTransApplyGoodsPrice(String paCode, String goodsCode);


}
