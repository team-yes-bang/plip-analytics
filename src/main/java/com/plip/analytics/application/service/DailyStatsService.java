package com.plip.analytics.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.plip.analytics.adapter.out.persistence.AgitDailyStatsEntity;
import com.plip.analytics.adapter.out.persistence.AgitDailyStatsRepository;
import com.plip.analytics.adapter.out.persistence.MetricType;
import com.plip.analytics.application.port.out.AnalyticsEventTopics;
import com.plip.analytics.application.port.out.EventPublisherPort;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyStatsService {

	private final AgitDailyStatsRepository dailyStatsRepository;
	private final EventPublisherPort eventPublisherPort;
	private final ObjectMapper objectMapper;

	@Transactional
	public void applyRecordedMetric(JsonNode payload) {
		String agitUuid = text(payload, "agitUuid");
		String typeName = text(payload, "metricType");
		if (agitUuid == null || typeName == null) {
			return;
		}
		MetricType type;
		try {
			type = MetricType.valueOf(typeName);
		} catch (IllegalArgumentException ex) {
			log.warn("unknown metricType={}", typeName);
			return;
		}
		Instant occurredAt = parseInstant(payload.get("occurredAt"));
		LocalDate statDate = occurredAt.atZone(ZoneOffset.UTC).toLocalDate();
		AgitDailyStatsEntity stats = dailyStatsRepository
				.findByStatDateAndAgitUuid(statDate, agitUuid)
				.orElseGet(() -> AgitDailyStatsEntity.empty(statDate, agitUuid));
		stats.increment(type);
		dailyStatsRepository.save(stats);

		ObjectNode updated = objectMapper.createObjectNode();
		updated.put("agitUuid", agitUuid);
		updated.put("statDate", statDate.toString());
		updated.put("metricType", type.name());
		eventPublisherPort.publish(AnalyticsEventTopics.STATS_UPDATED, agitUuid, updated.toString());
	}

	private static String text(JsonNode node, String field) {
		JsonNode value = node.get(field);
		if (value == null || value.isNull() || value.asText().isBlank()) {
			return null;
		}
		return value.asText();
	}

	private static Instant parseInstant(JsonNode node) {
		if (node == null || node.isNull() || node.asText().isBlank()) {
			return Instant.now();
		}
		try {
			return Instant.parse(node.asText());
		} catch (Exception ignored) {
			return Instant.now();
		}
	}
}
