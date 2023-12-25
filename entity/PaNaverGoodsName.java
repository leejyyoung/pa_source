package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="TPANAVERGOODSNAME")
public class PaNaverGoodsName {

	@Id
	private String goodsCode;
	
	private String paGoodsName;
}
