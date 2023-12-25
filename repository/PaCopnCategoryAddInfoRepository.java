package com.cware.partner.sync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaCopnCategoryAddInfo;
import com.cware.partner.sync.domain.id.PaCopnCategoryAddInfoId;

@Repository
public interface PaCopnCategoryAddInfoRepository extends JpaRepository<PaCopnCategoryAddInfo, PaCopnCategoryAddInfoId> {

	
	@Query(value = "select new PaCopnCategoryAddInfo (gm.lmsdCode, ca.minMarginRate, ca.commission)"
			+ " from PaCopnCategoryAddInfo ca "
			+ " inner join PaGoodsKindsMapping gm on ca.paGroupCode = gm.paGroupCode and ca.paLmsdKey = gm.paLmsdKey"
			+ " where ca.paGroupCode = :paGroupCode "
			)
	List<PaCopnCategoryAddInfo> getCopnCategoryAddInfo(String paGroupCode);
	
}