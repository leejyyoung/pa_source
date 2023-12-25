package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="TPAGMKTMAKER")
public class PaGmktMaker {

	@Id
	private String makerCode;
	private String makerNo;
}
