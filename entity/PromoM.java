package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="TPROMOM")
public class PromoM {

	@Id
	private String promoNo;
	private String promoName;
	private String firstOrderYn;
	private String couponYn;
	private String appType;
	private String doType;
	private Timestamp promoBdate;
	private Timestamp promoEdate;
	private String membGbAllYn;
	private String membGb;
	private String orderMediaAllYn;
	private String orderMedia;
	private String arsYn;
	private String mediaCodeAllYn;
	private String mediaCode;
	private String limitYn;
	private String goodsAllYn;
	private String grossNetFlag;
	private String useCode;
	private String immediatelyYn;
	private String alcoutPromoYn;
	private double appAmt;
	private String amtRateFlag;
	private double doRate;
	private double doAmt;
	private double limitAmt;
	private double standardAppAmt;
	private Timestamp modifyDate;
}
