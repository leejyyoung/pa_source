package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.cware.partner.sync.domain.id.EntpUserId;

import lombok.Data;

@Data
@Entity
@Table(name="TENTPUSER")
@IdClass(EntpUserId.class)
public class EntpUser {

	@Id
	private String entpCode;
	@Id
	private String entpManSeq;
	private String entpManGb;

	private String stdPost;
	private String stdPostAddr1;
	private String stdPostAddr2;
	private String postNo;
	private String postAddr;
	private String addr;
	private String stdRoadPost;

	private String entpManDdd;
	private String entpManTel1;
	private String entpManTel2;
	private String entpManHp1;
	private String entpManHp2;
	private String entpManHp3;


	private Timestamp modifyDate;
}
