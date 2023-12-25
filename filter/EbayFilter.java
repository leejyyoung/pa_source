package com.cware.partner.sync.filter;

import org.springframework.stereotype.Component;

import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;

import lombok.extern.slf4j.Slf4j;

/**
 * 이베이 입점 제약조건 체크
 *
 */
@Slf4j
@Component
public class EbayFilter extends Filter {

	public boolean apply(Goods goods, PaGoodsTarget target) {

		tag = "이베이필터";

		// 도서산간/제주 추가배송비 20만원 초과시 제외
		if ((goods.getPaCustShipCost().getIslandCost() - goods.getPaCustShipCost().getOrdCost()) > 200_000
				|| (goods.getPaCustShipCost().getJejuCost() - goods.getPaCustShipCost().getOrdCost()) > 200_000) {
			target.setExcept(true);
			target.setExceptNote("도서산간 또는 제주 추가배송비는 20만원을 초과하면 안됩니다.");
			log.info("{}: {} 상품: {}, 도서산간비: {}, 제주비: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getPaCustShipCost().getIslandCost(), 	goods.getPaCustShipCost().getJejuCost());
			logFilter("EBAY-ISLAND_COST", target);
			return false;
		}

//		// 옵션 체크
//		List<String> optionList = new ArrayList<String>();
//
//		// 옵션명 길이 체크 하나라도 초과하면 입점 제외
//		for (GoodsDt goodsDt : goods.getGoodsDtList()) {
//			if (SaleGb.FORSALE.code().equals(goodsDt.getSaleGb())) {
//				// 옵션값 50bytes 길이제한 적용 (이베이기준 인코딩 적용)
//				try {
//					if( goodsDt.getGoodsdtInfo().getBytes("euc-kr").length > 50) {
//						target.setExcept(true);
//						target.setExceptNote("옵션값은 50bytes를 초과하면 안됩니다.");
//						log.info("{}: {} 옵션: {}-{}, 옵션명: {}", tag, target.getExceptNote(), target.getGoodsCode(),
//								goodsDt.getGoodsdtCode(), goodsDt.getGoodsdtInfo());
//						logFilter("EBAY-GOODSDT_INFO", target);
//						return false;
//					}
//				} catch (UnsupportedEncodingException e) {
//					log.error("{}: {} 옵션: {}-{}, 옵션명: {}", tag, e.getMessage(), target.getGoodsCode(),
//							goodsDt.getGoodsdtCode(), goodsDt.getGoodsdtInfo());
//				}
//				optionList.add(goodsDt.getGoodsdtInfo().trim());
//			}
//		}
//
//		// 옵션 최대 50개까지
//		if (optionList.size() > 50) {
//			target.setExcept(true);
//			target.setExceptNote("옵션은 50개를 초과하면 안됩니다.");
//			log.info("{}: {} 상품: {}, 옵션수: {}", tag, target.getExceptNote(), target.getGoodsCode(), optionList.size());
//			logFilter("EBAY-GOODSDT_SIZE", target);
//			return false;
//		}

		return true;
	}

	@Override
	public boolean apply(Goods goods) {
		return false;
	}

}
