package com.cware.partner.sync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.TargetExcept;
import com.cware.partner.sync.domain.entity.PaPromoTarget;
import com.cware.partner.sync.domain.id.PaPromoId;

@Repository
public interface PaPromoTargetRepository extends JpaRepository<PaPromoTarget, PaPromoId> {

	// TODO: 2022년 1월6일 이후부터는 쿠폰프로모션 적용 안하고, 제휴프로모션만 적용됨.
	// 신규 프로모션은 타겟팅 되면 안됨
	@Query(value = " select new PaPromoTarget(p.promoNo, t.goodsCode, p.doType, p.couponYn, p.immediatelyYn "
			+ " , p.promoBdate, p.promoEdate, p.amtRateFlag, p.doRate, p.doAmt "
			+ " , p.standardAppAmt, p.limitAmt, p.useCode, t.ownCost, t.entpCost "
			+ " , p.mediaCode, p.alcoutPromoYn, a.paMarginExceptYn, nvl(pa.talkDealYn, '0')) "
			+ " from PromoM p "
			+ " inner join PromoTarget t on p.promoNo = t.promoNo "
			+ " inner join PromoAddInfo a on p.promoNo = a.promoNo "
			+ " left outer join PaPromoAddInfo pa on p.promoNo = pa.promoNo "
			+ "  where p.couponYn = '1' "
			+ "    and p.doType = '30' "
			+ "    and p.immediatelyYn = '1' "
			+ "    and p.useCode = '00' "
			+ "    and sysdate between p.promoBdate and p.promoEdate "
//			+ "    and p.mediaCode like 'EX%' "
			+ "    and p.alcoutPromoYn = '1' "
			+ "    and t.goodsCode = :goodsCode "
			+ " order by p.promoNo "
			)
	List<PaPromoTarget> selectPromoTarget(String goodsCode);


	@Query(value = " select ltrim(to_char(nvl(max(to_number(t.seq)), 0) + 1, '0000')) "
			+ "   from tpapromotarget t "
			+ "  where t.goods_code = :goodsCode "
			+ "    and t.promo_no = :promoNo "
			+ "    and t.pa_code = :paCode "
			, nativeQuery = true
			)
	String getNextSeq(String promoNo, String paCode, String goodsCode);


	@Query(value = " select pa_group_code_all_yn as paGroupCodeAllYn, pa_group_code as paGroupCode , pa_except_margin as paExceptMargin  "
			+ " from tpapromotargetexcept "
			+ " where target_gb = '00' and target_code = :goodsCode and use_yn = '1' "
			+ "   and sysdate between pa_except_bdate and pa_except_edate "
			+ "   and rownum =  1 "
			, nativeQuery = true)
	TargetExcept findTargetExcept(String goodsCode);

	@Query(value = " select pe.pa_group_code_all_yn as paGroupCodeAllYn, pe.pa_group_code as paGroupCode "
			+ " from tpapromoexceptentp pe"
			+ " inner join tgoods gd on gd.entp_code = pe.entp_code "
			+ " left outer join tpapromoexceptbrand pb on pb.entp_code = pe.entp_code and pb.use_yn = '1' and pb.brand_code = gd.brand_code and sysdate between pb.pa_except_bdate and pb.pa_except_edate "
			+ " where ( ( pb.entp_code is not null ) or ( pe.all_brand_yn = '1' and sysdate between pe.pa_except_bdate and pe.pa_except_edate ) ) "
			+ "   and pe.use_yn = '1' "
			+ "   and gd.goods_code = :goodsCode "
			+ "	  and rownum  = 1 "  , nativeQuery = true)
	TargetExcept findTargetExceptEntpNBrand(String goodsCode);
	

	@Query(value = " select * "
			+ " from tpapromotarget p "
			+ " where p.pa_code = :paCode "
			+ "   and p.goods_code = :goodsCode "
			+ "   and (p.pa_group_code is null or p.pa_group_code = :paGroupCode) "
			+ "   and (p.promo_no, p.seq) = "
			+ " (select /*+index_desc(pp pk_tpapromotarget)*/ "
			+ "         pp.promo_no, pp.seq "
			+ "    from tpapromotarget pp "
			+ "   where pp.pa_code = p.pa_code "
			+ "     and pp.goods_code = p.goods_code "
			+ "     and pp.promo_no = p.promo_no "
			+ "     and (pp.pa_group_code is null or pp.pa_group_code = p.pa_group_code ) "
			+ "     and rownum = 1 ) "
			, nativeQuery = true
			)
	List<PaPromoTarget> selectPaPromoTarget(String paGroupCode, String paCode, String goodsCode);

}
