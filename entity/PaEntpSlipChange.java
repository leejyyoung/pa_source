package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.cware.partner.sync.domain.id.PaEntpChangeId;

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
@Table(name="TPAENTPSLIPCHANGE")
@IdClass(PaEntpChangeId.class)
public class PaEntpSlipChange {

	@Id
	private String entpCode;
	@Id
	private String entpManSeq;
	@Id
	private String paCode;
	@Id
	private String changeSeq;

	private String changeFlag; // 변경구분 : 00-신규, 10-수정
	private String procFlag; // 처리구분 : 00-반영대상, 10-반영완료

	private Timestamp lastSyncDate;

	@Column(updatable=false)
	private Timestamp insertDate;
	@Column(updatable=false)
	private String insertId;
	private Timestamp modifyDate;
	private String modifyId;
}
