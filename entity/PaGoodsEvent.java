package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import lombok.Data;

@Data
@Entity
@Table(name="TPAGOODSEVENT")
@Where(clause = "sysdate between start_date and nvl(end_date, sysdate) and use_yn = '1'")
public class PaGoodsEvent {

	@Id
	private String goodsCode;
	private String paGroupCode;
	private String paGoodsCode;
	private Timestamp startDate;
	private Timestamp endDate;
}
