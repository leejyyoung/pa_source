package com.cware.partner.sync.domain.id;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaExceptEntpId implements Serializable {
	private static final long serialVersionUID = 7254836813542359223L;

	private String entpCode;
	private String paGroupCodeAllYn;
	private String allBrandYn;
	private String sourcingMedia;
}