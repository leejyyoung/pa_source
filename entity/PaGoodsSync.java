package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
@Table(name="TPAGOODSSYNC")
public class PaGoodsSync {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGoodsSyncNo")
	@SequenceGenerator(sequenceName = "SEQ_GOODS_SYNC_NO", name = "seqGoodsSyncNo", allocationSize = 1)
	private long goodsSyncNo;
	private int targetCnt;
	private int procCnt;
	private int syncCnt;
	private int filterCnt;
	private int stopCnt;
	private Timestamp startDate;
	private Timestamp endDate;

	@Transient
	private String syncGoodsCode;

	@Transient
	private List<String> filterPaGroup;

}
