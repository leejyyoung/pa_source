package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PaBrandId;

import lombok.Data;

@Data
@Entity
@Table(name="TPABRANDMAPPING")
@IdClass(PaBrandId.class)
public class PaBrandMapping {

	@Id
	private String paGroupCode;
	@Id
	private String brandCode;

	private String paBrandNo;
	private String brandName;
}
