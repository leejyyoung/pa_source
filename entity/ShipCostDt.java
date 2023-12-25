package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="TSHIPCOSTDT")
public class ShipCostDt {

	@Id
	private String shipCostNo;
	private String entpCode;
	private String shipCostCode;
	private String shipWeight;
	private String custAreaGb;
	private Timestamp applyDate;
	private double ordCostAmt;
	private double retCostAmt;
	private double exchCostAmt;
	private Timestamp modifyDate;
}
