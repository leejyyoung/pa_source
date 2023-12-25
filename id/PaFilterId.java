package com.cware.partner.sync.domain.id;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaFilterId implements Serializable {
	private static final long serialVersionUID = 3725560865094612442L;

	private String goodsCode;
	private long filterLogNo;
}