package com.cware.partner.sync.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cware.partner.common.code.Application;
import com.cware.partner.common.code.CdcReason;
import com.cware.partner.common.code.PaCode;
import com.cware.partner.common.service.CommonService;
import com.cware.partner.sync.domain.entity.PaEntpSlip;
import com.cware.partner.sync.domain.entity.PaGoodsSync;
import com.cware.partner.sync.domain.entity.PaGoodsSyncLog;
import com.cware.partner.sync.repository.PaEntpSlipRepository;
import com.cware.partner.sync.repository.PaGoodsSyncLogRepository;
import com.cware.partner.sync.repository.PaGoodsSyncRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 업체주소 동기화 (위메프제외)
 */
@Slf4j
@Service
public class EntpAddrSyncService {

	@Autowired
	PaEntpSlipRepository paEntpSlipRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	PaGoodsSyncRepository paGoodsSyncRepository;

	@Autowired
	PaGoodsSyncLogRepository goodsSyncLogRepository;

	// 페이징 사이즈
	@Value("${partner.sync.page-size}")
	int PAGE_SIZE;

	// 동기화할 제휴사코드 목록
	@Value("${partner.sync.pa-codes}")
	List<String> PA_CODES;

	CdcReason reason = CdcReason.ENTPUSER_MODIFY;

	public void executeEntpAddressSync() {

		log.info("*******Partner Enterprise Address Sync service start*******");
		// 업체주소 동기화
		entpAddressSync();
		log.info("*******Partner Enterprise Address Sync service end*******");
	}

	public void entpAddressSync() {

		int targetCnt = 0;
		int procCnt = 0;
		int syncCnt = 0;

		Slice<PaEntpSlip> slice = null;

		Pageable pageable = PageRequest.of(0, PAGE_SIZE);

		Timestamp currentDate = commonService.currentDate();
		PaGoodsSync goodsSync = new PaGoodsSync();
		goodsSync.setStartDate(currentDate);
		paGoodsSyncRepository.save(goodsSync);

		log.info("제휴사별 업체주소 타겟팅 [{}({})] 동기화시작일시:{} Start....", reason.name(), goodsSync.getGoodsSyncNo(), goodsSync.getStartDate());

		while (true) {

			// 업체주소 변경리스트 추출
			slice = paEntpSlipRepository.findModifyTargetList(pageable, PA_CODES);
			List<PaEntpSlip> targetList = slice.getContent();

			targetCnt = targetCnt + targetList.size();

			log.info("타겟팅 추출[{}({})] 타겟:{}", reason.name(), goodsSync.getGoodsSyncNo(), targetList.size());

			goodsSync.setTargetCnt(targetCnt);
			paGoodsSyncRepository.save(goodsSync);

			for (PaEntpSlip target : targetList) {

				// 제휴사별 업체주소 동기화
				syncCnt += syncEntpSlip(target, goodsSync.getGoodsSyncNo());
				procCnt++;
			}

			goodsSync.setProcCnt(procCnt);
			goodsSync.setSyncCnt(syncCnt);
			paGoodsSyncRepository.save(goodsSync);

			if (!slice.hasNext())
				break;

		}

		goodsSync.setEndDate(commonService.currentDate());
		paGoodsSyncRepository.save(goodsSync);

		log.info("제휴사별 업체주소 타겟팅 [{}({})] 동기화완료일시:{} 타겟:{} 처리:{} End ==> {}s elapsed.", reason.name(),
				goodsSync.getGoodsSyncNo(), goodsSync.getEndDate(), goodsSync.getTargetCnt(), goodsSync.getProcCnt(),
				(goodsSync.getEndDate().getTime() - goodsSync.getStartDate().getTime()) / 1000);
	}

	// 업체주소만 동기화
	public int syncEntpSlip(PaEntpSlip target, long goodsSyncNo) {

		Timestamp currentDate = commonService.currentDate();

		target.setTransTargetYn("1");
		target.setLastSyncDate(currentDate);
		target.setModifyDate(currentDate);
		target.setModifyId(Application.ID.code());
		paEntpSlipRepository.save(target);

		log.info("업체주소동기화 [{}({})] 업체:{}-{} 제휴사:{}", reason.name(), goodsSyncNo, target.getEntpCode(),
				target.getEntpManSeq(), target.getPaCode());

		return 0;
	}

