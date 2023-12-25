package com.cware.partner.sync.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cware.partner.sync.domain.entity.PaGoodsSync;
import com.cware.partner.sync.service.ProductSyncService;

@RestController
@RequestMapping(path="/affiliate/")
public class AffiliateProductApi {

	@Autowired
	ProductSyncService productSyncService;

	@GetMapping(value="/product/sync/{goodsCode}")
	public ResponseEntity<?> productSync(@PathVariable String goodsCode) {

		PaGoodsSync sync = null;
		try {
			sync = productSyncService.syncProduct(goodsCode);
		} catch (Exception e) {
			sync = new PaGoodsSync();
			sync.setTargetCnt(-1);
		}

		return new ResponseEntity<>(sync, HttpStatus.OK);
	}
}
