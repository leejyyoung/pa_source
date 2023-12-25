package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cware.partner.sync.domain.entity.PaPromoMarginAutoM;

public interface PaPromoMarginAutoMRepository extends JpaRepository<PaPromoMarginAutoM, String> {

	@Query(value = "select pma.exceptMarginRate "
			+ " from PaPromoMarginAutoM pma "
			+ " left outer join PaPromoMarginAutoGrp grp on pma.eventNo = grp.eventNo and grp.targetGb = '40' and grp.useCode = '00' "
			+ " inner join Goods g on 1 = (case when pma.goodsKindsAllYn = '1' then 1 "
			+ "                   		   when g.lmsdCode = grp.targetCode then 1 else 0  "
			+ "              				end ) "
			+ " where pma.mediaCode = :mediaCode "
			+ " and g.goodsCode = :goodsCode "
			+ " and pma.sourcingMedia = decode(substr(:paCode, 2, 3), '1', '01', '2', '61') "
			+ " and pma.useCode = '00' "
			+ " and sysdate between pma.eventBdate and pma.eventEdate "
			)
	Double getPromoMarginAuto(String goodsCode, String mediaCode, String paCode);

}