	// 현재까지 적용된 제휴사 모두 업체주소 변경시 상품타겟팅 하지 않음.
	// 상품별 타겟팅 로직 호출하지 않음
	// TODO 모든 제휴사 적용 후 미사용시 제거 예정
	public int syncEntpSlipGoods(PaEntpSlip target, long goodsSyncNo) {

		// oracle in 최대개수 1000
        Pageable pageable = PageRequest.of(0, 1000, Sort.by("goodsCode"));

		Slice<String> slice = null;
		List<String> targetList;
		int syncCnt = 0;
		String syncNote = "업체주소동기화";
		String groupCode = "";

		Timestamp currentDate = commonService.currentDate();

		target.setTransTargetYn("1");
		target.setLastSyncDate(currentDate);
		target.setModifyDate(currentDate);
		target.setModifyId(Application.ID.code());
		paEntpSlipRepository.save(target);

		log.info("업체주소동기화 [{}({})] 업체:{}-{} 제휴사:{}", reason.name(), goodsSyncNo, target.getEntpCode(),
				target.getEntpManSeq(), target.getPaCode());


		// 쿠팡은 업체주소에 출고지만 등록되며, 출고지 변경시 상품동기화를 수행하지 않음, 회수지에 대한 동기화는 상품동기화에서 수행
		if (PaCode.COUPANG_TV.code().equals(target.getPaCode()) || PaCode.COUPANG_ONLINE.code().equals(target.getPaCode())) return 0;

		while (true) {

			// TODO 레거시 출고지에 대해서만 상품연동 타겟팅을 하고 회수지는 하지 않고 있음, 체크 필요
			// 대상 상품 추출
			if (target.getEntpCode().equals("100001") && target.getEntpManSeq().equals("003")) {
				// 당사배송상품 추출
				slice = paEntpSlipRepository.findCenterGoodsTargetList(target.getPaCode(), pageable);
			} else {
				slice = paEntpSlipRepository.findGoodsTargetList(target.getEntpCode(), target.getEntpManSeq(), target.getPaCode(), pageable);
			}
			targetList = slice.getContent();

			if (targetList.isEmpty()) break;

			// 제휴 상품 연동 설정 (락을 최소화하기 위해 각자 커밋)
			if (PaCode.SK11ST_TV.code().equals(target.getPaCode()) || PaCode.SK11ST_ONLINE.code().equals(target.getPaCode())) {
				paEntpSlipRepository.enable11stGoodsTransTarget(targetList, target.getPaCode(), currentDate, Application.ID.code());
				groupCode = PaCode.SK11ST_TV.groupCode();
				log.info("상품동기화 [{}({})] 11번가({}), 동기화상품:{}", reason.name(), goodsSyncNo, target.getPaCode(), targetList.size());
			} else if (PaCode.EBAY_TV.code().equals(target.getPaCode()) || PaCode.EBAY_ONLINE.code().equals(target.getPaCode())) {
				paEntpSlipRepository.enableEbayGoodsTransTarget(targetList, target.getPaCode(), currentDate, Application.ID.code());
				groupCode = PaCode.EBAY_TV.groupCode();
				log.info("상품동기화 [{}({})] 이베이({}), 동기화상품:{}", reason.name(), goodsSyncNo, target.getPaCode(), targetList.size());
			} else if (PaCode.NAVER.code().equals(target.getPaCode())) {
				paEntpSlipRepository.enableNaverGoodsTransTarget(targetList, target.getPaCode(), currentDate, Application.ID.code());
				groupCode = PaCode.NAVER.groupCode();
				log.info("상품동기화 [{}({})] 네이버({}), 동기화상품:{}", reason.name(), goodsSyncNo, target.getPaCode(), targetList.size());
			} else if (PaCode.COUPANG_TV.code().equals(target.getPaCode()) || PaCode.COUPANG_ONLINE.code().equals(target.getPaCode())) {
				paEntpSlipRepository.enableCopnGoodsTransTarget(targetList, target.getPaCode(), currentDate, Application.ID.code());
				groupCode = PaCode.COUPANG_TV.groupCode();
				log.info("상품동기화 [{}({})] 쿠팡({}), 동기화상품:{}-{}", reason.name(), goodsSyncNo, target.getPaCode(), targetList.size(), targetList);
			} else if (PaCode.INTERPARK_TV.code().equals(target.getPaCode()) || PaCode.INTERPARK_ONLINE.code().equals(target.getPaCode())) {
				paEntpSlipRepository.enableIntpGoodsTransTarget(targetList, target.getPaCode(), currentDate, Application.ID.code());
				groupCode = PaCode.INTERPARK_TV.groupCode();
				log.info("상품동기화 [{}({})] 인터파크({}), 동기화상품:{}", reason.name(), goodsSyncNo, target.getPaCode(), targetList.size());
			} else if (PaCode.LOTTEON_TV.code().equals(target.getPaCode()) || PaCode.LOTTEON_ONLINE.code().equals(target.getPaCode())) {
				paEntpSlipRepository.enableLtonGoodsTransTarget(targetList, target.getPaCode(), currentDate, Application.ID.code());
				groupCode = PaCode.LOTTEON_TV.groupCode();
				log.info("상품동기화 [{}({})] 롯데온({}), 동기화상품:{}", reason.name(), goodsSyncNo, target.getPaCode(), targetList.size());
			} else if (PaCode.TMON_TV.code().equals(target.getPaCode()) || PaCode.TMON_ONLINE.code().equals(target.getPaCode())) {
				paEntpSlipRepository.enableTmonGoodsTransTarget(targetList, target.getPaCode(), currentDate, Application.ID.code());
				groupCode = PaCode.TMON_TV.groupCode();
				log.info("상품동기화 [{}({})] 티몬({}), 동기화상품:{}", reason.name(), goodsSyncNo, target.getPaCode(), targetList.size());
			} else if (PaCode.SSG_TV.code().equals(target.getPaCode()) || PaCode.SSG_ONLINE.code().equals(target.getPaCode())) {
				paEntpSlipRepository.enableSsgGoodsTransTarget(targetList, target.getPaCode(), currentDate, Application.ID.code());
				groupCode = PaCode.SSG_TV.groupCode();
				log.info("상품동기화 [{}({})] 쓱닷컴({}), 동기화상품:{}", reason.name(), goodsSyncNo, target.getPaCode(), targetList.size());
			}

			syncCnt += targetList.size();

			// 상품동기화 로그 생성
			List<PaGoodsSyncLog> logList = new ArrayList<PaGoodsSyncLog>();

			for (String goodsCode : targetList) {
				PaGoodsSyncLog syncLog = PaGoodsSyncLog.builder()
						.goodsCode(goodsCode)
						.cdcReasonCode(reason.code())
						.syncNote(syncNote)
						.goodsSyncNo(goodsSyncNo)
						.paGroupCode(groupCode).build();
				logList.add(syncLog);
			}
			goodsSyncLogRepository.saveAll(logList);

			if (!slice.hasNext())
				break;

			pageable = slice.nextPageable();
		}

		return syncCnt;
	}
}
