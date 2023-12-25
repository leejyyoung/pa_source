package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaGoodsSyncLog;
import com.cware.partner.sync.domain.id.PaSyncId;

@Repository
public interface PaGoodsSyncLogRepository extends JpaRepository<PaGoodsSyncLog, PaSyncId> {

}

