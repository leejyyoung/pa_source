package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaGoods;

@Repository
public interface PaGoodsRepository extends JpaRepository<PaGoods, String> {

	// goods로 이동
//	@Query(value = "select case when count(1) > 0 then '1' else '0' end from tdelynoarea where goods_code = :goodsCode and area_gb in ('20', '30')", nativeQuery = true)
//	String getDoNotIslandDelyYn(String goodsCode);


}
