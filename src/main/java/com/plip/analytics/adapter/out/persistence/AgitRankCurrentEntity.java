package com.plip.analytics.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
		name = "agit_rank_current",
		uniqueConstraints = @UniqueConstraint(
				name = "uq_agit_rank_current",
				columnNames = {"rank_type", "agit_uuid"}
		)
)
public class AgitRankCurrentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "rank_type", nullable = false, length = 20)
	private RankType rankType;

	@Column(name = "agit_uuid", nullable = false, length = 36)
	private String agitUuid;

	@Column(name = "score", nullable = false)
	private double score;

	@Column(name = "rank_no", nullable = false)
	private int rankNo;

	public static AgitRankCurrentEntity of(RankType rankType, String agitUuid, double score, int rankNo) {
		AgitRankCurrentEntity entity = new AgitRankCurrentEntity();
		entity.rankType = rankType;
		entity.agitUuid = agitUuid;
		entity.score = score;
		entity.rankNo = rankNo;
		return entity;
	}
}
