package com.plip.analytics.application.port.in.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgitSearchItemDto {

	private final String agitUuid;
	private final String agitName;
	private final String description;
	private final String thumbnailPath;
	private final Instant createdAt;
	private final Double score;
}
