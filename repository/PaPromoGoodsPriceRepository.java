package com.cware.partner.sync.repository;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaPromoGoodsPrice;
import com.cware.partner.sync.domain.id.PaPromoPriceId;

@Repository
public interface PaPromoGoodsPriceRepository extends JpaRepository<PaPromoGoodsPrice, PaPromoPriceId> {


	@Query(value = " select ltrim(to_char(nvl(max(to_number(p.promoSeq)), 0) + 1, '0000')) "
			+ "   from PaPromoGoodsPrice p "
			+ " where p.goodsCode = :#{#promoPrice.goodsCode} "
			+ "   and p.paCode = :#{#promoPrice.paCode} "
			+ "   and p.applyDate = :#{#promoPrice.applyDate} "
			)
	String getNextSeq(PaPromoGoodsPrice promoPrice);

	@Query(value = "select gp "
			+ " from PaPromoGoodsPrice gp "
			+ "  where gp.goodsCode = :goodsCode "
			+ "    and gp.paCode = :paCode "
			+ "    and gp.applyDate = :applyDate "
			+ "    and gp.promoSeq = (select max(sgp.promoSeq) from PaPromoGoodsPrice sgp "
			+ "            where sgp.goodsCode = gp.goodsCode "
			+ "              and sgp.paCode = gp.paCode "
			+ "              and sgp.applyDate = gp.applyDate "
			+ " ) "
			)
	Optional<PaPromoGoodsPrice> findPaPromoGoodsPrice(String goodsCode, String paCode, Timestamp applyDate);
}
