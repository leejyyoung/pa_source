package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.cware.partner.sync.domain.id.PaSaleNoId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicInsert
@Table(name = "TPASALENOGOODS")
@IdClass(PaSaleNoId.class)
public class PaSaleNoGoods {

	@Id
	private String paGroupCode;
	@Id
	private String paCode;
	@Id
	private String goodsCode;
	@Id
	private String seqNo;

	private String paGoodsCode;
	private String paSaleGb;
	private String note;

	private String insertId;
	private Timestamp insertDate;

}