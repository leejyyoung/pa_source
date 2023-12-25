package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;

import com.cware.partner.sync.domain.id.PaGoodsPriceApplyId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicInsert
@Table(name = "TPAGOODSPRICEAPPLY")
@IdClass(PaGoodsPriceApplyId.class)
public class PaGoodsPriceApply  {

	@Id
	private String goodsCode;
	@Id
	private String paGroupCode;
	@Id
	private String paCode;
	@Id
	private Timestamp applyDate;
	@Id
	private int priceApplySeq;

	private String priceSeq;
	private double salePrice;
	private double arsDcAmt;
	private double arsOwnDcAmt;
	private double arsEntpDcAmt;
	private double lumpSumDcAmt;
	private double lumpSumOwnDcAmt;
	private double lumpSumEntpDcAmt;

	private String couponPromoNo;
	private double couponDcAmt;
	private double couponOwnCost;
	private double couponEntpCost;

	private double supplyPrice;
	private double commission;

	@Column(updatable=false)
	private String insertId;
	@Column(updatable=false)
	private Timestamp insertDate;
	private String transId;
	private Timestamp transDate;

	private double bestPrice; // 최종혜택가
	
	private Double marginRate; // 당사부담금액에 대한 마진

}