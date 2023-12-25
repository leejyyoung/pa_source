package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PaPromoMarginExceptId;

import lombok.Data;

@Data
@Entity
@Table(name="TPAPROMOMARGINEXCEPT")
@IdClass(PaPromoMarginExceptId.class)
public class PaPromoMarginExcept {

	@Id
	private String goodsCode;
	@Id
	private String paGroupCode;
	
	private String useYn;
	
	private String talkDealPromoNo;
	
	private Timestamp modifyDate;
	private String modifyId;
	
}
