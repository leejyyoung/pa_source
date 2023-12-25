package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PaNoticeSeq;

import lombok.Data;

@Data
@Entity
@Table(name="TPANOTICETARGET")
@IdClass(PaNoticeSeq.class)
public class PaNoticeTarget {

	@Id
	private String noticeNo;
	@Id
	private String noticeSeq;
	private String goodsCode;
	private String modifyId;
	private Timestamp modifyDate;
}
