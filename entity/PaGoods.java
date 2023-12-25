package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;

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
@Table(name = "TPAPRODUCT")
public class PaGoods {

	@Id
	private String goodsCode;
	private String goodsName;
	private String saleGb; // 판매구분[B032]
	private String lmsdCode; // 상품분류코드
	private String makecoCode;
	private String brandName;
	private String originCode;
	private String originName;
	private String taxYn;
	private String taxSmallYn;
	private String adultYn;
	private int orderMinQty;
	private int orderMaxQty;
	private int custOrdQtyCheckTerm;
	private String doNotIslandDelyYn;
	private String entpCode;
	private String shipManSeq;
	private String returnManSeq;
	private String shipCostCode;
	private String salePaCode;
	private int avgDelyLeadtime;
	private Timestamp lastSyncDate;
	private Timestamp lastDescribeSyncDate;
	private Timestamp saleStartDate;
	private Timestamp saleEndDate;
	private String keyword;
	private String collectYn;
	private String orderCreateYn;
	private int ranking;
	@Column(updatable=false)
	private String insertId;
	@Column(updatable=false)
	private Timestamp insertDate;
	private String modifyId;
	private Timestamp modifyDate;
	private String installYn;

	@Transient
	private boolean isDirty;
	@Transient
	private boolean isDescribeDirty;

}
