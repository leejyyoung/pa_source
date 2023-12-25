package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import lombok.Data;

@Data
@Entity
@Table(name="TDESCRIBE")
// 웹기술서
@Where(clause = "describe_code in ('998', '999') and describe_ext is not null")
public class Describe {

	@Id
	private String goodsCode;
	private String describeCode;
	private String describeTitle;
	private String describeNote;
	private String webFlag;
	private String describeExt;
	private Timestamp modifyDate;
}
