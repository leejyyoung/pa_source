package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PaPromoMarginAutoGrpId;

import lombok.Data;

@Data
@Entity
@Table(name = "TPAPROMOMARGINAUTOGRP")
@IdClass(PaPromoMarginAutoGrpId.class)
public class PaPromoMarginAutoGrp {

	@Id
	private String eventNo;
	@Id
	private String targetGb;
	@Id
	private String targetCode;
	private String useCode;
	private Timestamp modifyDate;

}
