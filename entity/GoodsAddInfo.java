package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="TGOODSADDINFO")
public class GoodsAddInfo {

	@Id
	private String goodsCode;
	private String emGoodsYn; // 구성원전용상품여부
	private String globalDelyYn; // 해외직배송여부
	private String dawnYn; // 새벽배송여부[0 : 일반, 1 : 동원새벽배송, 2 : 기타새벽배송]
	private String mobGiftGb; // 모바일이용권구분 00-기프티콘, 10-모바일상품권, 90-미대상
	private String orderCreateYn;
	private String alcoholYn;
	private String mobileEtvYn;
	private Timestamp modifyDate;
	private String goodsStts; // 중고,리퍼
}

