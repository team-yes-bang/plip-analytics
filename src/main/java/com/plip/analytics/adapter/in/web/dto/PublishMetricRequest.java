package com.plip.analytics.adapter.in.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PublishMetricRequest {

	private String type;
	private String agitUuid;
}
