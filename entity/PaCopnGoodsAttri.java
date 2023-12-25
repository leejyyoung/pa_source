package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.cware.partner.sync.domain.id.PaGoodsAttrSeq;

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
@Table(name="TPACOPNGOODSATTRI")
@IdClass(PaGoodsAttrSeq.class)
public class PaCopnGoodsAttri {

	@Id
	private String goodsCode;
	@Id
	private String attributeSeq;

	private String attributeTypeName; // 옵션타입명
	private String attributeValueName; // 옵션값
	private String attributeTypeMapping; // 옵션매핑
	private String dataType; // 데이터형식
	private String basicUnit; // 기본단위
	private String usableUnits; //사용가능한단위값 목록
	private String groupNumber; //그룹속성값(속성 중 택1 형태의 속성여부)
	private String exposed;// 구매옵션/검색옵션 구분값
	private String requiredYn; //필수여부

	@Column(updatable=false)
	private String insertId;
	@Column(updatable=false)
	private Timestamp insertDate;
	private String modifyId;
	private Timestamp modifyDate;
}
