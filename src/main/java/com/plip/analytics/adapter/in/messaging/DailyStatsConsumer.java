package com.plip.analytics.adapter.in.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plip.analytics.application.port.out.AnalyticsEventTopics;
import com.plip.analytics.application.service.DailyStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DailyStatsConsumer {

	private final ObjectMapper objectMapper;
	private final DailyStatsService dailyStatsService;

	@KafkaListener(topics = AnalyticsEventTopics.METRIC_RECORDED, groupId = "analytics-daily-stats")
	public void consume(String payload) {
		try {
			JsonNode node = objectMapper.readTree(payload);
			dailyStatsService.applyRecordedMetric(node);
		} catch (Exception e) {
			log.warn("daily stats 반영 실패: {}", e.getMessage());
		}
	}
}
