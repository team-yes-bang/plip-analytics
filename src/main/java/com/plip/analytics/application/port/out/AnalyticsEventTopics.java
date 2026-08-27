package com.plip.analytics.application.port.out;

public final class AnalyticsEventTopics {

	public static final String SEARCH_IMPRESSION = "analytics.search-impression";
	public static final String SEARCH_CLICK = "analytics.search-click";
	public static final String DETAIL_VIEW = "analytics.detail-view";
	public static final String METRIC_RECORDED = "analytics.metric-recorded";
	public static final String STATS_UPDATED = "analytics.stats-updated";

	public static final String AGIT_CREATED = "agit.created";
	public static final String AGIT_UPDATED = "agit.updated";
	public static final String AGIT_DELETED = "agit.deleted";
	public static final String AGIT_MEMBER_JOINED = "agit.member-joined";
	public static final String AGIT_MEMBER_LEFT = "agit.member-left";

	private AnalyticsEventTopics() {
	}
}
