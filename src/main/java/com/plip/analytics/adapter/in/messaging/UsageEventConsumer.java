package com.plip.analytics.adapter.in.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plip.analytics.adapter.out.persistence.MetricType;
import com.plip.analytics.application.port.out.AnalyticsEventTopics;
import com.plip.analytics.application.service.MetricIngestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class UsageEventConsumer {

	private final ObjectMapper objectMapper;
	private final MetricIngestService metricIngestService;

	@KafkaListener(topics = AnalyticsEventTopics.AGIT_MEMBER_JOINED, groupId = "analytics-usage")
	public void consumeJoined(String payload) {
		ingest(payload, MetricType.JOIN_APPROVED);
	}

	@KafkaListener(topics = AnalyticsEventTopics.AGIT_MEMBER_LEFT, groupId = "analytics-usage")
	public void consumeLeft(String payload) {
		ingest(payload, MetricType.LEAVE);
	}

	private void ingest(String payload, MetricType type) {
		try {
			JsonNode node = objectMapper.readTree(payload);
			metricIngestService.ingest(type, node);
		} catch (Exception e) {
			log.warn("usage metric ingest 실패 type={}: {}", type, e.getMessage());
		}
	}
}
