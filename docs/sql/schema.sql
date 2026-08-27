CREATE TABLE IF NOT EXISTS agit_catalog (
  agit_uuid       VARCHAR(36)  NOT NULL PRIMARY KEY,
  agit_name       VARCHAR(40)  NOT NULL,
  description     VARCHAR(200) NULL,
  thumbnail_path  VARCHAR(255) NULL,
  status          VARCHAR(20)  NOT NULL,
  created_at      DATETIME(6)  NOT NULL
);

CREATE TABLE IF NOT EXISTS metric_events (
  id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
  agit_uuid       VARCHAR(36)  NOT NULL,
  actor_user_uuid VARCHAR(36)  NULL,
  metric_type     VARCHAR(40)  NOT NULL,
  occurred_at     DATETIME(6)  NOT NULL,
  INDEX idx_metric_agit_type_occurred (agit_uuid, metric_type, occurred_at)
);

CREATE TABLE IF NOT EXISTS agit_daily_stats (
  id           BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
  stat_date    DATE        NOT NULL,
  agit_uuid    VARCHAR(36) NOT NULL,
  impressions  BIGINT      NOT NULL DEFAULT 0,
  clicks       BIGINT      NOT NULL DEFAULT 0,
  views        BIGINT      NOT NULL DEFAULT 0,
  joins        BIGINT      NOT NULL DEFAULT 0,
  leaves       BIGINT      NOT NULL DEFAULT 0,
  UNIQUE KEY uq_agit_daily_stats (stat_date, agit_uuid)
);

CREATE TABLE IF NOT EXISTS agit_rank_current (
  id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
  rank_type  VARCHAR(20)  NOT NULL,
  agit_uuid  VARCHAR(36)  NOT NULL,
  score      DOUBLE       NOT NULL,
  rank_no    INT          NOT NULL,
  UNIQUE KEY uq_agit_rank_current (rank_type, agit_uuid)
);
