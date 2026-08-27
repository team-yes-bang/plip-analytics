package com.plip.analytics.adapter.in.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plip.analytics.application.port.out.AnalyticsEventTopics;
import com.plip.analytics.application.service.AgitCatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class AgitCatalogConsumer {

	private final ObjectMapper objectMapper;
	private final AgitCatalogService agitCatalogService;

	@KafkaListener(
			topics = {AnalyticsEventTopics.AGIT_CREATED, AnalyticsEventTopics.AGIT_UPDATED},
			groupId = "analytics-catalog"
	)
	public void consumeUpsert(String payload) {
		try {
			JsonNode node = objectMapper.readTree(payload);
			agitCatalogService.upsert(node);
		} catch (Exception e) {
			log.warn("catalog upsert 실패: {}", e.getMessage());
		}
	}

	@KafkaListener(topics = AnalyticsEventTopics.AGIT_DELETED, groupId = "analytics-catalog")
	public void consumeDeleted(String payload) {
		try {
			JsonNode node = objectMapper.readTree(payload);
			agitCatalogService.markDeleted(node);
		} catch (Exception e) {
			log.warn("catalog delete 실패: {}", e.getMessage());
		}
	}
}
