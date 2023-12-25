package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="TPANAVERORIGINMAPPING")
public class PaNaverOriginMapping {

	@Id
	private String originCode;
	private String naverOriginCode;
}
