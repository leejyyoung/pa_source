package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.PaGoodsKindsRequiredId;

import lombok.Data;

@Data
@Entity
@Table(name="TPAGOODSKINDSREQUIRED")
@IdClass(PaGoodsKindsRequiredId.class)
public class PaGoodsKindsRequired {
    @Id
	private String paGroupCode;
	@Id
	private String lmsdCode;
	@Id
	private String requiredGb;
	
	private String useYn;
}
