package com.cware.partner.sync.domain.id;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaBrandId implements Serializable {
	private static final long serialVersionUID = -5400277588711198635L;

	private String paGroupCode;
	private String brandCode;
}