package com.cware.partner.sync.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.cware.partner.common.code.ExceptHalfSourcingCode;
import com.cware.partner.common.code.PaSaleGb;
import com.cware.partner.common.code.SaleGb;
import com.cware.partner.sync.domain.entity.EntpUser;
import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;
import com.cware.partner.sync.repository.EntpUserRepository;
import com.cware.partner.sync.repository.PaHalfBrandMappingRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * 하프클럽 입점 제약조건 체크
 *
 */
@Slf4j
@Component
public class HalfFilter extends Filter {

	@Autowired
	EntpUserRepository entpUserRepostiory;
	
	@Autowired
	PaHalfBrandMappingRepository paHalfBrandMappingRepository;

	public boolean apply(Goods goods, PaGoodsTarget target) {

		tag = "하프클럽필터";
		
		//***하프클럽 답변상으로는 특별히 상품에 대해서 제약조건 없다고 함 ***
		// 주소정제 성공한 건 들만 입점처리
		// 업체출고지 유효성 체크
		EntpUser shipMan = goods.getShipManUser();
		if (shipMan.getStdPost() == null || shipMan.getStdRoadPost() == null) {
			target.setExcept(true);
			target.setExceptNote("업체출고지 주소가 유효하지 않습니다.");
			log.info("{}: {} 상품: {}, 업체: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getShipEntpCode());
			logFilter("HALF-SHIP_MAN_ADDR", target);
			return false;
		}

		// 업체회수지 유효성 체크
		EntpUser returnMan = goods.getReturnManUser();
		if (returnMan.getStdPost() == null || returnMan.getStdRoadPost() == null) {
			target.setExcept(true);
			target.setExceptNote("업체회수지 주소가 유효하지 않습니다.");
			log.info("{}: {} 상품: {}, 업체: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getShipEntpCode());
			logFilter("HALF-RETURN_MAN_ADDR", target);
			return false;
		}
		
		// 업체 출고/회수 담당자 체크
		if ( !"30".equals(shipMan.getEntpManGb()) || !"20".equals(returnMan.getEntpManGb()) ) {
			target.setExcept(true);
			target.setExceptNote("업체 출고/회수 담당자가 올바르지 않습니다.");
			log.info("{}: {} 상품: {}, 업체: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getShipEntpCode());
			logFilter("HALF-ENTP_MAN_GB", target);
			return false;
		}
		
		// 하프클럽 브랜드 매핑 체크
		int brandCnt = paHalfBrandMappingRepository.validateHalfBrandMapping(target.getPaCode(), goods.getBrandCode(), goods.getReturnNoYn());
		if(brandCnt < 1) {
			target.setExcept(true);
			target.setExceptNote("하프클럽 브랜드 정보가 존재하지 않습니다 : " + goods.getBrandCode());
			log.info("{}: {} 제휴사: {}, 브랜드: {}, 반품/교환불가 여부:{} " , tag, target.getExceptNote(), target.getPaCode(), goods.getBrandCode(), goods.getReturnNoYn());
			logFilter("HALF-BRAND-MAPPING", target);
			return false;
		}
		
		// 옵션명 중복 처리
		List<String> optionList = new ArrayList<String>();
		goods.getGoodsDtList().stream().filter( 
				goodsDt -> SaleGb.FORSALE.code().equals(goodsDt.getSaleGb())).collect(Collectors.toList())
										.forEach( goodsDt -> optionList.add(goodsDt.getGoodsdtInfo()));
		
		if (optionList.size() != optionList.stream().distinct().count()) {
			target.setExcept(true);
			target.setExceptNote("동일한 옵션명이 있습니다.");
			log.info("{}: {} 상품: {}, 옵션수: {}", tag, target.getExceptNote(), target.getGoodsCode(), goods.getGoodsDtList().size());
			logFilter("HALF-GOODSDT_INFO_DUP", target);
			return false;
		}
		
		
		// 하프클럽 제외소싱상품 예외 입점 , 'HALFCLUB, BORI, TLIFE 와 별개로 YIC, SELLERUB 추가 연동 금지'
		if (Arrays.stream(ExceptHalfSourcingCode.values()).anyMatch(v -> v.name().equals(goods.getSourcingCode()))) {
			if (goods.getSourcingExceptInput() == null || (!goods.getSourcingExceptInput().getPaAllYn().equals("1")
					&& !goods.getSourcingExceptInput().getPaGroupCode().contains(target.getPaGroupCode()))) {
				target.setExceptNote("하프클럽 제휴연동 대상 소싱 상품이 아닙니다: " + goods.getSourcingCode());
				target.setExcept(true);
				log.info("{}: {} [{}-{}]", tag, target.getExceptNote(), target.getGoodsCode(), target.getPaGroupCode());
				logFilter("HALF-SOURCING_CODE", target);
				return false;
			}
		}
		
		//하프클럽 당사배송 상품 연동하지 않음 
		//2차제휴사에서 고객직접발송 또는 판매자 지정택배 SK스토아 SCM에서 인입 거부, 업무 협의 후 당사배송상품은 연동하지 않기로 함 
		if("10".equals(goods.getDelyType())) {
			target.setExceptNote("하프클럽은 당사배송 상품 연동하지 않습니다.");
			target.setExcept(true);
			log.info("{}: {} [{}-{}]", tag, target.getExceptNote(), target.getGoodsCode(), target.getPaGroupCode());
			logFilter("HALF_DELY_TYPE", target);
			return false;
		}
		
		if (PaSaleGb.FORSALE.code().equals(target.getPaSaleGb())) {// 수정시에만 체크
			//교환반품속성 가능 -> 불가능 변경 불가
			if("0".equals(goods.getHalfReturnNoYn()) && "1".equals(goods.getReturnNoYn())) {
				target.setExceptNote("교환/반품불가상품으로 변경할 수 없습니다.");
				target.setExcept(true);
				log.info("{}: {} [{}-{}]", tag, target.getExceptNote(), target.getGoodsCode(), target.getPaGroupCode());
				logFilter("HALF_RETURN_NO_YN", target);
				return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean apply(Goods goods) {
		return false;
	}

}
