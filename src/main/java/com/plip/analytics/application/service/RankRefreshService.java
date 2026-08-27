package com.plip.analytics.application.service;

import com.plip.analytics.adapter.out.persistence.AgitDailyStatsEntity;
import com.plip.analytics.adapter.out.persistence.AgitDailyStatsRepository;
import com.plip.analytics.adapter.out.persistence.AgitRankCurrentEntity;
import com.plip.analytics.adapter.out.persistence.AgitRankCurrentRepository;
import com.plip.analytics.adapter.out.persistence.MetricEventRepository;
import com.plip.analytics.adapter.out.persistence.MetricType;
import com.plip.analytics.adapter.out.persistence.RankType;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RankRefreshService {

	private static final double RISING_MIN_CURRENT = 2.0;

	private final AgitDailyStatsRepository dailyStatsRepository;
	private final AgitRankCurrentRepository rankCurrentRepository;
	private final MetricEventRepository metricEventRepository;

	@Transactional
	public void refreshAll() {
		refreshPopular();
		refreshRising();
	}

	private void refreshPopular() {
		LocalDate today = LocalDate.now(ZoneOffset.UTC);
		List<AgitDailyStatsEntity> window = dailyStatsRepository.findByStatDateBetween(today.minusDays(6), today);
		Map<String, Double> totals = new HashMap<>();
		for (AgitDailyStatsEntity row : window) {
			totals.merge(row.getAgitUuid(), row.popularScore(), Double::sum);
		}
		replaceRanks(RankType.POPULAR, totals.entrySet().stream()
				.filter(entry -> entry.getValue() > 0)
				.sorted(Map.Entry.<String, Double>comparingByValue().reversed())
				.toList());
	}

	private void refreshRising() {
		Instant now = Instant.now();
		Instant currentFrom = now.minus(24, ChronoUnit.HOURS);
		Instant previousFrom = now.minus(48, ChronoUnit.HOURS);
		Map<String, Double> current = scoreByAgit(previousFrom, currentFrom, currentFrom, now);
		replaceRanks(RankType.RISING, current.entrySet().stream()
				.filter(entry -> entry.getValue() >= RISING_MIN_CURRENT)
				.sorted(Map.Entry.<String, Double>comparingByValue().reversed())
				.toList());
	}

	private Map<String, Double> scoreByAgit(Instant prevFrom, Instant prevTo, Instant curFrom, Instant curTo) {
		Map<String, EnumMap<MetricType, Long>> previous = group(metricEventRepository.countByWindow(prevFrom, prevTo));
		Map<String, EnumMap<MetricType, Long>> current = group(metricEventRepository.countByWindow(curFrom, curTo));
		Map<String, Double> scores = new HashMap<>();
		for (String agitUuid : current.keySet()) {
			double currentScore = score(current.get(agitUuid));
			double previousScore = score(previous.getOrDefault(agitUuid, new EnumMap<>(MetricType.class)));
			scores.put(agitUuid, currentScore / Math.max(previousScore, 1.0));
		}
		return scores;
	}

	private static Map<String, EnumMap<MetricType, Long>> group(
			List<MetricEventRepository.MetricWindowCount> rows
	) {
		Map<String, EnumMap<MetricType, Long>> grouped = new HashMap<>();
		for (MetricEventRepository.MetricWindowCount row : rows) {
			grouped.computeIfAbsent(row.getAgitUuid(), key -> new EnumMap<>(MetricType.class))
					.put(row.getMetricType(), row.getCnt());
		}
		return grouped;
	}

	private static double score(EnumMap<MetricType, Long> counts) {
		long views = counts.getOrDefault(MetricType.DETAIL_VIEW, 0L);
		long clicks = counts.getOrDefault(MetricType.SEARCH_CLICK, 0L);
		long joins = counts.getOrDefault(MetricType.JOIN_APPROVED, 0L);
		long impressions = counts.getOrDefault(MetricType.SEARCH_IMPRESSION, 0L);
		return 0.4 * views + 0.3 * clicks + 0.2 * joins + 0.1 * impressions;
	}

	private void replaceRanks(RankType type, List<Map.Entry<String, Double>> ordered) {
		rankCurrentRepository.deleteByRankType(type);
		rankCurrentRepository.flush();
		int rank = 1;
		for (Map.Entry<String, Double> entry : ordered) {
			rankCurrentRepository.save(
					AgitRankCurrentEntity.of(type, entry.getKey(), entry.getValue(), rank++)
			);
		}
	}
}
