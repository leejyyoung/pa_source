package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PaFilterId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="TPAGOODSFILTERLOG")
@IdClass(PaFilterId.class)
public class PaGoodsFilterLog {

	@Id
	private String goodsCode;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqFilterLogNo")
	@SequenceGenerator(sequenceName = "SEQ_FILTER_LOG_NO", name = "seqFilterLogNo", allocationSize = 1)
	private long filterLogNo;
	private String filterType;
	private String filterNote;
	private long goodsSyncNo;
	private String paGroupCode;
//	private Timestamp insertDate;

}
