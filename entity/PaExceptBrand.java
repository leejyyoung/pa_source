package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "TPAEXCEPTBRAND")
public class PaExceptBrand {

	@Id
	private String entpCode;
	private String brandCode;
	private String useYn;
	private String modifyId;
	private Timestamp modifyDate;
}