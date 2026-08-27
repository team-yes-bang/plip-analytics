package com.plip.analytics.adapter.in.web.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgitSearchPageResponse {

	private List<AgitSearchItemResponse> items;
	private int page;
	private int size;
	private long total;
}
