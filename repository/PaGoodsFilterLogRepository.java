package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaGoodsFilterLog;
import com.cware.partner.sync.domain.id.PaFilterId;

@Repository
public interface PaGoodsFilterLogRepository extends JpaRepository<PaGoodsFilterLog, PaFilterId> {

}

