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
@Table(name = "TPAKAKAOGOODS")
public class PaKakaoGoods extends PartnerGoods {

	private String productId;
	private String categoryId;
	private String goodsName;
	private String manufacturer;
	private String brand;
	private String productOrginAreaInfo;

	private String massTargetYn;
	private String massTargetCode;
}