-- MockDraft schema v1. Portable between MariaDB/MySQL and H2 (MODE=MariaDB).
-- /*!40101 ... */ blocks are executed by MariaDB/MySQL only and ignored as comments elsewhere.

CREATE TABLE IF NOT EXISTS users (
    username       VARCHAR(50)  NOT NULL,
    email          VARCHAR(254) NULL,
    password_hash  VARCHAR(100) NOT NULL,
    legacy_salt    VARCHAR(64)  NULL,
    created_at     DATETIME     NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (username),
    CONSTRAINT uq_users_email UNIQUE (email)
) /*!40101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci */;

CREATE TABLE IF NOT EXISTS sessions (
    token_hash  CHAR(64)    NOT NULL,
    username    VARCHAR(50) NOT NULL,
    created_at  DATETIME    NOT NULL,
    expires_at  DATETIME    NOT NULL,
    CONSTRAINT pk_sessions PRIMARY KEY (token_hash),
    CONSTRAINT fk_sessions_user FOREIGN KEY (username) REFERENCES users (username)
        ON DELETE CASCADE ON UPDATE CASCADE
) /*!40101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci */;

CREATE TABLE IF NOT EXISTS password_resets (
    token_hash  CHAR(64)    NOT NULL,
    username    VARCHAR(50) NOT NULL,
    expires_at  DATETIME    NOT NULL,
    CONSTRAINT pk_password_resets PRIMARY KEY (token_hash),
    CONSTRAINT fk_password_resets_user FOREIGN KEY (username) REFERENCES users (username)
        ON DELETE CASCADE ON UPDATE CASCADE
) /*!40101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci */;

CREATE TABLE IF NOT EXISTS players (
    player_rank       INT           NOT NULL,
    name              VARCHAR(100)  NOT NULL,
    position          VARCHAR(3)    NOT NULL,
    projected_points  DECIMAL(6, 2) NOT NULL,
    CONSTRAINT pk_players PRIMARY KEY (player_rank),
    CONSTRAINT ck_players_position CHECK (position IN ('QB', 'RB', 'WR', 'TE', 'K', 'DST'))
) /*!40101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci */;

CREATE TABLE IF NOT EXISTS drafts (
    draft_id      BIGINT      NOT NULL AUTO_INCREMENT,
    owner         VARCHAR(50) NOT NULL,
    team_name     VARCHAR(50) NOT NULL,
    num_teams     INT         NOT NULL,
    rounds        INT         NOT NULL,
    user_slot     INT         NOT NULL,
    status        VARCHAR(12) NOT NULL,
    created_at    DATETIME    NOT NULL,
    completed_at  DATETIME    NULL,
    CONSTRAINT pk_drafts PRIMARY KEY (draft_id),
    CONSTRAINT fk_drafts_owner FOREIGN KEY (owner) REFERENCES users (username)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT ck_drafts_num_teams CHECK (num_teams BETWEEN 2 AND 20),
    CONSTRAINT ck_drafts_rounds CHECK (rounds BETWEEN 1 AND 30),
    CONSTRAINT ck_drafts_user_slot CHECK (user_slot BETWEEN 1 AND num_teams),
    CONSTRAINT ck_drafts_status CHECK (status IN ('IN_PROGRESS', 'COMPLETE')),
    KEY idx_drafts_owner_status (owner, status)
) /*!40101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci */;

CREATE TABLE IF NOT EXISTS draft_teams (
    draft_id  BIGINT      NOT NULL,
    slot      INT         NOT NULL,
    name      VARCHAR(50) NOT NULL,
    is_user   BOOLEAN     NOT NULL,
    CONSTRAINT pk_draft_teams PRIMARY KEY (draft_id, slot),
    CONSTRAINT fk_draft_teams_draft FOREIGN KEY (draft_id) REFERENCES drafts (draft_id) ON DELETE CASCADE
) /*!40101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci */;

CREATE TABLE IF NOT EXISTS draft_picks (
    draft_id     BIGINT   NOT NULL,
    overall      INT      NOT NULL,
    slot         INT      NOT NULL,
    player_rank  INT      NOT NULL,
    picked_at    DATETIME NOT NULL,
    CONSTRAINT pk_draft_picks PRIMARY KEY (draft_id, overall),
    CONSTRAINT uq_draft_picks_player UNIQUE (draft_id, player_rank),
    CONSTRAINT fk_draft_picks_draft FOREIGN KEY (draft_id) REFERENCES drafts (draft_id) ON DELETE CASCADE,
    CONSTRAINT fk_draft_picks_player FOREIGN KEY (player_rank) REFERENCES players (player_rank)
) /*!40101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci */;
