package com.plip.analytics.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.plip.analytics.adapter.out.persistence.AgitCatalogEntity;
import com.plip.analytics.adapter.out.persistence.AgitCatalogRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgitCatalogService {

	private final AgitCatalogRepository catalogRepository;

	@Transactional
	public void upsert(JsonNode payload) {
		String agitUuid = text(payload, "agitUuid");
		if (agitUuid == null) {
			log.warn("catalog skip: agitUuid 없음");
			return;
		}
		AgitCatalogEntity entity = catalogRepository.findById(agitUuid)
				.orElseGet(() -> AgitCatalogEntity.create(
						agitUuid,
						textOrDefault(payload, "agitName", "아지트"),
						text(payload, "description"),
						text(payload, "thumbnailPath"),
						"ACTIVE",
						parseInstant(payload.get("occurredAt"))
				));
		if (payload.hasNonNull("agitName")) {
			entity.setAgitName(payload.get("agitName").asText());
		}
		if (payload.has("description")) {
			entity.setDescription(text(payload, "description"));
		}
		if (payload.has("thumbnailPath")) {
			entity.setThumbnailPath(text(payload, "thumbnailPath"));
		}
		entity.setStatus("ACTIVE");
		catalogRepository.save(entity);
	}

	@Transactional
	public void markDeleted(JsonNode payload) {
		String agitUuid = text(payload, "agitUuid");
		if (agitUuid == null) {
			return;
		}
		catalogRepository.findById(agitUuid).ifPresent(entity -> {
			entity.setStatus("DELETED");
			catalogRepository.save(entity);
		});
	}

	private static String text(JsonNode node, String field) {
		JsonNode value = node.get(field);
		if (value == null || value.isNull() || value.asText().isBlank()) {
			return null;
		}
		return value.asText();
	}

	private static String textOrDefault(JsonNode node, String field, String fallback) {
		String value = text(node, field);
		return value == null ? fallback : value;
	}

	private static Instant parseInstant(JsonNode node) {
		if (node == null || node.isNull() || node.asText().isBlank()) {
			return Instant.now();
		}
		try {
			return Instant.parse(node.asText());
		} catch (Exception ignored) {
			return Instant.now();
		}
	}
}
