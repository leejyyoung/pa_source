package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.cware.partner.sync.domain.id.PaGoodsDtSeq;

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
@Table(name="TPASSGGOODSDTMAPPING")
@IdClass(PaGoodsDtSeq.class)
public class PaSsgGoodsDtMapping {

	@Id
	private String paCode;
	@Id
	private String goodsCode;
	@Id
	private String goodsdtCode;
	@Id
	private String goodsdtSeq;

	private String goodsdtInfo;
	private String useYn;

	private int transOrderAbleQty;
	private String transStockYn;
//	private String transSaleYn;

	@Column(updatable=false)
	private String insertId;
	@Column(updatable=false)
	private Timestamp insertDate;
	private String modifyId;
	private Timestamp modifyDate;
}
