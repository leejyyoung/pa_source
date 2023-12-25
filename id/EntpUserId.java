package com.cware.partner.sync.domain.id;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntpUserId implements Serializable {
	private static final long serialVersionUID = -7990255964371172575L;
	private String entpCode;
	private String entpManSeq;
}