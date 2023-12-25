package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaStockExcept;

@Repository
public interface PaStockExceptRepository extends JpaRepository<PaStockExcept, String> {
   
	boolean existsByTargetGbAndTargetCodeAndUseYn(String targetCode, String targetGb, String useYn);
	
}
