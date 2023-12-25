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
@Table(name = "TPATMONGOODS")
public class PaTmonGoods extends PartnerGoods {

	private String dealNo;
	private String managedTitle; // 티몬 내부에서 관리될 딜 이름
	private String title; // 판매용 메인 제목(딜명)
	private String categoryNo; // 티몬 카테고리 번호
	private String brandName;

	private String massTargetYn;
	private String massTargetCode;
}