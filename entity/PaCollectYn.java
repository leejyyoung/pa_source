package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "TPACOLLECTYNCATE")
public class PaCollectYn {

	@Id
	private String paGroupCode;
	private String paLmsdKey;

}