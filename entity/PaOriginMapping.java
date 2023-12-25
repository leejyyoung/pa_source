package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PaOriginId;

import lombok.Data;

@Data
@Entity
@Table(name="TPAORIGINMAPPING")
@IdClass(PaOriginId.class)
public class PaOriginMapping {

	@Id
	private String paGroupCode;
	@Id
	private String originCode;
	private String paOriginCode;
}
