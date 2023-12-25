package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PaPromoMinMarginId;

import lombok.Data;

@Data
@Entity
@Table(name="TPAPROMOMINMARGINRATE")
@IdClass(PaPromoMinMarginId.class)
public class PaPromoMinMarginRate {

	@Id
	private String paGroupCode;
	@Id
	private String paCode;
	private Double minMarginRate;

}