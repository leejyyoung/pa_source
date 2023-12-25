package com.cware.partner.sync.domain.id;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class GoodsPriceId implements Serializable {

	private static final long serialVersionUID = 1748847451083694246L;
	private String goodsCode;
	private Timestamp applyDate;
	private String priceSeq;

}