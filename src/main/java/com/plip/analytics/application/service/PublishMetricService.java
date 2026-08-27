package com.plip.analytics.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.plip.analytics.adapter.out.persistence.MetricType;
import com.plip.analytics.application.port.in.PublishMetricUseCase;
import com.plip.analytics.application.port.out.AnalyticsEventTopics;
import com.plip.analytics.application.port.out.EventPublisherPort;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublishMetricService implements PublishMetricUseCase {

	private final EventPublisherPort eventPublisherPort;
	private final ObjectMapper objectMapper;

	@Override
	public void publish(String type, String agitUuid, UUID actorUserUuid) {
		if (agitUuid == null || agitUuid.isBlank()) {
			throw new IllegalArgumentException("agitUuid는 필수입니다.");
		}
		MetricType metricType = parseType(type);
		ObjectNode payload = objectMapper.createObjectNode();
		payload.put("agitUuid", agitUuid);
		payload.put("actorUserUuid", actorUserUuid.toString());
		payload.put("metricType", metricType.name());
		payload.put("occurredAt", Instant.now().toString());
		eventPublisherPort.publish(topicOf(metricType), agitUuid, payload.toString());
	}

	private static MetricType parseType(String type) {
		if (type == null || type.isBlank()) {
			throw new IllegalArgumentException("event type은 필수입니다.");
		}
		try {
			return MetricType.valueOf(type.trim().toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("지원하지 않는 event type입니다.");
		}
	}

	private static String topicOf(MetricType type) {
		return switch (type) {
			case SEARCH_IMPRESSION -> AnalyticsEventTopics.SEARCH_IMPRESSION;
			case SEARCH_CLICK -> AnalyticsEventTopics.SEARCH_CLICK;
			case DETAIL_VIEW -> AnalyticsEventTopics.DETAIL_VIEW;
			case JOIN_APPROVED, LEAVE -> throw new IllegalArgumentException("사용 지표는 agit Kafka에서만 수집합니다.");
		};
	}
}
