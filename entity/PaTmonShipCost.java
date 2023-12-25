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

import com.cware.partner.sync.domain.id.PaTmonShipCostId;

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
@Table(name = "TPATMONSHIPCOST")
@IdClass(PaTmonShipCostId.class)
public class PaTmonShipCost {

	@Id
	private String paCode;
	@Id
	private String entpCode;
	@Id
	private String productType; // 배송상품타입[DP04:화물설치,DP05:주문제작,DP07:일반상품]
	@Id
	private String shipManSeq; // 출고지번호
	@Id
	private String returnManSeq; // 회수지번호
	@Id
	private String shipCostCode;
	@Id
	private Timestamp applyDate;
	@Id
	private String noShipIsland; // 도서산간배송부가여부

	private int shipCostBaseAmt;
	private double ordCost;
	private double returnCost;
	private double changeCost;
	private double islandCost;
	private double jejuCost;

	@Column(updatable=false)
	private String insertId;
	@Column(updatable=false)
	private Timestamp insertDate;
	private String modifyId;
	private Timestamp modifyDate;

	@Transient
	private String shipCostFlag;


	public PaTmonShipCost(String entpCode, String shipCostFlag, String shipCostCode, double shipCostBaseAmt,
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
