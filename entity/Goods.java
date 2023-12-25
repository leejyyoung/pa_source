package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;

import com.cware.partner.sync.domain.TargetExcept;

import lombok.Data;

@Data
@Entity
@Table(name = "TGOODS")
public class Goods {

	@Id
	private String goodsCode;
	private String goodsName;
	private String sourcingMedia; // 소싱매체 J009
	private String sourcingCode; // 소싱코드
	private String originCode; // 원산지코드 B023
	private String shipManSeq;
	private String returnManSeq;
	private String shipCostCode; // 배송비정책코드 TSHIPCOSTDT
	private String entpCode;
	private String lgroup; // 상품대분류
	private String lmsdCode; // 상품분류코드
	private String buyMed; // 매입방법 B020
	private String entpBuyMed; // 업체매입방법 B222
	private String goodsNameMc; // 모바일전용 상품명
	private String delyType; // 배송주체 B021
	private String inviGoodsType; // 무형상품구분
	private String broadSaleYn; //방송시간외판매불가
	private String ombudsmanYn; // 옴부즈맨 여부
	private String signGb; // 결재구분[B024]
	private String saleGb; // 판매구분[B032]
	private String giftYn; // 사은품여부
	private String sqcGb; // QA결과[B123]
	private String describeSqcGb; // 웹QA결과[B123]
	private String orderMediaAllYn; // 전체주문매체
	private String orderMedia; // 주문매체[J001]
	private String mdKind; // MD분류(TMDKIND)
	private String taxYn;
	private String taxSmallYn;
	private String adultYn;
	private String keyword;
	private String collectYn;
	private String installYn;
	private int orderMinQty;
	private int orderMaxQty;
	private int custOrdQtyCheckTerm;
	private int avgDelyLeadtime;

	private Timestamp saleStartDate;
	private Timestamp saleEndDate;
	private Timestamp modifyDate;
	private String returnNoYn;

	@OneToOne(fetch=FetchType.LAZY, optional = false)
	@JoinColumn(name = "goodsCode")
	private GoodsAddInfo goodsAddInfo;

	@OneToOne(fetch=FetchType.LAZY, optional = false)
	@JoinColumn(name = "goodsCode")
	private GoodsImage goodsImage;

	@OneToOne
	@JoinColumn(name = "goodsCode")
	private PaSourcingExceptInput sourcingExceptInput;

	private String makecoCode;
    @Formula("(select m.makeco_name from tmakecomp m where m.makeco_code = makeco_code)")
	private String makecoName;

	private String brandCode;
    @Formula("(select b.brand_name from tbrand b where b.brand_code = brand_code)")
	private String brandName;

	@OneToMany
	@JoinColumn(name = "goodsCode")
	private List<GoodsDt> goodsDtList;

	@OneToMany
	@JoinColumn(name = "goodsCode")
	private List<Offer> offerList;

	@Transient
	private GoodsPrice goodsPrice;

	@Transient
	private PaCustShipCost paCustShipCost;

	@Transient
	private PaGoods paGoods;

	@Transient
	private List<PaGoodsTarget> targetList;

	@Transient
	private List<PaPromoTarget> paPromoTargetList;

	@Transient
	private TargetExcept targetExcept;

	@Transient
	private TargetExcept promoTargetExcept;

	@Transient
	private TargetExcept promoEntpBrandExcept;

	@Formula("case when dely_type = '10' then '100001' else entp_code end")
	private String shipEntpCode;

	@Formula("case when sale_start_date <= sysdate and nvl(sale_end_date, sysdate) >= sysdate then '1' else '0' end")
	private String forSale;

    @Formula("(select o.offer_type from toffer o where o.goods_Code = goods_code and o.use_yn = '1' and rownum = 1)")
    private String offerType;

    @Formula("(select case when count(1) > 0 then '1' else '0' end from tdelynoarea d where d.goods_code = goods_code and d.area_gb in ('20', '30'))")
	private String doNotIslandDelyYn;

    // 유효기술서 최종수정일시
    @Formula("(select max(d.modify_date) from tdescribe d where d.goods_code = goods_code and d.describe_code in ('200', '998', '999') "
    		+ " and (d.describe_ext is not null or d.describe_note is not null))")
    private Timestamp describeModifyDate;

    // 주문가능재고
//    @Formula("decode(sale_gb, '00', nvl(fun_get_order_able_qty(goods_code, '', wh_code), 0), 0)")
    @Formula("(select nvl(sum(decode(dt.sale_gb, '00', nvl(fun_get_order_able_qty(dt.goods_code, dt.goodsdt_code, wh_code), 0), 0)), 0) from tgoodsdt dt where dt.goods_code = goods_code) ")
	private int orderAbleQty;

	@Formula("nvl((select 1 from tgoodsssgfood f where f.goods_code = goods_code and f.use_yn = '1'), 0) ")
	private boolean isFoodInfo;

	// 폐기여부
	@Formula("case when sale_gb = '19' then 1 else 0 end")
	private boolean isDiscard;

    // 제외사유
    @Transient
    private String exceptNote;

    // 타겟팅제외여부
    @Transient
    private boolean isExcept;

    @Transient
    private long goodsSyncNo = 0;

	@Transient
	private EntpUser shipManUser;

	@Transient
	private EntpUser returnManUser;
	
	@Transient
	private int usedEntpCnt;
	
    @Formula("(select pg.pa_collect_yn from tpaSsgGoods pg where pg.goods_code = goods_code)")
	private String ssgCollectYn;
    
    @Formula("(select pb.return_no_yn from tpahalfgoods pg inner join tpahalfbrand pb on pg.pa_brand_no = pb.pa_brand_no where pg.goods_code = goods_code)")
	private String halfReturnNoYn;


}
