package com.cware.partner.sync.domain.id;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsDtId implements Serializable {
	private static final long serialVersionUID = 6719836849671842373L;
	private String goodsCode;
	private String goodsdtCode;
}