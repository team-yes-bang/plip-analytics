package com.plip.analytics.adapter.in.messaging;

import com.plip.analytics.application.port.out.AnalyticsEventTopics;
import com.plip.analytics.application.service.RankRefreshService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class RankRefreshConsumer {

	private final RankRefreshService rankRefreshService;

	@KafkaListener(topics = AnalyticsEventTopics.STATS_UPDATED, groupId = "analytics-rank")
	public void consume(String payload) {
		try {
			rankRefreshService.refreshAll();
		} catch (Exception e) {
			log.warn("rank refresh 실패: {}", e.getMessage());
		}
	}
}
