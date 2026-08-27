package com.plip.analytics.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "agit_catalog")
public class AgitCatalogEntity {

	@Id
	@Column(name = "agit_uuid", length = 36)
	private String agitUuid;

	@Column(name = "agit_name", nullable = false, length = 40)
	private String agitName;

	@Column(name = "description", length = 200)
	private String description;

	@Column(name = "thumbnail_path", length = 255)
	private String thumbnailPath;

	@Column(name = "status", nullable = false, length = 20)
	private String status;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	public static AgitCatalogEntity create(
			String agitUuid,
			String agitName,
			String description,
			String thumbnailPath,
			String status,
			Instant createdAt
	) {
		AgitCatalogEntity entity = new AgitCatalogEntity();
		entity.agitUuid = agitUuid;
		entity.agitName = agitName;
		entity.description = description;
		entity.thumbnailPath = thumbnailPath;
		entity.status = status;
		entity.createdAt = createdAt;
		return entity;
	}
}
