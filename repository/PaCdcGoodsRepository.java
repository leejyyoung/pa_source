package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaCdcGoods;

@Repository
public interface PaCdcGoodsRepository extends JpaRepository<PaCdcGoods, String> {

}
