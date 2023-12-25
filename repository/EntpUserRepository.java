package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.EntpUser;
import com.cware.partner.sync.domain.id.EntpUserId;

@Repository
public interface EntpUserRepository extends JpaRepository<EntpUser, EntpUserId> {

	// 업체주소/연락처 유효성 체크
	@Query(value = "select count(1) "
			+ " from EntpUser eu "
			+ "where eu.entpCode = :entpCode "
			+ " and eu.entpManSeq = :entpManSeq"
			+ " and nvl(eu.stdPostAddr1, eu.postAddr) is not null "
			+ " and nvl(eu.stdPostAddr2, eu.addr) is not null "
			+ " and length(eu.entpManDdd || eu.entpManTel1 || eu.entpManTel2) between 9 and 12 "
			+ " and length(eu.entpManHp1 || eu.entpManHp2 || eu.entpManHp3) between 9 and 12 "
			)
	int validateEntpUser(String entpCode, String entpManSeq);

	// 업체주소 유효성 체크
	@Query(value = "select count(1) "
			+ " from EntpUser eu "
			+ "where eu.entpCode = :entpCode "
			+ " and eu.entpManSeq = :entpManSeq"
			+ " and length(nvl(eu.stdPost, eu.postNo)) = 5 "
			+ " and nvl(eu.stdPostAddr1, eu.postAddr) is not null "
			+ " and nvl(eu.stdPostAddr2, eu.addr) is not null "
			)
	int validateEntpUserAddress(String entpCode, String entpManSeq);

	// 업체주소 유효성 체크 (지번 and 도로명주소)
	@Query(value = "select count(1) "
			+ " from EntpUser eu "
			+ "where eu.entpCode = :entpCode "
			+ " and eu.entpManSeq = :entpManSeq"
			+ " and eu.stdPost is not null "
			+ " and eu.stdRoadPost is not null "
			)
	int validateEntpUserAddressAll(String entpCode, String entpManSeq);

}

