package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;

import com.cware.partner.sync.domain.id.PaShipCostId;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "TPACUSTSHIPCOST")
@DynamicInsert
@IdClass(PaShipCostId.class)
public class PaCustShipCost {

	@Id
	private String paCode;
	@Id
	private String entpCode;
	@Id
	private String shipCostCode;

	private int shipCostBaseAmt;
	private double ordCost;
	private double returnCost;
	private double changeCost;
	private double islandCost;
	private double jejuCost;

	private String transTargetYn;
	private String transCnCostYn;

	private Timestamp lastSyncDate;
	@Column(updatable=false)
	private String insertId;
	@Column(updatable=false)
	private Timestamp insertDate;
	private String modifyId;
	private Timestamp modifyDate;

	@Transient
	private String shipCostFlag;

	@Transient
	private Timestamp applyDate;


	public PaCustShipCost(String entpCode, String shipCostFlag, String shipCostCode, double shipCostBaseAmt,
			Date applyDate, double ordCost, double returnCost,
			double changeCost, double islandCost, double jejuCost) {
		this.entpCode = entpCode;
		this.shipCostFlag = shipCostFlag;
		this.shipCostCode = shipCostCode;
		this.shipCostBaseAmt = (int)shipCostBaseAmt;
		this.applyDate = new Timestamp(applyDate.getTime());
		this.ordCost = ordCost;
		this.returnCost = returnCost;
		this.changeCost = changeCost;
		this.islandCost = islandCost;
		this.jejuCost = jejuCost;
	}

}
