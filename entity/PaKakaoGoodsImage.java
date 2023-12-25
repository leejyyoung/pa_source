package com.cware.partner.sync.domain.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.cware.partner.sync.domain.id.PaKakaoImageId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicInsert
@Table(name="TPAKAKAOGOODSIMAGE")
@IdClass(PaKakaoImageId.class)
public class PaKakaoGoodsImage {

	@Id
	private String paGroupCode;
	@Id
	private String goodsCode;
	@Id
	private String imageGb; // 이미지 TYPE

	private String imageUrl; // 이미지경로
	private String stoaImage; // sk스토아이미지
	private String kakaoImage; // 카카오이미지 url
	private Timestamp uploadDate; // 카카오이미지 업로드 날짜
	private String uploadStatus; // 전송상태(00:대상, 20:반려, 30:승인)

	@Column(updatable=false)
	private String insertId;
	@Column(updatable=false)
	private Timestamp insertDate;
	private String modifyId;
	private Timestamp modifyDate;
}
