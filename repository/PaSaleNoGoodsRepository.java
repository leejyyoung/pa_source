package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaSaleNoGoods;
import com.cware.partner.sync.domain.id.PaSaleNoId;

@Repository
public interface PaSaleNoGoodsRepository extends JpaRepository<PaSaleNoGoods, PaSaleNoId> {

	@Query(value = " select ltrim(to_char(nvl(max(to_number(p.seqNo)), 0) + 1, '00000')) "
			+ "   from PaSaleNoGoods p "
			+ "  where p.goodsCode = :goodsCode "
			+ "    and p.paGroupCode = :paGroupCode "
			+ "    and p.paCode = :paCode "
			)
	String getNextSeq(String paGroupCode, String paCode, String goodsCode);

}
