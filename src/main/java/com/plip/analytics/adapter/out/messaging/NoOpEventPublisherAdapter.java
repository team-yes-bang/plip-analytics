package com.plip.analytics.adapter.out.messaging;

import com.plip.analytics.application.port.out.EventPublisherPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class NoOpEventPublisherAdapter implements EventPublisherPort {

	@Override
	public void publish(String topic, String key, String payload) {
		// test profile: Kafka 없음
	}
}
