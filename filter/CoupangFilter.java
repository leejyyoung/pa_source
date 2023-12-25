package com.cware.partner.sync.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cware.partner.sync.domain.entity.EntpUser;
import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.GoodsDt;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;
import com.cware.partner.sync.repository.PaCollectYnRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 쿠팡 입점 제약조건 체크
 *
 */
@Slf4j
@Component
public class CoupangFilter extends Filter {

//	@Autowired
//	private PaGoodsPriceRepository paGoodsPriceRepository;
	
	@Autowired
	private PaCollectYnRepository paCollectYnRepository;

	public boolean apply(Goods goods, PaGoodsTarget target) {

		tag = "쿠팡필터";

		// 조건부 배송비에 대한 필터는 공통으로 이관 22.2.11
//		// 조건부나 개별조건부일때 배송비기준금액이 100원 단위보다 커야하고 기준금액이 100원이상이어야함.
//		if (ShipCostFlag.BASEAMT.code().equals(goods.getPaCustShipCost().getShipCostFlag())
//				|| ShipCostFlag.BASEAMT_CODE.code().equals(goods.getPaCustShipCost().getShipCostFlag())) {
//			if (goods.getPaCustShipCost().getShipCostBaseAmt() < 100) {
//				target.setExcept(true);
//				target.setExceptNote("무료배송기준금액은 100원 이상이어야합니다.");
//				log.info("{}: {} [{}-{}] 상품: {}, 기준금액: {}", tag, target.getExceptNote(), target.getGoodsCode(),
//						goods.getPaCustShipCost().getShipCostBaseAmt());
//				logFilter("COUPANG-SHIP_COST_BASE_AMT", target);
//				return false;
//			}
//			if (Math.floorMod(goods.getPaCustShipCost().getShipCostBaseAmt(), 100) > 0) {
//				target.setExcept(true);
//				target.setExceptNote("무료배송기준금액은 100원단위 이상이어야합니다.");
//				log.info("{}: {} [{}-{}] 상품: {}, 기준금액: {}", tag, target.getExceptNote(), target.getGoodsCode(),
//						goods.getPaCustShipCost().getShipCostBaseAmt());
//				logFilter("COUPANG-SHIP_COST_BASE_AMT", target);
//				return false;
//			}
//		}

		// 반품배송비가 25만원을 넘으면 제외
		if (goods.getPaCustShipCost().getReturnCost() > 250000) {
			target.setExcept(true);
			target.setExceptNote("반품배송비가 25만원을 초과하면 안됩니다.");
			log.info("{}: {} 상품: {}, 반품비: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getPaCustShipCost().getReturnCost());
			logFilter("COUPANG-RETURN_COST", target);
			return false;
		}

		// 옵션 체크
		List<String> optionList = new ArrayList<String>();

		// 옵션명 길이 체크 하나라도 초과하면 입점 제외
		for (GoodsDt goodsDt : goods.getGoodsDtList()) {
//			if (SaleGb.FORSALE.code().equals(goodsDt.getSaleGb())) {
				// 옵션값 길이 최대 30 (단품속성명)
//				if (goodsDt.getOtherText() != null && goodsDt.getOtherText().length() > 30) {
//					target.setExcept(true);
//					target.setExceptNote("단품기타 길이가 30을 초과하면 안됩니다.");
//					log.info("{}: {} 옵션: {}-{}, 단품기타: {}", tag, target.getExceptNote(), target.getGoodsCode(),
//							goodsDt.getGoodsdtCode(), goodsDt.getOtherText());
//					logFilter("COUPANG-OTHER_TEXT", target);
//					return false;
//				}
				// 쿠팡 상품연동시 단품상세로 전송하고 있어 단품상세 == 옵션값임.
				// 옵션값 30 길이제한 적용
				// 옵션명 길이는 최대 150 (단품상세) => 내부 관리용일때만
				if( goodsDt.getGoodsdtInfo().length() > 30) {
					target.setExcept(true);
					target.setExceptNote("옵션값은 30자를 초과하면 안됩니다.");
					log.info("{}: {} 옵션: {}-{}, 옵션명: {}", tag, target.getExceptNote(), target.getGoodsCode(),
							goodsDt.getGoodsdtCode(), goodsDt.getGoodsdtInfo());
					logFilter("COUPANG-GOODSDT_INFO", target);
					return false;
				}
				optionList.add(goodsDt.getGoodsdtInfo().trim());
//			}
		}

		// 옵션 최대 200개까지
		if (optionList.size() > 200) {
			target.setExcept(true);
			target.setExceptNote("옵션은 200개를 초과하면 안됩니다.");
			log.info("{}: {} 상품: {}, 옵션수: {}", tag, target.getExceptNote(), target.getGoodsCode(), optionList.size());
			logFilter("COUPANG-GOODSDT_SIZE", target);
			return false;
		}

		// 동일 옵션명 등록 불가
		if (optionList.size() != optionList.stream().distinct().count()) {
			target.setExcept(true);
			target.setExceptNote("동일한 옵션명이 있습니다.");
			log.info("{}: {} 상품: {}, 옵션수: {}", tag, target.getExceptNote(), target.getGoodsCode(), optionList.size());
			logFilter("COUPANG-GOODSDT_INFO_DUP", target);
			return false;
		}

		EntpUser shipMan = goods.getShipManUser();

		// 업체주소 유효성 체크
		if ((shipMan.getStdPostAddr1() == null && shipMan.getPostAddr() == null) ||
				(shipMan.getStdPostAddr2() == null && shipMan.getAddr() == null)) {
			target.setExcept(true);
			target.setExceptNote("업체출고지 주소가 유효하지 않습니다.");
			log.info("{}: {} 상품: {}, 업체: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getShipEntpCode());
			logFilter("COUPANG-SHIP_MAN_ADDR", target);
			return false;
		}

		int telLength =String.join(Optional.ofNullable(shipMan.getEntpManDdd()).orElse("")
				, Optional.ofNullable(shipMan.getEntpManTel1()).orElse("")
				, Optional.ofNullable(shipMan.getEntpManTel2()).orElse("")).length();

		int cellLength =String.join(Optional.ofNullable(shipMan.getEntpManHp1()).orElse("")
				, Optional.ofNullable(shipMan.getEntpManHp2()).orElse("")
				, Optional.ofNullable(shipMan.getEntpManHp3()).orElse("")).length();

		// 업체 연락처 유효성 체크
		if ((telLength < 9 || telLength > 12) || (cellLength < 9 && cellLength > 12 )) {
			target.setExcept(true);
			target.setExceptNote("업체출고지 연락처가 유효하지 않습니다.");
			log.info("{}: {} 상품: {}, 업체: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getEntpCode());
			logFilter("COUPANG-SHIP_MAN_TEL", target);
			return false;
		}

		// 22.5.12 가격 변동폭 상관없이 연동하기로 함
//		// 직전 금액과의 변동폭이 50% 이상이면 필터처리
//		Optional<PaGoodsPrice> optional = paGoodsPriceRepository.findTransApplyGoodsPrice(target.getPaCode(), target.getGoodsCode());
//		if (optional.isPresent()) {
//			PaGoodsPrice paPrice = optional.get();
//			GoodsPrice price = goods.getGoodsPrice();
//
//			double paBestPrice = paPrice.getSalePrice() - paPrice.getDcAmt() - paPrice.getLumpSumDcAmt();
//
//			if (paBestPrice/2 >= price.getBestPrice() || paBestPrice * 2 <= price.getBestPrice()) {
//				target.setExceptNote("직전 연동금액에서 변동폭이 50%이상입니다.");
//				log.info("{}: {} 상품: {}, 직전연동가: {}, 현재가: {}", tag, target.getExceptNote(), target.getGoodsCode(),
//						paBestPrice, price.getBestPrice());
//				logFilter("COUPANG-SALE_PRICE_50", target);
//				return false;
//			}
//
//		}
		
		if("1".equals(goods.getCollectYn()) && paCollectYnRepository.countCopnCollecyLmsdCnt(goods.getLmsdCode()) < 1) {
			target.setExcept(true);
			target.setExceptNote("쿠팡의 착불 사용 가능 카테고리가 아닙니다.");
			log.info("{}: {} 상품: {}", tag, target.getExceptNote(), target.getGoodsCode());
			logFilter("COUPANG-COLLECT_CATEGORY", target);
			return false;
		}
		
		// 상품명 애플 관련 문구 체크
		if(goods.getGoodsName().contains("아이폰") || goods.getGoodsName().contains("에어팟") || goods.getGoodsName().contains("애플워치")) {
			target.setExcept(true);
			target.setExceptNote("애플 상표권 침해 문구 상품은 제휴OUT 연동 대상에서 제외됩니다.");
			log.info("{}: {} [상품:{} 상품명:{}]", tag, target.getExceptNote(), target.getGoodsCode(), goods.getGoodsName());
			logFilter("COUPANG-APPLE_BRAND_WORD", target);
			return false;
		}
		
		// 쿠팡은 주류상품 반려
		if ("1".equals(goods.getGoodsAddInfo().getAlcoholYn())) {
			target.setExcept(true);
			target.setExceptNote("쿠팡 주류상품은 입점할 수 없습니다.");
			log.info("{}: {} 상품: {}", tag, target.getExceptNote(), target.getGoodsCode());
			logFilter("COUPANG-ALCOHOL-YN", target);
			return false;
		}
		
		return true;
	}

	@Override
	public boolean apply(Goods goods) {
		return false;
	}

}
