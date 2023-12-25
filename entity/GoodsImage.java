package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="TGOODSIMAGE")
public class GoodsImage {

	@Id
	private String goodsCode;
	private String imageNo;
	@Column(name="IMAGE_C")
	private String imageC;
	@Column(name="IMAGE_G")
	private String imageG;
	private String imageAt;
	private String imageAg;
	private String imageBt;
	private String imageBg;
	private String imageCt;
	private String imageCg;
	private String imageDt;
	private String imageDg;
	private String imageUrl;
	private Timestamp modifyDate;
}
