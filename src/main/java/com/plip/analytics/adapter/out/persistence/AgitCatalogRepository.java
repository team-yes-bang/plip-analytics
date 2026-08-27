package com.plip.analytics.adapter.out.persistence;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgitCatalogRepository extends JpaRepository<AgitCatalogEntity, String> {

	Page<AgitCatalogEntity> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

	Page<AgitCatalogEntity> findByStatusAndAgitNameContainingIgnoreCaseOrderByCreatedAtDesc(
			String status,
			String agitName,
			Pageable pageable
	);

	List<AgitCatalogEntity> findByStatusAndAgitUuidIn(String status, List<String> agitUuids);
}
