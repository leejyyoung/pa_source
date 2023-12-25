package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PaEntpCostId;

import lombok.Data;

@Data
@Entity
@Table(name="TPAWEMPENTPSLIP")
@IdClass(PaEntpCostId.class)
public class PaWempEntpSlip {

	@Id
	private String paCode;
	@Id
	private String entpCode;
	@Id
	private String entpManSeq;
	@Id
	private String shipCostCode;
	@Id
	private String noShipIsland;
	@Id
	private String installYn;

	private String transTargetYn;

	private Timestamp lastSyncDate;
	private Timestamp modifyDate;
	private String modifyId;
}
