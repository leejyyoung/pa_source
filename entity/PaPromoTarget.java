package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;

import com.cware.partner.sync.domain.id.PaPromoId;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@DynamicInsert
@Table(name = "TPAPROMOTARGET")
@IdClass(PaPromoId.class)
public class PaPromoTarget {

	@Id
	private String promoNo; // 프로모션코드
	@Id
	private String paCode; // 제휴사코드
	@Id
	private String goodsCode; // 상품코드
	@Id
	private String seq; // 프로모션코드+제휴사코드+상품코드별 SEQ 001

	private String doType; // 혜택구분[B007]
	private String couponYn; // 쿠폰프로모션여부
	private String immediatelyYn; // 즉시할인여부
	private Date promoBdate; // 프로모션시작일
	private Date promoEdate; // 프로모션종료일

	private String amtRateFlag; // 1: 금액/ 2: 비율
	private double doRate; // 혜택비율
	private double doAmt; // 혜택금액
	private double standardAppAmt; // 적용기준금액
	private double limitAmt; // 제한금액
	private String useCode; // 프로모션진행상태[B064]
	private double ownCost; // 당사부담금액/비율
	private double entpCost; // 업체부담금액/비율
//	private double doCost; // 최종할인금액(정률일경우sale_price-dc_amt후의금액)
//	private double doOwnCost; // 최종당사부담금액
//	private double doEntpCost; // 최종업체부담금액

	private String paGroupCode;
	private String procGb;

	@Column(updatable=false)
	private String insertId;
	@Column(updatable=false)
	private Timestamp insertDate;
	private String modifyId;
	private Timestamp modifyDate;
	private Timestamp transDate;

	@Transient
	private String mediaCode;

	@Transient
	private String alcoutPromoYn;
	
	@Transient
	private String paMarginExceptYn;
	
	@Transient
	private String talkDealYn;

	public PaPromoTarget(String promoNo, String goodsCode, String doType, String couponYn, String immediatelyYn, Date promoBdate,
			Date promoEdate, String amtRateFlag, double doRate, double doAmt, double standardAppAmt,
			double limitAmt, String useCode, double ownCost, double entpCost, String mediaCode, String alcoutPromoYn, String paMarginExceptYn,
			String talkDealYn) {
		this.promoNo = promoNo;
		this.goodsCode = goodsCode;
		this.doType = doType;
		this.couponYn = couponYn;
		this.immediatelyYn = immediatelyYn;
		this.promoBdate = promoBdate;
		this.promoEdate = promoEdate;
		this.amtRateFlag = amtRateFlag;
		this.doRate = doRate;
		this.doAmt = doAmt;
		this.standardAppAmt = standardAppAmt;
		this.limitAmt = limitAmt;
		this.useCode = useCode;
		this.ownCost = ownCost;
		this.entpCost = entpCost;
		this.mediaCode = mediaCode;
		this.alcoutPromoYn = alcoutPromoYn;
		this.paMarginExceptYn = paMarginExceptYn;
		this.talkDealYn = talkDealYn;
	}
}
