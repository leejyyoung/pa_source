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
@Table(name = "TPALTONGOODS")
public class PaLtonGoods extends PartnerGoods {

	private String spdNo; // 업체상품ID
	private String scatNo; // 표준카테고리번호
	private String lfDcatNo; // 전시카테고리번호
	private String oplcCd; // 원산지코드

	private String brdNo;
	private String mfcrNm;

	private String massTargetYn;
	private String massTargetCode;
}