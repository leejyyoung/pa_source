package com.cware.partner.sync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaNoticeApply;
import com.cware.partner.sync.domain.entity.PaNoticeTarget;
import com.cware.partner.sync.domain.id.PaNoticeId;

@Repository
public interface PaNoticeApplyRepository extends JpaRepository<PaNoticeApply, PaNoticeId> {

	@Query(value = "select g "
			+ " from PaNoticeM n "
			+ " inner join PaNoticeTarget g on n.noticeNo = g.noticeNo "
			+ " where n.useCode = '00' "
			+ " and current_date between n.noticeBdate and n.noticeEdate"
			+ " and g.goodsCode = :goodsCode "
			+ " and n.paGroupCode like '%'||:paGroupCode||'%' "
			)
	List<PaNoticeTarget> selecNoticeTarget(String goodsCode, String paGroupCode);

	List<PaNoticeApply> findByGoodsCodeAndPaGroupCode(String goodsCode, String paGroupCode);

}
