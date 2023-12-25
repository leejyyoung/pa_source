package com.cware.partner.sync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cware.partner.sync.domain.entity.PaKakaoGoodsImage;
import com.cware.partner.sync.domain.id.PaKakaoImageId;

@Repository
public interface PaKakaoGoodsImageRepository extends JpaRepository<PaKakaoGoodsImage, PaKakaoImageId> {

	@Modifying
	@Query(value = " insert into tpakakaogoodsimage "
			+ " ( pa_group_code, goods_code"
			+ " , image_gb, image_url, stoa_image"
			+ " , upload_status, insert_id, insert_date, modify_id, modify_date ) "
			+ " select pa_group_code, goods_code"
			+ "      , replace(image_gb, 'IMAGE_', '') as image_gb "
			+ "      , image_url, stoa_image "
			+ "      , '00', :modifyId, sysdate, :modifyId, sysdate "
			+ "   from (select * from tpagoodsimage where goods_code = :goodsCode and pa_group_code = :paGroupCode)"
			+ " unpivot (stoa_image for image_gb in (image_p, image_ap, image_bp, image_cp, image_dp)) "
			, nativeQuery = true)
	int insertGoodsImage(String goodsCode, String paGroupCode, String modifyId);

	int deleteByGoodsCodeAndPaGroupCode(String goodsCode, String paGroupCode);

}
