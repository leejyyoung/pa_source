package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="TPACDCGOODS")
public class PaCdcGoods {

	@Id
	private String goodsCode;
	private long cdcSnapshotNo;
	private int ranking;
}

