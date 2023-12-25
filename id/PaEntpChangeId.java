package com.cware.partner.sync.domain.id;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaEntpChangeId implements Serializable {
	private static final long serialVersionUID = -5106098332965784898L;

	private String entpCode;
	private String entpManSeq;
	private String paCode;
	private String changeSeq;
}