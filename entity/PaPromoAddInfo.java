package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="TPAPROMOADDINFO")
public class PaPromoAddInfo {

	@Id
	private String promoNo;
	
	private String talkDealYn;
	
}
