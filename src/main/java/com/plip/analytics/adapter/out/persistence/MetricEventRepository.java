package com.plip.analytics.adapter.out.persistence;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MetricEventRepository extends JpaRepository<MetricEventEntity, Long> {

	@Query("""
			select e.agitUuid as agitUuid, e.metricType as metricType, count(e) as cnt
			  from MetricEventEntity e
			 where e.occurredAt >= :from
			   and e.occurredAt < :to
			 group by e.agitUuid, e.metricType
			""")
	List<MetricWindowCount> countByWindow(
			@Param("from") Instant from,
			@Param("to") Instant to
	);

	interface MetricWindowCount {
		String getAgitUuid();

		MetricType getMetricType();

		long getCnt();
	}
}
