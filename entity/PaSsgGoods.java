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
@Table(name = "TPASSGGOODS")
public class PaSsgGoods extends PartnerGoods {

	private String itemId; // 업체상품ID
	private String stdCtgId; // 표준카테고리번호
	private String orplcId; // 원산지코드

	private String brandId; // 브랜드번호
	private String manufcoNm; // 제조사명
	private String goodsName;

	private String massTargetYn;
	private String massTargetCode;
}