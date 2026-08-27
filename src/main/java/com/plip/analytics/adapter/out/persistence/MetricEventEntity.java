package com.plip.analytics.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
		name = "metric_events",
		indexes = {
				@Index(name = "idx_metric_agit_type_occurred", columnList = "agit_uuid, metric_type, occurred_at")
		}
)
public class MetricEventEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "agit_uuid", nullable = false, length = 36)
	private String agitUuid;

	@Column(name = "actor_user_uuid", length = 36)
	private String actorUserUuid;

	@Enumerated(EnumType.STRING)
	@Column(name = "metric_type", nullable = false, length = 40)
	private MetricType metricType;

	@Column(name = "occurred_at", nullable = false)
	private Instant occurredAt;

	public static MetricEventEntity create(
			String agitUuid,
			String actorUserUuid,
			MetricType metricType,
			Instant occurredAt
	) {
		MetricEventEntity entity = new MetricEventEntity();
		entity.agitUuid = agitUuid;
		entity.actorUserUuid = actorUserUuid;
		entity.metricType = metricType;
		entity.occurredAt = occurredAt;
		return entity;
	}
}
