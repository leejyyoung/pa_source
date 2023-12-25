package com.cware.partner.sync.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.cware.partner.sync.domain.entity.PaHalfGoodsDtMapping;
import com.cware.partner.sync.domain.id.PaGoodsDtId;

@Repository
public interface PaHalfGoodsDtMappingRepository extends JpaRepository<PaHalfGoodsDtMapping, PaGoodsDtId> {

	@Query(value = "select dt "
			+ " from PaHalfGoodsDtMapping dt "
			+ "where dt.goodsCode = :goodsCode "
			+ " and dt.goodsdtCode = :goodsdtCode "
			+ " and dt.paCode = :paCode "
			+ " and dt.goodsdtSeq = (select max(sdt.goodsdtSeq) "
			+ " from PaHalfGoodsDtMapping sdt "
			+ " where sdt.goodsCode = dt.goodsCode "
			+ "   and sdt.goodsdtCode = dt.goodsdtCode  "
			+ "   and sdt.paCode = dt.paCode ) "
			)
	Optional<PaHalfGoodsDtMapping> findGoodsDtMapping(String paCode, String goodsCode, String goodsdtCode);

	@Query(value = " select ltrim(to_char(nvl(max(to_number(dt.goodsdtSeq)), 0) + 1, '000')) "
			+ "   from PaHalfGoodsDtMapping dt "
			+ "  where dt.goodsCode = :goodsCode "
			+ "    and dt.goodsdtCode = :goodsdtCode "
			+ "    and dt.paCode = :paCode "
			)
	String getNextSeq(String paCode, String goodsCode, String goodsdtCode);
}

