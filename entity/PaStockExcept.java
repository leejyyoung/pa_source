package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PaStockExceptId;

import lombok.Data;

@Data
@Entity
@Table(name = "TPASTOCKEXCEPT")
@IdClass(PaStockExceptId.class)
public class PaStockExcept {

	@Id
	private String targetCode;
	@Id
	private String targetGb;
	private String remark;
	private String useYn;

}