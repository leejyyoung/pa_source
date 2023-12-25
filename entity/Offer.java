package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.GoodsOfferId;

import lombok.Data;

@Data
@Entity
@Table(name="TOFFER")
@IdClass(GoodsOfferId.class)
public class Offer {

	@Id
	private String goodsCode;
	@Id
	private String offerType;
	@Id
	private String offerCode;
	private String offerContents;
	private String useYn;
	private Timestamp modifyDate;
}
