package com.cware.partner.sync.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaSsgGoodsDtMapping;
import com.cware.partner.sync.domain.id.PaGoodsDtId;

@Repository
public interface PaSsgGoodsDtMappingRepository extends JpaRepository<PaSsgGoodsDtMapping, PaGoodsDtId> {

	@Query(value = "select dt "
			+ " from PaSsgGoodsDtMapping dt "
			+ "where dt.goodsCode = :goodsCode "
			+ " and dt.goodsdtCode = :goodsdtCode "
			+ " and dt.paCode = :paCode "
			+ " and dt.goodsdtSeq = (select max(sdt.goodsdtSeq) "
			+ " from PaSsgGoodsDtMapping sdt "
			+ " where sdt.goodsCode = dt.goodsCode "
			+ "   and sdt.goodsdtCode = dt.goodsdtCode  "
			+ "   and sdt.paCode = dt.paCode ) "
			)
	Optional<PaSsgGoodsDtMapping> findGoodsDtMapping(String paCode, String goodsCode, String goodsdtCode);

	@Query(value = " select ltrim(to_char(nvl(max(to_number(dt.goodsdtSeq)), 0) + 1, '0000')) "
			+ "   from PaSsgGoodsDtMapping dt "
			+ "  where dt.goodsCode = :goodsCode "
			+ "    and dt.goodsdtCode = :goodsdtCode "
			+ "    and dt.paCode = :paCode "
			)
	String getNextSeq(String paCode, String goodsCode, String goodsdtCode);
}

