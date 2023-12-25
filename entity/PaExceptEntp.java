package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PaExceptEntpId;

import lombok.Data;

@Data
@Entity
@Table(name = "TPAEXCEPTENTP")
@IdClass(PaExceptEntpId.class)
public class PaExceptEntp {

	@Id
	private String entpCode;
	@Id
	private String paGroupCodeAllYn; // 전체 제휴매체 제외여부	
	@Id
	private String allBrandYn; // 전체 브랜드 제외여부
	@Id
	private String sourcingMedia; // 소싱매체(00:전체,01:방송,61:쇼핑몰)
	
	private String paGroupCode; // 제외매체
	private String useYn;
	private String modifyId;
	private Timestamp modifyDate;

//	@OneToMany
//	@JoinColumn(name = "entpCode")
//	List<PaExceptBrand> paExceptBrand;
}