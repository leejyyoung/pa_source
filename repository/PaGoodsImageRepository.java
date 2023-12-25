package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.GoodsInfoImage;
import com.cware.partner.sync.domain.entity.PaGoodsImage;
import com.cware.partner.sync.domain.id.PaGroupGoodsId;

@Repository
public interface PaGoodsImageRepository extends JpaRepository<PaGoodsImage, PaGroupGoodsId> {

	@Query(value = "select i "
			+ " from GoodsInfoImage i "
			+ " where i.infoImageType = '200' "
			+ "   and i.infoImageFile1 is not null "
			+ "   and i.goodsCode = :goodsCode ")
	GoodsInfoImage getEbayGoodsImage(String goodsCode);
}
