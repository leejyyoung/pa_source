package com.cware.partner.sync.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cware.partner.common.code.ExceptNaverLgroup;
import com.cware.partner.common.code.ExceptNaverOfferType;
import com.cware.partner.common.code.SaleGb;
import com.cware.partner.sync.domain.entity.Goods;
import com.cware.partner.sync.domain.entity.GoodsDt;
import com.cware.partner.sync.domain.entity.PaGoodsTarget;
import com.cware.partner.sync.domain.entity.PaNaverExceptCert;
import com.cware.partner.sync.domain.entity.PaNaverGoodsName;
import com.cware.partner.sync.repository.PaTargetExceptRepository;
import com.cware.partner.sync.repository.PaNaverExceptCertRepository;
import com.cware.partner.sync.repository.PaNaverGoodsNameRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 네이버 입점 제약조건 체크
 *
 */
@Slf4j
@Component
public class NaverFilter extends Filter {

	@Autowired
	private PaTargetExceptRepository paTargetExceptRepository;
	
	@Autowired
	private PaNaverExceptCertRepository paNaverExceptCert;
	
	@Autowired
	private PaNaverGoodsNameRepository paNaverGoodsNameRepository;

	public boolean apply(Goods goods, PaGoodsTarget target) {
		
		Optional<PaNaverExceptCert> naverExceptCert = paNaverExceptCert.findById(goods.getGoodsCode());		
		tag = "네이버필터";
		String naverExceptCertGoodsCode = "";
		
		// 네이버예외연동상품 여부 조회
		if (naverExceptCert.isPresent()) {
			naverExceptCertGoodsCode = naverExceptCert.get().getGoodsCode();
		}

		// 인증정보 있을 경우 입점 불가
		// 2023.04.12 PaNaverExceptCert 존재할 경우 해당필터 스킵 처리
		if ((paTargetExceptRepository.countNaverExceptCode(goods.getLmsdCode()) > 0) 
				&& !naverExceptCertGoodsCode.equals(target.getGoodsCode())) {
			if (goods.getSourcingExceptInput() == null || (!goods.getSourcingExceptInput().getPaAllYn().equals("1")
					&& !goods.getSourcingExceptInput().getPaGroupCode().contains(target.getPaGroupCode()))) {
				target.setExcept(true);
				target.setExceptNote("인증정보가 필요한 상품분류는 제외됩니다.");
				log.info("{}: {} [{}-{}] 상품: {}, 상품분류 : {}", tag, target.getExceptNote(), target.getGoodsCode(),
						goods.getLmsdCode());
				logFilter("NAVER-EXCEPT_CODE", target);
				return false;
			}
		}
		
		// 특정 상품분류 입점 제외1
		// 2023.04.12 PaNaverExceptCert 존재할 경우 해당필터 스킵 처리
		if ((Arrays.stream(ExceptNaverLgroup.values()).anyMatch(v -> v.code().equals(goods.getLgroup())))
				&& !naverExceptCertGoodsCode.equals(target.getGoodsCode())) {
			target.setExcept(true);
			target.setExceptNote("입점제외되는 상품분류 입니다");
			log.info("{}: {} [{}-{}] 상품: {}, 대분류: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getLgroup());
			logFilter("NAVER-LGROUP", target);
			return false;
		}

		// 특정 정보고시 입점 제외
		// 2023.04.12 PaNaverExceptCert 존재할 경우 해당필터 스킵 처리
		if (Arrays.stream(ExceptNaverOfferType.values()).anyMatch(v -> v.code().equals(goods.getOfferType()))				
				&& !naverExceptCertGoodsCode.equals(target.getGoodsCode())) {
			target.setExcept(true);
			target.setExceptNote("입점제외되는 정보고시 타입 입니다");
			log.info("{}: {} [{}-{}] 상품: {}, 정보고시: {}", tag, target.getExceptNote(), target.getGoodsCode(),
					goods.getOfferType());
			logFilter("NAVER-OFFER_TYPE", target);
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
					logFilter("NAVER-GOODSDT_INFO", target);
					return false;
				}
				optionList.add(goodsDt.getGoodsdtInfo());
			}
		}

		// 동일 옵션명 등록 불가
		if (optionList.size() != optionList.stream().distinct().count()) {
			target.setExcept(true);
			target.setExceptNote("동일한 옵션명이 있습니다.");
			log.info("{}: {} 상품: {}, 옵션수: {}", tag, target.getExceptNote(), target.getGoodsCode(), optionList.size());
			logFilter("NAVER-GOODSDT_INFO_DUP", target);
			return false;
		}

		// 동일 상품명 등록 불가
		if(!"20".equals(target.getPaSaleGb())) { // 입점 전일 경우
			//한글/영어/숫자 외 모두 제거 후 비교
			List<PaNaverGoodsName> paNaverGoodsName = paNaverGoodsNameRepository.findByPaGoodsName(goods.getGoodsName().replaceAll("[^\u3131-\u3163\uAC00-\uD7A30-9a-zA-Z]", ""));
			if(paNaverGoodsName.size() > 0) {
				target.setExcept(true);
				target.setExceptNote("네이버 정책에 따라 기존 입점된 상품과 상품명이 중복되어 입점반려 처리됩니다. (기입점상품 : " + paNaverGoodsName.get(0).getGoodsCode() + ")");
				log.info("{}: {} 상품: {}, 상품명: {}", tag, target.getExceptNote(), target.getGoodsCode(), goods.getGoodsName());
				logFilter("NAVER-GOODS_NAME_DUP", target);
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
