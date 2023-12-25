package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaGoodsQaLog;
import com.cware.partner.sync.domain.id.PaGoodsLogId;

@Repository
public interface PaGoodsQaLogRepository extends JpaRepository<PaGoodsQaLog, PaGoodsLogId> {

	@Query(value = " select ltrim(to_char(nvl(max(to_number(l.seq)), 0) + 1, '00000')) "
			+ "   from PaGoodsQaLog l "
			+ "  where l.goodsCode = :goodsCode "
			+ "    and l.paCode = :paCode "
			)
	String getNextSeq(String paCode, String goodsCode);

}
