package com.cware.partner.sync.domain.id;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaEntpUserId implements Serializable {
	private static final long serialVersionUID = 7066719667247439390L;

	private String entpCode;
	private String entpManSeq;
	private String paCode;
}