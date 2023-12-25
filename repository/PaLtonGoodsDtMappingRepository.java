package com.cware.partner.sync.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaLtonGoodsDtMapping;
import com.cware.partner.sync.domain.id.PaGoodsDtId;

@Repository
public interface PaLtonGoodsDtMappingRepository extends JpaRepository<PaLtonGoodsDtMapping, PaGoodsDtId> {

	@Query(value = "select dt "
			+ " from PaLtonGoodsDtMapping dt "
			+ "where dt.goodsCode = :goodsCode "
			+ " and dt.goodsdtCode = :goodsdtCode "
			+ " and dt.paCode = :paCode "
			+ " and dt.goodsdtSeq = (select max(sdt.goodsdtSeq) "
			+ " from PaLtonGoodsDtMapping sdt "
			+ " where sdt.goodsCode = dt.goodsCode "
			+ "   and sdt.goodsdtCode = dt.goodsdtCode  "
			+ "   and sdt.paCode = dt.paCode ) "
			)
	Optional<PaLtonGoodsDtMapping> findGoodsDtMapping(String paCode, String goodsCode, String goodsdtCode);

	@Query(value = " select ltrim(to_char(nvl(max(to_number(dt.goodsdtSeq)), 0) + 1, '000')) "
			+ "   from PaLtonGoodsDtMapping dt "
			+ "  where dt.goodsCode = :goodsCode "
			+ "    and dt.goodsdtCode = :goodsdtCode "
			+ "    and dt.paCode = :paCode "
			)
	String getNextSeq(String paCode, String goodsCode, String goodsdtCode);
}

