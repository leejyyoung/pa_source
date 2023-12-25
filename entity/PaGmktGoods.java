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
@Table(name = "TPAGMKTGOODS")
public class PaGmktGoods extends PartnerGoods {

	private String itemNo;
	private String paSgroup;
	private String originEnum;
	private String originDt;
	private String makerNo;
	private String brandNo;
	private String returnNote;

	private String transDiscountYn; // 상품부담할인 전송대상여부 (미사용)

	private String esmCategoryCode; // ESM카테고리코드
	private String siteCategoryCode; // SITE카테고리코드

	private String massTargetYn;
	private String massTargetCode;
}