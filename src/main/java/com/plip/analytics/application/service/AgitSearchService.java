package com.plip.analytics.application.service;

import com.plip.analytics.adapter.out.persistence.AgitCatalogEntity;
import com.plip.analytics.adapter.out.persistence.AgitCatalogRepository;
import com.plip.analytics.adapter.out.persistence.AgitRankCurrentEntity;
import com.plip.analytics.adapter.out.persistence.AgitRankCurrentRepository;
import com.plip.analytics.adapter.out.persistence.RankType;
import com.plip.analytics.application.port.in.SearchAgitsUseCase;
import com.plip.analytics.application.port.in.dto.AgitSearchItemDto;
import com.plip.analytics.application.port.in.dto.AgitSearchPageDto;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgitSearchService implements SearchAgitsUseCase {

	private static final String ACTIVE = "ACTIVE";

	private final AgitCatalogRepository catalogRepository;
	private final AgitRankCurrentRepository rankCurrentRepository;

	@Override
	public AgitSearchPageDto search(String query, String sort, int page, int size) {
		int safePage = Math.max(page, 0);
		int safeSize = Math.min(Math.max(size, 1), 50);
		String keyword = query == null ? "" : query.trim();
		String normalizedSort = sort == null ? "new" : sort.trim().toLowerCase(Locale.ROOT);

		if ("popular".equals(normalizedSort)) {
			return searchByRank(RankType.POPULAR, keyword, safePage, safeSize);
		}
		if ("rising".equals(normalizedSort)) {
			return searchByRank(RankType.RISING, keyword, safePage, safeSize);
		}
		return searchNew(keyword, safePage, safeSize);
	}

	private AgitSearchPageDto searchNew(String keyword, int page, int size) {
		PageRequest pageable = PageRequest.of(page, size);
		Page<AgitCatalogEntity> result = keyword.isBlank()
				? catalogRepository.findByStatusOrderByCreatedAtDesc(ACTIVE, pageable)
				: catalogRepository.findByStatusAndAgitNameContainingIgnoreCaseOrderByCreatedAtDesc(
						ACTIVE, keyword, pageable);
		return AgitSearchPageDto.builder()
				.items(result.getContent().stream().map(this::toItem).toList())
				.page(page)
				.size(size)
				.total(result.getTotalElements())
				.build();
	}

	private AgitSearchPageDto searchByRank(RankType rankType, String keyword, int page, int size) {
		Page<AgitRankCurrentEntity> ranks =
				rankCurrentRepository.findByRankTypeOrderByRankNoAsc(rankType, PageRequest.of(page, size));
		List<String> uuids = ranks.getContent().stream().map(AgitRankCurrentEntity::getAgitUuid).toList();
		Map<String, AgitCatalogEntity> catalog = catalogRepository.findByStatusAndAgitUuidIn(ACTIVE, uuids)
				.stream()
				.collect(Collectors.toMap(AgitCatalogEntity::getAgitUuid, Function.identity()));
		String lowered = keyword.toLowerCase(Locale.ROOT);
		List<AgitSearchItemDto> items = ranks.getContent().stream()
				.map(rank -> {
					AgitCatalogEntity entity = catalog.get(rank.getAgitUuid());
					if (entity == null) {
						return null;
					}
					if (!keyword.isBlank() && !entity.getAgitName().toLowerCase(Locale.ROOT).contains(lowered)) {
						return null;
					}
					return toItem(entity, rank.getScore());
				})
				.filter(item -> item != null)
				.toList();
		return AgitSearchPageDto.builder()
				.items(items)
				.page(page)
				.size(size)
				.total(ranks.getTotalElements())
				.build();
	}

	private AgitSearchItemDto toItem(AgitCatalogEntity entity) {
		return toItem(entity, null);
	}

	private AgitSearchItemDto toItem(AgitCatalogEntity entity, Double score) {
		return AgitSearchItemDto.builder()
				.agitUuid(entity.getAgitUuid())
				.agitName(entity.getAgitName())
				.description(entity.getDescription())
				.thumbnailPath(entity.getThumbnailPath())
				.createdAt(entity.getCreatedAt())
				.score(score)
				.build();
	}
}
