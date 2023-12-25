package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.ShipCostId;

import lombok.Data;

@Data
@Entity
@Table(name="TSHIPCOSTM")
@IdClass(ShipCostId.class)
public class ShipCostM {

	@Id
	private String entpCode;
	@Id
	private String shipCostCode;
	private String shipCostFlag;
	private String shipCostName;
	private double shipCostBaseAmt;
	private String shipCostReceipt;
	private String useYn;
	private Timestamp modifyDate;
}
