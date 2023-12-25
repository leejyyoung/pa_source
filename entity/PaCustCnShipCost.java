package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

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
@Table(name = "TPACUSTCNSHIPCOST")
public class PaCustCnShipCost {

	@Id
	private String cnCostSeq;
	private String paCode;
	private String entpCode;
	private String entpManSeq;
	private String shipCostCode;
	private double shipCostBaseAmt;
	private double ordCostAmt;

	private String transTargetYn;
	private Timestamp transEndDate;
	private String applyCnCostSeq;
	private String transCnCostYn;

	@Column(updatable=false)
	private String insertId;
	@Column(updatable=false)
	private Timestamp insertDate;
	private String modifyId;
	private Timestamp modifyDate;

}
