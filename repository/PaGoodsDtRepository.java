package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaGoodsDt;
import com.cware.partner.sync.domain.id.GoodsDtId;

@Repository
public interface PaGoodsDtRepository extends JpaRepository<PaGoodsDt, GoodsDtId> {

}
