package com.cware.partner.sync.domain.id;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsOfferId implements Serializable {
	private static final long serialVersionUID = 1L;
	private String goodsCode;
	private String offerType;
	private String offerCode;
}