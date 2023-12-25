package com.cware.partner.sync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaNaverGoodsName;

@Repository
public interface PaNaverGoodsNameRepository extends JpaRepository<PaNaverGoodsName, String> {

	List<PaNaverGoodsName> findByPaGoodsName(String paGoodsName);
}
