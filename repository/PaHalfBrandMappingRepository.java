package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.cware.partner.sync.domain.entity.PaHalfBrandMapping;
import com.cware.partner.sync.domain.id.PaHalfBrandId;

@Repository
public interface PaHalfBrandMappingRepository extends JpaRepository<PaHalfBrandMapping, PaHalfBrandId> {
	
	@Query(value="select b.paBrandNo from PaHalfBrandMapping b where b.paCode = :paCode and b.brandCode = :brandCode and b.returnNoYn = :returnNoYn ")
	String findHalfBrandMapping(String paCode, String brandCode, String returnNoYn);
    
    @Query(value=" select count(1) from tpahalfbrandmapping b "
            + " inner join tpahalfbrand a on a.pa_code = b.pa_code and a.pa_brand_no = b.pa_brand_no "
            + " where b.pa_code = :paCode and b.brand_code = :brandCode  and a.use_yn = '1' and b.return_no_yn = :returnNoYn ", nativeQuery = true )
    int validateHalfBrandMapping(String paCode, String brandCode, String returnNoYn);
}
