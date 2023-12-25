package com.cware.partner.sync.repository;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaGoodsPriceApply;
import com.cware.partner.sync.domain.id.PaGoodsPriceApplyId;

@Repository
public interface PaGoodsPriceApplyRepository extends JpaRepository<PaGoodsPriceApply, PaGoodsPriceApplyId> {

	@Query(value = "select gp "
			+ " from PaGoodsPriceApply gp "
			+ "  where gp.goodsCode = :goodsCode "
			+ "    and gp.paGroupCode = :paGroupCode "
			+ "    and gp.paCode = :paCode "
			+ "    and gp.applyDate = :applyDate "
			+ "    and gp.priceApplySeq = (select max(sgp.priceApplySeq) from PaGoodsPriceApply sgp "
			+ "            where sgp.goodsCode = gp.goodsCode "
			+ "              and sgp.paGroupCode = gp.paGroupCode "
			+ "              and sgp.paCode = gp.paCode "
			+ "              and sgp.applyDate = gp.applyDate "
			+ " ) "
			)
	Optional<PaGoodsPriceApply> findGoodsPriceApply(String goodsCode, String paGroupCode, String paCode, Timestamp applyDate);


	@Query(value = "select gp.* "
			+ " from tpagoodspriceapply gp "
			+ "  where gp.goods_code = :goodsCode "
			+ "    and gp.pa_group_code = :paGroupCode "
			+ "    and gp.pa_code = :paCode "
			+ "    and (gp.apply_date, gp.price_apply_seq) = (select /*+index_desc(sgp pk_tpagoodspriceapply)*/ "
			+ "          sgp.apply_date, sgp.price_apply_seq from tpagoodspriceapply sgp "
			+ "            where sgp.goods_code = gp.goods_code "
			+ "              and sgp.pa_group_code = gp.pa_group_code "
			+ "              and sgp.pa_code = gp.pa_code "
			+ "              and sgp.apply_date <= current_date "
			+ "              and rownum = 1 "
			+ " ) "
			, nativeQuery = true
			)
	Optional<PaGoodsPriceApply> findGoodsPriceApply(String goodsCode, String paGroupCode, String paCode);

	@Query(value = " select nvl(max(gp.priceApplySeq), 0) + 1 "
			+ "   from PaGoodsPriceApply gp "
			+ "  where gp.goodsCode = :#{#priceApply.goodsCode} "
			+ "    and gp.paGroupCode = :#{#priceApply.paGroupCode} "
			+ "    and gp.paCode = :#{#priceApply.paCode} "
			+ "    and gp.applyDate = :#{#priceApply.applyDate} "
			)
	int getNextSeq(PaGoodsPriceApply priceApply);

}
