package com.cware.partner.sync.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="TMAKECOMP")
public class MakeComp {

	@Id
	private String makecoCode;
	private String makecoName;
}

