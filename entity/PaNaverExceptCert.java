package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TPANAVEREXCEPTCERT")
public class PaNaverExceptCert {
	
	@Id
	private String goodsCode;
	private String certYn;
	private String insertId;
	private Timestamp insertDate;
	private String modifyId;
	private Timestamp modifyDate;
	private String remark;
}


