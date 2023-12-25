package com.cware.partner.sync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.EbayCategory;
import com.cware.partner.sync.domain.entity.PaGoodsKindsMapping;
import com.cware.partner.sync.domain.id.PaLmsdId;

@Repository
public interface PaGoodsKindsMappingRepository extends JpaRepository<PaGoodsKindsMapping, PaLmsdId> {

	List<PaGoodsKindsMapping> findByLmsdCode(String lmsdCode);

	// 이베이 표준카테고리
	@Query(value="select esm.pa_lmsdn_key as esmCategoryCode, nvl(site.dgroup, site.sgroup) as siteCategoryCode  "
			+ "  from tpaesmgoodskindsmapping esm "
			+ " inner join tpasitegoodskinds site on decode(:paGroupCode, '02', esm.pa_lmsd_key, esm.pa_iac_lmsd_key) = site.lmsd_code"
			+ " where esm.lmsd_code = :lmsdCode ", nativeQuery = true)
	EbayCategory getEbayCategory(String paGroupCode, String lmsdCode);

	// 롯데온 전시카테고리
	@Query(value="select listagg(d.disp_cat_id, ',')  "
			+ "  from tpaltonstandarddisplist d "
			+ " where d.std_cat_id = :stcCatId ", nativeQuery = true)
	String getLotteonDispalyCategory(String stcCatId);
	
}