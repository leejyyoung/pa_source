package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.cware.partner.sync.domain.id.PaCopnCategoryAddInfoId;

import lombok.Data;

@Data
@Entity
@Table(name="TPACOPNCATEGORYADDINFO")
@IdClass(PaCopnCategoryAddInfoId.class)
public class PaCopnCategoryAddInfo {

	public PaCopnCategoryAddInfo(String lmsdCode, String minMarginRate, String commission) {
		this.commission = commission;
		this.minMarginRate = minMarginRate;
		this.lmsdCode = lmsdCode;
	}

	@Id
	private String paGroupCode;
	@Id
	private String paLmsdKey;
	
	private String commission;
	private String minMarginRate;
	
	@Transient
	private String lmsdCode;
	

}