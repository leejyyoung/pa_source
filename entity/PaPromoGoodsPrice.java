package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.cware.partner.sync.domain.id.PaPromoPriceId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicInsert
@Table(name = "TPAPROMOGOODSPRICE")
@IdClass(PaPromoPriceId.class)
public class PaPromoGoodsPrice {

	@Id
	private String goodsCode; // 상품코드
	@Id
	private String paCode; // 제휴사코드
	@Id
	private Timestamp applyDate;
	@Id
	private String promoSeq;
	@Id
	private String alcoutPromoYn;

	private String promoNo; // 프로모션코드
	private double doAmt; // 혜택금액
	private double ownCost; // 당사부담금액/비율
	private double entpCost; // 업체부담금액/비율

	@Column(updatable=false)
	private String insertId;
	@Column(updatable=false)
	private Timestamp insertDate;

}
