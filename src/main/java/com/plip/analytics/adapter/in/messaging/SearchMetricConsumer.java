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
public class SearchMetricConsumer {

	private final ObjectMapper objectMapper;
	private final MetricIngestService metricIngestService;

	@KafkaListener(
			topics = {
					AnalyticsEventTopics.SEARCH_IMPRESSION,
					AnalyticsEventTopics.SEARCH_CLICK,
					AnalyticsEventTopics.DETAIL_VIEW
			},
			groupId = "analytics-metric-ingest"
	)
	public void consume(String payload) {
		try {
			JsonNode node = objectMapper.readTree(payload);
			MetricType type = MetricType.valueOf(node.path("metricType").asText());
			metricIngestService.ingest(type, node);
		} catch (Exception e) {
			log.warn("search metric ingest 실패: {}", e.getMessage());
		}
	}
}
