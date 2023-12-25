package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import com.cware.partner.sync.domain.id.GoodsDtId;

import lombok.Data;

@Data
@Entity
@Table(name = "TGOODSDT")
@IdClass(GoodsDtId.class)
public class GoodsDt {

	@Id
	private String goodsCode;
	@Id
	private String goodsdtCode;
	private String goodsdtInfo;
	private String saleGb;
	private String colorCode;
	private String sizeCode;
	private String patternCode;
	private String formCode;
	private String otherText;

	private Timestamp modifyDate;

	@OneToOne
	@JoinColumns(value = { @JoinColumn(name = "goodsCode"), @JoinColumn(name = "goodsdtCode") })
	private PaGoodsDt paGoodsDt;

    // 주문가능재고
    @Formula("decode(sale_gb, '00', nvl(fun_get_order_able_qty(goods_code, goodsdt_code, ''), 0), 0)")
	private int orderAbleQty;

}
