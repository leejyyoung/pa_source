package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.cware.partner.sync.domain.id.PaGroupGoodsId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicInsert
@Table(name="TPAGOODSIMAGE")
@IdClass(PaGroupGoodsId.class)
public class PaGoodsImage {

	@Id
	private String goodsCode;
	@Id
	private String paGroupCode;
	@Column(name="IMAGE_P")
	private String imageP;
	private String imageAp;
	private String imageBp;
	private String imageCp;
	private String imageDp;
	private String imageUrl;
	private String transTargetYn;
	private Timestamp lastSyncDate;
	@Column(updatable=false)
	private Timestamp insertDate;
	@Column(updatable=false)
	private String insertId;
	private Timestamp modifyDate;
	private String modifyId;
}
