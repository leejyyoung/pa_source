package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.cware.partner.sync.domain.id.PaGoodsId;

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
@Table(name = "TPAINTPGOODS")
@IdClass(PaGoodsId.class)
public class PaIntpGoods extends PartnerGoods {

	private String prdNo;
	private String paLmsdKey;
	private String brandNo;

	private String massTargetYn;
	private String massTargetCode;
}