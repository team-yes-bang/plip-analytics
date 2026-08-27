package com.plip.analytics.application.port.in;

import java.util.UUID;

public interface PublishMetricUseCase {

	void publish(String type, String agitUuid, UUID actorUserUuid);
}
