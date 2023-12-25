package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PaSyncId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="TPAGOODSSYNCLOG")
@IdClass(PaSyncId.class)
public class PaGoodsSyncLog {

	@Id
	private String goodsCode;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqSyncLogNo")
	@SequenceGenerator(sequenceName = "SEQ_SYNC_LOG_NO", name = "seqSyncLogNo", allocationSize = 1)
	private long syncLogNo;
	private String cdcReasonCode;
	private String syncNote;
	private long goodsSyncNo;
	private String paGroupCode;
//	private Timestamp insertDate;

}
