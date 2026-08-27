package com.plip.analytics.adapter.out.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgitDailyStatsRepository extends JpaRepository<AgitDailyStatsEntity, Long> {

	Optional<AgitDailyStatsEntity> findByStatDateAndAgitUuid(LocalDate statDate, String agitUuid);

	List<AgitDailyStatsEntity> findByStatDateBetween(LocalDate from, LocalDate to);
}
