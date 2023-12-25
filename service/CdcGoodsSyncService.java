package com.cware.partner.sync.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cware.partner.common.service.CommonService;
import com.cware.partner.sync.domain.entity.PaCdcGoods;
import com.cware.partner.sync.domain.entity.PaGoodsSync;
import com.cware.partner.sync.repository.PaCdcGoodsRepository;
import com.cware.partner.sync.repository.PaGoodsSyncRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CdcGoodsSyncService {

	@Autowired
	PaCdcGoodsRepository paCdcGoodsRepository;

	@Autowired
	PaGoodsSyncRepository paGoodsSyncRepository;

	@Autowired
	ProductSyncService productSyncService;

	@Autowired
	CommonService commonService;

	// 페이징 사이즈
	@Value("${partner.sync.page-size}")
	int PAGE_SIZE;

	public void executeProductSync() {

		// 상품큐 대상으로 상품동기화
		cdcGoodsSync();
	}

	private void cdcGoodsSync() {

		int targetCnt = 0;
		int procCnt = 0;
		int syncCnt = 0;
		int filterCnt = 0;
		int stopCnt = 0;
		int cnt = 0;

		Slice<PaCdcGoods> slice = null;

		Pageable pageable = PageRequest.of(0, PAGE_SIZE,
				Sort.by("ranking").descending().and(Sort.by("cdcSnapshotNo")).and(Sort.by("goodsCode")));
		PaGoodsSync goodsSync = new PaGoodsSync();
		goodsSync.setStartDate(commonService.currentDate());
		paGoodsSyncRepository.save(goodsSync);

		while (true) {

			// CDC 큐에서 동기화 대상 추출
			slice = paCdcGoodsRepository.findAll(pageable);
			List<PaCdcGoods> targetList = slice.getContent();

			targetCnt = targetCnt + targetList.size();
			log.info("GoodsSyncNo.{} target----> {} ", goodsSync.getGoodsSyncNo(), targetCnt);

			goodsSync.setTargetCnt(targetCnt);
			paGoodsSyncRepository.save(goodsSync);

			List<CompletableFuture<PaGoodsSync>> futures = new ArrayList<>();

			for (PaCdcGoods target : targetList) {
				// 동기화 수행
				futures.add(productSyncService.asyncService(target, goodsSync.getGoodsSyncNo()));
			}

			PaGoodsSync result;
			for(CompletableFuture<PaGoodsSync> future : futures) {
				try {
					result = future.get();
					log.info("Sync result-{} {} ", ++cnt, result);

					// 큐 제거 (비동기 실행후 동기화 영역)
					if (result.getProcCnt() > 0 ) {
						paCdcGoodsRepository.deleteById(result.getSyncGoodsCode());
						log.info("큐 제거: {}", result.getSyncGoodsCode());
						procCnt++;
						syncCnt += result.getSyncCnt();
						filterCnt += result.getFilterCnt();
						stopCnt += result.getStopCnt();
					}
				} catch (Exception e) {
					log.error("상품별 동기화 오류: ", e);
				}
			}

			goodsSync.setProcCnt(procCnt);
			goodsSync.setSyncCnt(syncCnt);
			goodsSync.setFilterCnt(filterCnt);
			goodsSync.setStopCnt(stopCnt);
			paGoodsSyncRepository.save(goodsSync);

			if (!slice.hasNext())
				break;

		}

		goodsSync.setEndDate(commonService.currentDate());
		paGoodsSyncRepository.save(goodsSync);

		log.info("CDC GoodsSyncNo.{} Result: Target:{}, Proc:{}, Sync:{} , Filter:{}, Stop:{} ==> {}s elapsed.",
				goodsSync.getGoodsSyncNo(), targetCnt, procCnt, syncCnt, filterCnt, stopCnt,
				(goodsSync.getEndDate().getTime() - goodsSync.getStartDate().getTime()) / 1000);
		return;

	}

}
