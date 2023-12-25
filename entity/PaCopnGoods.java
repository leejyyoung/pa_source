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
@Table(name = "TPACOPNGOODS")
public class PaCopnGoods extends PartnerGoods {

	private String paLmsdKey;
	private String sellerProductId;
	private String displayCategoryCode;
	private String displayProductName;
	private String generalProductName;
	private String approvalStatus;

	private String massTargetYn;
	private String massTargetCode;
}