package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "TGOODS")
public class EntpGoods {

	@Id
	private String goodsCode;
	private String shipManSeq;
	private String returnManSeq;
	private String entpCode;
	private String delyType;
	private String saleGb;
}
