package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import lombok.Data;

@Data
@Entity
@Table(name="TPASOURCINGEXCEPTINPUT")
@Where(clause = "use_yn = '1'")
public class PaSourcingExceptInput {

	@Id
	private String goodsCode;
	private String sourcingCode;
	private String paAllYn;
	private String paGroupCode;
	private String useYn;
	private String insertId;
	private Timestamp insertDate;
	private String modifyId;
	private Timestamp modifyDate;
	private String marginExceptYn;

}
