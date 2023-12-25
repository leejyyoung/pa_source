package com.cware.partner.sync.domain.id;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaEntpCostId implements Serializable {
	private static final long serialVersionUID = 5343889790774003553L;

	private String paCode;
	private String entpCode;
	private String entpManSeq;
	private String shipCostCode;
	private String noShipIsland;
	private String installYn;
}