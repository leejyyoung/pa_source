package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="TPROMOADDINFO")
public class PromoAddInfo {

	@Id
	private String promoNo;
	
	private String paMarginExceptYn;
	
}
