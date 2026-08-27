package com.plip.analytics.application.port.in.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgitSearchPageDto {

	private final List<AgitSearchItemDto> items;
	private final int page;
	private final int size;
	private final long total;
}
