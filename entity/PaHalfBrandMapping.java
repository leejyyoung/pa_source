package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import com.cware.partner.sync.domain.id.PaHalfBrandId;

import lombok.Data;

@Data
@Entity
@Table(name="TPAHALFBRANDMAPPING")
@IdClass(PaHalfBrandId.class)
public class PaHalfBrandMapping {
    @Id
	private String paCode;
	@Id
	private String brandCode;
	@Id
	private String paBrandNo;
	
	private String brandName;
	private String returnNoYn;
}
