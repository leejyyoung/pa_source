package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.cware.partner.sync.domain.id.PaGoodsPriceId;

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
@Table(name = "TPAGOODSPRICE")
@IdClass(PaGoodsPriceId.class)
public class PaGoodsPrice  {

	@Id
	private String paCode;
	@Id
	private String goodsCode;
	@Id
	private Timestamp applyDate;
//	private String supplySeq;

	private String priceSeq;

	private double salePrice;
	private double supplyPrice;
	private double commision;
	private double dcAmt;
	private double lumpSumDcAmt;
	private double lumpSumOwnDcAmt;
	private double lumpSumEntpDcAmt;

	@Column(updatable=false)
	private String insertId;
	@Column(updatable=false)
	private Timestamp insertDate;
	private String modifyId;
	private Timestamp modifyDate;
	private String transId;
	private Timestamp transDate;

}