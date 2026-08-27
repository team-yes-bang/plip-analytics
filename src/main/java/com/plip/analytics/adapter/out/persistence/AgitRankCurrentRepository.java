package com.plip.analytics.adapter.out.persistence;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgitRankCurrentRepository extends JpaRepository<AgitRankCurrentEntity, Long> {

	void deleteByRankType(RankType rankType);

	Page<AgitRankCurrentEntity> findByRankTypeOrderByRankNoAsc(RankType rankType, Pageable pageable);

	List<AgitRankCurrentEntity> findByRankTypeAndAgitUuidIn(RankType rankType, List<String> agitUuids);
}
