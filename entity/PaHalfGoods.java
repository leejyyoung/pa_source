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
@Table(name = "TPAHALFGOODS")
public class PaHalfGoods extends PartnerGoods {

	private String productNo;
	private String goodsName;
	private String paLmsdKey;
	private String paBrandNo;
	private String originCode;

	private String paShipCostCode;
	private String prdPrcNo;
}