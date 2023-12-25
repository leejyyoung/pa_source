package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="TPAGMKTORIGINMAPPING")
public class PaGmktOriginMapping {

	@Id
	private String originCode;
	private String orgnTypDtlsCd;
}
