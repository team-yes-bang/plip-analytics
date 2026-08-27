package com.plip.analytics.adapter.in.web;

import com.plip.analytics.adapter.in.web.dto.AgitSearchItemResponse;
import com.plip.analytics.adapter.in.web.dto.AgitSearchPageResponse;
import com.plip.analytics.application.port.in.SearchAgitsUseCase;
import com.plip.analytics.application.port.in.dto.AgitSearchPageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Analytics", description = "아지트 검색·랭킹")
@RestController
@RequestMapping("/api/v1/agits")
@RequiredArgsConstructor
public class AgitSearchController {

	private final SearchAgitsUseCase searchAgitsUseCase;

	@Operation(summary = "아지트 검색", description = "sort=new|popular|rising. 정산·집계는 Kafka EDA로 비동기 처리됩니다.")
	@GetMapping("/search")
	public AgitSearchPageResponse search(
			@RequestParam(required = false) String q,
			@RequestParam(defaultValue = "new") String sort,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size
	) {
		AuthenticatedActor.requireUserUuid();
		AgitSearchPageDto result = searchAgitsUseCase.search(q, sort, page, size);
		return AgitSearchPageResponse.builder()
				.items(result.getItems().stream()
						.map(item -> AgitSearchItemResponse.builder()
								.agitUuid(item.getAgitUuid())
								.agitName(item.getAgitName())
								.description(item.getDescription())
								.thumbnailPath(item.getThumbnailPath())
								.createdAt(item.getCreatedAt())
								.score(item.getScore())
								.build())
						.toList())
				.page(result.getPage())
				.size(result.getSize())
				.total(result.getTotal())
				.build();
	}
}
