package com.plip.analytics.adapter.in.web.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgitSearchItemResponse {

	private String agitUuid;
	private String agitName;
	private String description;
	private String thumbnailPath;
	private Instant createdAt;
	private Double score;
}
