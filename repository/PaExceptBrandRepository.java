package com.cware.partner.sync.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaExceptBrand;

@Repository
public interface PaExceptBrandRepository extends JpaRepository<PaExceptBrand, String> {

	Optional<PaExceptBrand> findByEntpCodeAndBrandCode(String entpCode, String brandCode);

}
