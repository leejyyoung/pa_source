package com.cware.partner.sync.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.TargetExcept;
import com.cware.partner.sync.domain.entity.PaTargetExcept;

@Repository
public interface PaTargetExceptRepository extends JpaRepository<PaTargetExcept, String> {

	Optional<PaTargetExcept> findByTargetGbAndTargetCode(String targetCode, String targetGb);

	@Query(value = "select max(pa_code_all_yn) as paGroupCodeAllYn, max(pa_group_code) as paGroupCode "
			+ " from ( "
			+ " select pa_code_all_yn, '' as pa_group_code from tpatargetexcept "
			+ " where target_gb = '00' and target_code = :goodsCode and use_yn = '1' "
			+ " union all  "
			+ " select entp.pa_group_code_all_yn, entp.pa_group_code "
			+ "  from tpaexceptentp entp "
			+ "     , tpaexceptbrand brand "
			+ " where entp.entp_code = :entpCode "
			+ "   and entp.use_yn = '1' "
			+ "   and entp.entp_code = brand.entp_code(+) "
			+ "   and brand.use_yn(+) = '1' "
			+ "   and ( entp.all_brand_yn = '1' "
			+ "    or  brand.brand_code = :brandCode) "
			+ "   and (entp.sourcing_media = '00' "
			+ "    or instr(entp.sourcing_media, :sourcingMedia) > 0)"
			+ ")", nativeQuery = true)
	TargetExcept findTargetExcept(String goodsCode, String entpCode, String brandCode, String sourcingMedia);


	// 네이버 인증정보
	@Query(value="select count(ki.except_code) "
			+ " from tpanavergoodskindsinfo ki "
			+ " inner join tpagoodskindsmapping km on km.pa_lmsd_key = ki.category_id and km.pa_group_code = '04' "
			+ " where km.lmsd_code = :lmsdCode ", nativeQuery = true)
	int countNaverExceptCode(String lmsdCode);
	
	// 중고상품 업체 예외 연동
	@Query(value="select count(tg.goods_code) "
			+ "  from tgoods tg "
			+ " inner join tgoodsaddinfo ta on ta.goods_code = tg.goods_code and ta.goods_stts in ('1','2') "
			+ " inner join tcode tc on tc.code_lgroup = 'B712' and tg.entp_code = tc.code_mgroup and tc.remark2 = '1' and tc.use_yn = '1' "
			+ " where tg.goods_code = :goodsCode " , nativeQuery = true)
	int countUsedEntp(String goodsCode);
}
