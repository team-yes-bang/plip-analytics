package com.plip.analytics.application.port.out;

public interface EventPublisherPort {

	void publish(String topic, String key, String payload);
}
