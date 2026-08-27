package com.plip.analytics.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
		name = "agit_daily_stats",
		uniqueConstraints = @UniqueConstraint(
				name = "uq_agit_daily_stats",
				columnNames = {"stat_date", "agit_uuid"}
		)
)
public class AgitDailyStatsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "stat_date", nullable = false)
	private LocalDate statDate;

	@Column(name = "agit_uuid", nullable = false, length = 36)
	private String agitUuid;

	@Column(name = "impressions", nullable = false)
	private long impressions;

	@Column(name = "clicks", nullable = false)
	private long clicks;

	@Column(name = "views", nullable = false)
	private long views;

	@Column(name = "joins", nullable = false)
	private long joins;

	@Column(name = "leaves", nullable = false)
	private long leaves;

	public static AgitDailyStatsEntity empty(LocalDate statDate, String agitUuid) {
		AgitDailyStatsEntity entity = new AgitDailyStatsEntity();
		entity.statDate = statDate;
		entity.agitUuid = agitUuid;
		return entity;
	}

	public void increment(MetricType type) {
		switch (type) {
			case SEARCH_IMPRESSION -> impressions++;
			case SEARCH_CLICK -> clicks++;
			case DETAIL_VIEW -> views++;
			case JOIN_APPROVED -> joins++;
			case LEAVE -> leaves++;
		}
	}

	public double popularScore() {
		return 0.4 * views + 0.3 * clicks + 0.2 * joins + 0.1 * impressions;
	}
}
