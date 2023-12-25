package com.cware.partner.sync.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cware.partner.common.code.SaleGb;
import com.cware.partner.common.code.ShipCostFlag;
import com.cware.partner.sync.domain.entity.EntpUser;
import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.GoodsDt;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;
import com.cware.partner.sync.repository.EntpUserRepository;

import com.cware.partner.common.code.ExceptKakaoSourcingCode;

import lombok.extern.slf4j.Slf4j;

/**
 * 카카오 입점 제약조건 체크
 *
 */
@Slf4j
@Component
public class KakaoFilter extends Filter {

	@Autowired
	EntpUserRepository entpUserRepostiory;

	public boolean apply(Goods goods, PaGoodsTarget target) {

		tag = "카카오필터";

		// 상품명 길이는 50이하여야 함.
		if (goods.getGoodsNameMc().length() > 50) {
			target.setExcept(true);
			target.setExceptNote("상품명 길이가 50을 초과하면 안됩니다.");
			log.info("{}: {} [{}-{}] 상품: {}, 상품명 : {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getGoodsName());
			logFilter("KAKAO-GOODS_NAME", target);
			return false;
		}

		// 기본 배송비는 최소 10원 이상 최대 50만원이하, 입력단위는 10원단위
		if (!ShipCostFlag.FREE.code().equals(goods.getPaCustShipCost().getShipCostFlag())) {

			if (goods.getPaCustShipCost().getOrdCost() < 10) {
				target.setExcept(true);
				target.setExceptNote("주문배송비는 10원 이상이어야합니다.");
				log.info("{}: {} 상품: {}, 배송비 : {}", tag, target.getExceptNote(), goods.getGoodsCode(),
						goods.getPaCustShipCost().getOrdCost());
				logFilter("KAKAO-ORD_COST_MIN", target);
				return false;
			}

			if (goods.getPaCustShipCost().getOrdCost() > 500_000) {
				target.setExcept(true);
				target.setExceptNote("주문배송비는 50만원 이하여야합니다.");
				log.info("{}: {} 상품: {}, 배송비 : {}", tag, target.getExceptNote(), goods.getGoodsCode(),
						goods.getPaCustShipCost().getOrdCost());
				logFilter("KAKAO-ORD_COST_MAX", target);
				return false;
			}

			if (Math.floorMod((int) goods.getPaCustShipCost().getOrdCost(), 10) > 0) {
				target.setExcept(true);
				target.setExceptNote("주문배송비는 10원단위 이상이어야합니다.");
				log.info("{}: {} 상품: {}, 배송비 : {}", tag, target.getExceptNote(), goods.getGoodsCode(),
						goods.getPaCustShipCost().getOrdCost());
				logFilter("KAKAO-ORD_COST_UNIT", target);
				return false;
			}

			// 조건부나 개별조건부일때 배송비기준금액이 10원 단위보다 커야하고 기준금액이 10원이상이어야함.
			// 배송비기준금액은 주문배송비이상
			if (ShipCostFlag.BASEAMT.code().equals(goods.getPaCustShipCost().getShipCostFlag())
					|| ShipCostFlag.BASEAMT_CODE.code().equals(goods.getPaCustShipCost().getShipCostFlag())) {
				if (goods.getPaCustShipCost().getShipCostBaseAmt() < 10) {
					target.setExcept(true);
					target.setExceptNote("무료배송기준금액은 10원 이상이어야합니다.");
					log.info("{}: {} 상품: {}, 기준금액: {}", tag, target.getExceptNote(), goods.getGoodsCode(),
							goods.getPaCustShipCost().getShipCostBaseAmt());
					logFilter("KAKAO-SHIP_COST_BASE_AM_MIN", target);
					return false;
				}
				if (Math.floorMod(goods.getPaCustShipCost().getShipCostBaseAmt(), 10) > 0) {
					target.setExcept(true);
					target.setExceptNote("무료배송기준금액은 10원단위 이상이어야합니다.");
					log.info("{}: {} 상품: {}, 기준금액: {}", tag, target.getExceptNote(), goods.getGoodsCode(),
							goods.getPaCustShipCost().getShipCostBaseAmt());
					logFilter("KAKAO-SHIP_COST_BASE_AMT_UNIT", target);
					return false;
				}
				if (goods.getPaCustShipCost().getShipCostBaseAmt() < goods.getPaCustShipCost().getOrdCost()) {
					target.setExcept(true);
					target.setExceptNote("무료배송기준금액은 주문배송비 이상이어야합니다.");
					log.info("{}: {} 상품: {}, 기준금액: {}, 배송비: {}", tag, target.getExceptNote(), goods.getGoodsCode(),
							goods.getPaCustShipCost().getShipCostBaseAmt(), goods.getPaCustShipCost().getOrdCost());
					logFilter("KAKAO-SHIP_COST_BASE_AMT_COST", target);
					return false;
				}
			}
		}

		// 반품배송비 최대 50만원
		if (goods.getPaCustShipCost().getReturnCost() > 500_000) {
			target.setExcept(true);
			target.setExceptNote("반품배송비가 50만원을 초과하면 안됩니다.");
			log.info("{}: {} 상품: {}, 반품비: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getPaCustShipCost().getReturnCost());
			logFilter("KAKAO-RETURN_COST", target);
			return false;
		}

		// 교환배송비 최대 100만원
		if (goods.getPaCustShipCost().getChangeCost() > 1_000_000) {
			target.setExcept(true);
			target.setExceptNote("교환배송비가 100만원을 초과하면 안됩니다.");
			log.info("{}: {} 상품: {}, 교환비: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getPaCustShipCost().getChangeCost());
			logFilter("KAKAO-CHANGE_COST", target);
			return false;
		}

		// 도서산간/제주 배송비가 주문 배송비보다 작을 경우 반려
		if ((goods.getPaCustShipCost().getIslandCost() - goods.getPaCustShipCost().getOrdCost()) < 0 || (goods.getPaCustShipCost().getJejuCost() - goods.getPaCustShipCost().getOrdCost()) < 0) {
			target.setExcept(true);
			target.setExceptNote("도서산간 또는 제주 배송비가 주문 배송비보다 작을 수 없습니다.");
			log.info("{}: {} 상품: {}, 주문비{}, 도서산간비: {}, 제주비: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getPaCustShipCost().getOrdCost(), goods.getPaCustShipCost().getIslandCost(), 	goods.getPaCustShipCost().getJejuCost());
			logFilter("KAKAO-ISLAND_COST", target);
			return false;
		}

		// 도서산간 또는 제주 추가배송비는 20만원 초과시 입점 반려
		if ((goods.getPaCustShipCost().getIslandCost() - goods.getPaCustShipCost().getOrdCost()) > 200_000 || (goods.getPaCustShipCost().getJejuCost() - goods.getPaCustShipCost().getOrdCost()) > 200_000) {
			target.setExcept(true);
			target.setExceptNote("도서산간 또는 제주 추가배송비는 20만원을 초과하면 안됩니다.");
			log.info("{}: {} 상품: {}, 도서산간비: {}, 제주비: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getPaCustShipCost().getIslandCost(), 	goods.getPaCustShipCost().getJejuCost());
			logFilter("KAKAO-ISLAND_COST", target);
			return false;
		}


		// 옵션 체크
		List<String> optionList = new ArrayList<String>();

		for (GoodsDt goodsDt : goods.getGoodsDtList()) {
			if (SaleGb.FORSALE.code().equals(goodsDt.getSaleGb())) {

				// 옵션명 길이는 25이하, 하나라도 초과하면 입점 제외
				if (goodsDt.getGoodsdtInfo().length() > 25) {
					target.setExcept(true);
					target.setExceptNote("옵션명 길이가 25를 초과하면 안됩니다.");
					log.info("{}: {} 상품: {}, 옵션명: {}", tag, target.getExceptNote(), target.getGoodsCode(),
							goodsDt.getGoodsdtInfo());
					logFilter("KAKAO-GOODSDT_INFO", target);
					return false;
				}
				optionList.add(goodsDt.getGoodsdtInfo());

			}
		}

		// 옵션 최대 100개까지
		if (optionList.size() > 100) {
			target.setExcept(true);
			target.setExceptNote("옵션은 100개를 초과하면 안됩니다.");
			log.info("{}: {} 상품: {}, 옵션수: {}", tag, target.getExceptNote(), target.getGoodsCode(), optionList.size());
			logFilter("KAKAO-GOODSDT_SIZE", target);
			return false;
		}

		// 동일 옵션명 등록 불가
		if (optionList.size() != optionList.stream().distinct().count()) {
			target.setExcept(true);
			target.setExceptNote("동일한 옵션명이 있습니다.");
			log.info("{}: {} 상품: {}, 옵션수: {}", tag, target.getExceptNote(), target.getGoodsCode(), optionList.size());
			logFilter("KAKAO-GOODSDT_INFO_DUP", target);
			return false;
		}

		// 업체출고지 유효성 체크
		EntpUser shipMan = goods.getShipManUser();
		if (shipMan.getStdPost() == null || shipMan.getStdRoadPost() == null) {
			target.setExcept(true);
			target.setExceptNote("업체출고지 주소가 유효하지 않습니다.");
			log.info("{}: {} 상품: {}, 업체: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getShipEntpCode());
			logFilter("KAKAO-SHIP_MAN_ADDR", target);
			return false;
		}

		// 업체회수지 유효성 체크
		EntpUser returnMan = goods.getReturnManUser();
		if (returnMan.getStdPost() == null || returnMan.getStdRoadPost() == null) {
			target.setExcept(true);
			target.setExceptNote("업체회수지 주소가 유효하지 않습니다.");
			log.info("{}: {} 상품: {}, 업체: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getShipEntpCode());
			logFilter("KAKAO-RETURN_MAN_ADDR", target);
			return false;
		}

		// 카카오 제외소싱상품 예외 입점, 'HALFCLUB, BORI, TLIFE 와 별개로 SELLERUB 추가 연동 금지'
		if (Arrays.stream(ExceptKakaoSourcingCode.values()).anyMatch(v -> v.name().equals(goods.getSourcingCode()))) {
			if (goods.getSourcingExceptInput() == null || (!goods.getSourcingExceptInput().getPaAllYn().equals("1")
					&& !goods.getSourcingExceptInput().getPaGroupCode().contains(target.getPaGroupCode()))) {
				target.setExceptNote("카카오 제휴연동 대상 소싱 상품이 아닙니다: " + goods.getSourcingCode());
				target.setExcept(true);
				log.info("{}: {} [{}-{}]", tag, target.getExceptNote(), target.getGoodsCode(), target.getPaGroupCode());
				logFilter("KAKAO-SOURCING_CODE", target);
				return false;
			}
		}

		//착불상품 입점제외
		if ("1".equals(goods.getCollectYn())) {
			target.setExcept(true);
			target.setExceptNote("착불상품은 제외됩니다.");
			log.info("{}: {} 상품: {}", tag, target.getExceptNote(), target.getGoodsCode());
			logFilter("KAKAO-COLLECT_YN", target);
			return false;
			}

		return true;
	}

	@Override
	public boolean apply(Goods goods) {
		return false;
	}

}
