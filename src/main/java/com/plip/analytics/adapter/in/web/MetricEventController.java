package com.plip.analytics.adapter.in.web;

import com.plip.analytics.adapter.in.web.dto.PublishMetricRequest;
import com.plip.analytics.application.port.in.PublishMetricUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Analytics", description = "검색·조회 이벤트 Kafka 발행")
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class MetricEventController {

	private final PublishMetricUseCase publishMetricUseCase;

	@Operation(summary = "검색/조회 이벤트 발행", description = "DB에 쌓지 않고 Kafka 토픽만 produce합니다.")
	@PostMapping
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void publish(@RequestBody PublishMetricRequest request) {
		publishMetricUseCase.publish(
				request.getType(),
				request.getAgitUuid(),
				AuthenticatedActor.requireUserUuid()
		);
	}
}
