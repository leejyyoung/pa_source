package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.cware.partner.sync.domain.id.PaGoodsOfferId;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@DynamicInsert
@Table(name="TPAGOODSOFFER")
@IdClass(PaGoodsOfferId.class)
public class PaGoodsOffer {

	@Id
	private String goodsCode;
	@Id
	private String paGroupCode;
	@Id
	private String paOfferType;
	@Id
	private String paOfferCode;
	private String paOfferExt;
	private String remark1;
	private String remark2;
	private String useYn;
	private String transTargetYn;
	private Timestamp lastSyncDate;
	@Column(updatable=false)
	private String insertId;
	@Column(updatable=false)
	private Timestamp insertDate;
	private String modifyId;
	private Timestamp modifyDate;

	public PaGoodsOffer(String goodsCode, String paGroupCode, String paOfferType, String paOfferCode, String paOfferExt, String useYn) {
		this.goodsCode = goodsCode;
		this.paGroupCode = paGroupCode;
		this.paOfferType = paOfferType;
		this.paOfferCode = paOfferCode;
		this.paOfferExt = paOfferExt;
		this.useYn = useYn;
	}

	public PaGoodsOffer(String goodsCode, String paGroupCode, String paOfferType, String paOfferCode, String paOfferExt) {
		this.goodsCode = goodsCode;
		this.paGroupCode = paGroupCode;
		this.paOfferType = paOfferType;
		this.paOfferCode = paOfferCode;
		this.paOfferExt = paOfferExt;
	}

	public PaGoodsOffer(String paGroupCode, String paOfferType, String paOfferCode) {
		this.paGroupCode = paGroupCode;
		this.paOfferType = paOfferType;
		this.paOfferCode = paOfferCode;
	}
}