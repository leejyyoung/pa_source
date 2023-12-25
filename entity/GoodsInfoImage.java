package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="TGOODSINFOIMAGE")
public class GoodsInfoImage {

	@Id
	private String goodsCode;
	private String infoImageType;
	private String infoImageFile1;
	private Timestamp modifyDate;
}
