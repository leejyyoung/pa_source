package com.cware.partner.sync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaGoodsEvent;

@Repository
public interface PaGoodsEventRepository extends JpaRepository<PaGoodsEvent, String> {

	List<PaGoodsEvent> findByGoodsCode(String goodsCode);

}
