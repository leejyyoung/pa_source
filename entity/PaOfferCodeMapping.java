package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PaOfferId;

import lombok.Data;

@Data
@Entity
@Table(name="TPAOFFERCODEMAPPING")
@IdClass(PaOfferId.class)
public class PaOfferCodeMapping {

	@Id
	private String paGroupCode;
	@Id
	private String paOfferType;
	@Id
	private String paOfferCode;
	private String offerType;
	private String offerCode;


}