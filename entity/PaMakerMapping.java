package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PaMakerId;

import lombok.Data;

@Data
@Entity
@Table(name="TPAMAKERMAPPING")
@IdClass(PaMakerId.class)
public class PaMakerMapping {

	@Id
	private String paGroupCode;
	@Id
	private String makerCode;

	private String paMakerNo;
	private String makerName;
}
