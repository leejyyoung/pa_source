package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PaGoodsLogId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "TPAGOODSQALOG")
@IdClass(PaGoodsLogId.class)
public class PaGoodsQaLog {

	@Id
	private String paCode;
	@Id
	private String goodsCode;
	@Id
	private String seq;
	private String paGroupCode;
	private String note;
	private Timestamp insertDate;

}
