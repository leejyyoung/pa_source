package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import lombok.Data;

@Data
@Entity
@Table(name = "TPAPROMOMARGINAUTOM")
//프로모션 마진율 변동 대상
//@Where(clause = "use_code = '00' and sysdate between event_bdate and event_edate ")
public class PaPromoMarginAutoM {

	@Id
	private String eventNo;
	private String eventName;
	private String mediaCode;
	private String goodsKindsAllYn;
	private String sourcingMedia;
	private String exceptMarginRate;
	private String useCode;
	private Timestamp eventEdate;
	private Timestamp eventBdate; 
	private Timestamp modifyDate;
	private String modifyId;
}
