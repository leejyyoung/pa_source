package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="TBRAND")
public class Brand {

	@Id
	private String brandCode;
	private String brandName;
}

