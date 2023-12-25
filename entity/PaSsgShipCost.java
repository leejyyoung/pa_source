package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.cware.partner.sync.domain.id.PaSsgShipCostId;

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
@Table(name = "TPASSGSHIPCOST")
@IdClass(PaSsgShipCostId.class)
public class PaSsgShipCost {

	@Id
	private String paCode;
	@Id
	private String shppcstAplUnitCd; // 배송비적용단위코드(10:주문금액합산, 30:상품수량별, 40:상품별주문금액합산)
	@Id
	private String shppcstPlcyDivCd; // 고객배송비정책구분( 10:출고배송비, 20:반품배송비, 60:도서산간, 70:제주 )
	@Id
	private String collectYn; // 착불여부 (1:착불, 0:선불)
	@Id
	private int shipCostBaseAmt; // 조건부무료 기준금액
	@Id
	private int shipCost; // 배송비

//	@Column(updatable=false)
//	private String paShipPolicyNo; // SSG배송비정책번호

	@Column(updatable=false)
	private String insertId;
	@Column(updatable=false)
	private Timestamp insertDate;

}
