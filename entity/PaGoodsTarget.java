package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;

import com.cware.partner.sync.domain.PartnerBase;
import com.cware.partner.sync.domain.id.PaGoodsId;

import lombok.Data;

@Data
@Entity
@Table(name="TPAGOODSTARGET")
@IdClass(PaGoodsId.class)
public class PaGoodsTarget {

	@Id
	private String paGroupCode; // 대표제휴사코드 O500

	@Id
	private String paCode; // 제휴사코드 O501

	@Id
	private String goodsCode;

	private String paSaleGb; // 제휴사판매상태

	private String autoYn;

	@Column(updatable=false)
	private Timestamp insertDate;

	@Transient
	private String mediaCode;

	@Transient
	private int partnerStockQty;

	@Transient
	private PartnerBase partnerBase; // 제휴사 기준정보

	@Transient
	private PartnerGoods partnerGoods;

	private Timestamp modifyDate;
	private String modifyId;

    @Formula("(select case when count(1) > 0 then '1' else '0' end "
    		+ "from tpagoodsevent e "
    		+ "where e.goods_code = goods_code and e.pa_group_code = pa_group_code and e.use_yn = '1' "
    		+ "  and e.start_date <= sysdate and (e.end_date > sysdate or e.end_date is null))")
	private String eventYn;

    @Formula("(select case when count(1) > 0 then '1' else '0' end "
    		+ "from tpagoodsevent e "
    		+ "where e.goods_code = goods_code and e.pa_group_code = pa_group_code and e.use_yn = '1' "
    		+ "  and e.start_date > sysdate )")
	private String preEventYn; // 예정된 행사여부

    // 제외사유
    @Transient
    private String exceptNote;

    // 타겟팅제외여부
    @Transient
    private boolean isExcept;

    @Transient
    private long goodsSyncNo;

}


