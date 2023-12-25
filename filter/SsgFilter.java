package com.cware.partner.sync.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cware.partner.common.code.PaSaleGb;
import com.cware.partner.sync.domain.entity.EntpUser;
import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;
import com.cware.partner.sync.repository.EntpUserRepository;
import com.cware.partner.sync.repository.PaSsgRequiredInfoRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 쓱닷컴 입점 제약조건 체크
 *
 */
@Slf4j
@Component
public class SsgFilter extends Filter {

	@Autowired
	EntpUserRepository entpUserRepostiory;
	
	@Autowired
	PaSsgRequiredInfoRepository paSsgRequiredInfoRepository; 

	public boolean apply(Goods goods, PaGoodsTarget target) {

		tag = "쓱닷컴필터";

		/*
		 * // 과세상품이 아닌 경우 입점제외 if (!"1".equals(goods.getTaxYn())) {
		 * target.setExcept(true); target.setExceptNote("과세상품이 아니면 제외됩니다.");
		 * log.info("{}: {} [{}-{}] 상품: {}", tag, target.getExceptNote(),
		 * target.getGoodsCode()); logFilter("SSG-TAX_YN", target); return false; }
		 */

		// 농수축산물인 경우(offerType: 20), 농수산물 필수 정보가 입력되어 있지 않으면 제외
		// 2023-01-17 농수축산물(정보고시) -> 카테고리 기준으로 변경
		if(paSsgRequiredInfoRepository.countSsgRequiredInfo(target.getPaGroupCode(), goods.getLmsdCode()) > 0 && !goods.isFoodInfo()) {
			target.setExcept(true);
			target.setExceptNote("SSG의 필수 제품표시정보가 누락되었습니다.");
			log.info("{}: {} [{}-{}] 상품: {}", tag, target.getExceptNote(), target.getGoodsCode());
			logFilter("SSG-GOODS_FOOD_INFO", target);
			return false;
		}
		
		/*
		 * if ("20".equals(goods.getOfferType()) && !goods.isFoodInfo() ) {
		 * target.setExcept(true); target.setExceptNote("농수축산 식품 필수 항목이 누락되었습니다.");
		 * log.info("{}: {} [{}-{}] 상품: {}", tag, target.getExceptNote(),
		 * target.getGoodsCode()); logFilter("SSG-GOODS_FOOD_INFO", target); return
		 * false; }
		 */ 

		//착불여부 변경 체크
		if(PaSaleGb.FORSALE.code().equals(target.getPaSaleGb())) {//수정시에만 체크
			if (!(String.valueOf(goods.getSsgCollectYn())).equals(goods.getCollectYn())) {
				target.setExcept(true);
				target.setExceptNote("착불여부는 변경될 수 없습니다.");
				log.info("{}: {} 상품: {}", tag, target.getExceptNote(), target.getGoodsCode());
				logFilter("SSG-COLLECT_YN", target);
				return false;
			}
		}

		EntpUser shipMan = goods.getShipManUser();

		// 업체출고지 유효성 체크
		if ((shipMan.getStdPostAddr1() == null && shipMan.getPostAddr() == null) ||
				(shipMan.getStdPostAddr2() == null && shipMan.getAddr() == null)) {
			target.setExcept(true);
			target.setExceptNote("업체출고지 주소가 유효하지 않습니다.");
			log.info("{}: {} 상품: {}, 업체: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getShipEntpCode());
			logFilter("SSG-SHIP_MAN_ADDR", target);
			return false;
		}

		EntpUser returnMan = goods.getReturnManUser();

		// 업체회수지 유효성 체크
		if ((returnMan.getStdPostAddr1() == null && returnMan.getPostAddr() == null) ||
				(returnMan.getStdPostAddr2() == null && returnMan.getAddr() == null)) {
			target.setExcept(true);
			target.setExceptNote("업체회수지 주소가 유효하지 않습니다.");
			log.info("{}: {} [{}-{}] 상품: {}, 업체 : {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getEntpCode());
			logFilter("SSG-RETURN_MAN_SEQ", target);
			return false;
		}

		return true;
	}

	@Override
	public boolean apply(Goods goods) {
		return false;
	}

}
