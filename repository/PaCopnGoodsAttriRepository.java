package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaCopnGoodsAttri;
import com.cware.partner.sync.domain.id.PaGoodsAttrSeq;

@Repository
public interface PaCopnGoodsAttriRepository extends JpaRepository<PaCopnGoodsAttri, PaGoodsAttrSeq> {

}

