package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PromoId;

import lombok.Data;

@Data
@Entity
@Table(name="TPROMOTARGET")
@IdClass(PromoId.class)
public class PromoTarget {

	@Id
	private String promoNo;
	@Id
	private String promoSeq;
	private String goodsCode;
	private double ownCost;
	private double entpCost;
}
