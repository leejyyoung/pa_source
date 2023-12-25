package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="TPANOTICEM")
public class PaNoticeM {

	@Id
	private String noticeNo;
	private Timestamp noticeBdate;
	private Timestamp noticeEdate;
	private String paGroupCode;
	private String useCode;
	private Timestamp modifyDate;
	private String modifyId;
}
