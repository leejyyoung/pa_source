package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.hibernate.annotations.DynamicInsert;

import com.cware.partner.sync.domain.id.PaGoodsId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 제휴사별상품
 *
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicInsert
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@IdClass(PaGoodsId.class)
public abstract class PartnerGoods {

	@Id
	private String paGroupCode;
	@Id
	private String paCode;
	@Id
	private String goodsCode;

	private String paSaleGb;
	private String paStatus;

	private int transOrderAbleQty;
	private String transTargetYn;
	private String transSaleYn;

	private String returnNote;

	private Timestamp lastSyncDate;

	@Column(updatable = false)
	private String insertId;
	@Column(updatable = false)
	private Timestamp insertDate;
	private String modifyId;
	private Timestamp modifyDate;
}
