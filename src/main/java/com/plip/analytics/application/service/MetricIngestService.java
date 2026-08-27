package com.plip.analytics.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.plip.analytics.adapter.out.persistence.MetricEventEntity;
import com.plip.analytics.adapter.out.persistence.MetricEventRepository;
import com.plip.analytics.adapter.out.persistence.MetricType;
import com.plip.analytics.application.port.out.AnalyticsEventTopics;
import com.plip.analytics.application.port.out.EventPublisherPort;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricIngestService {

	private final MetricEventRepository metricEventRepository;
	private final EventPublisherPort eventPublisherPort;
	private final ObjectMapper objectMapper;

	@Transactional
	public void ingest(MetricType type, JsonNode payload) {
		String agitUuid = text(payload, "agitUuid");
		if (agitUuid == null) {
			log.warn("metric skip: agitUuid 없음 type={}", type);
			return;
		}
		Instant occurredAt = parseInstant(payload.get("occurredAt"));
		MetricEventEntity saved = metricEventRepository.save(
				MetricEventEntity.create(agitUuid, text(payload, "actorUserUuid"), type, occurredAt)
		);

		ObjectNode recorded = objectMapper.createObjectNode();
		recorded.put("eventId", saved.getId());
		recorded.put("agitUuid", agitUuid);
		recorded.put("metricType", type.name());
		recorded.put("actorUserUuid", saved.getActorUserUuid());
		recorded.put("occurredAt", occurredAt.toString());
		eventPublisherPort.publish(AnalyticsEventTopics.METRIC_RECORDED, agitUuid, recorded.toString());
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
