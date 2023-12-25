package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.cware.partner.sync.domain.id.GoodsPriceId;

import lombok.Data;

@Data
@Entity
@Table(name="TGOODSPRICE")
@IdClass(GoodsPriceId.class)
public class GoodsPrice {

	@Id
	private String goodsCode;
	@Id
	private Timestamp applyDate;
	private double salePrice;
	private double buyPrice;
	private double arsDcAmt;
	private double arsOwnDcAmt;
	private double arsEntpDcAmt;
	private String priceSeq;
	private double lumpSumDcAmt;
	private double lumpSumOwnDcAmt;
	private double lumpSumEntpDcAmt;

	@Transient
	private double bestPrice;
	@Transient
	private double marginRate;
}
