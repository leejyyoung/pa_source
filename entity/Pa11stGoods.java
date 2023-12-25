package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicInsert
@Table(name = "TPA11STGOODS")
public class Pa11stGoods extends PartnerGoods {

	private String productNo;
	private String paLmsdKey;
	private String orgnTypCd;
	private String orgnTypDtlsCd;
	private String brandNo;

	private String massTargetYn;
	private String massTargetCode;
}