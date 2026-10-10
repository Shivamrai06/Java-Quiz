-- =====================================================================
--  QuizDesk :: schema.sql
--  Java-Based Online Quiz Platform - MySQL 8 schema
--  Run this FIRST, then run seed.sql
--  Command:  mysql -u root -p < sql/schema.sql
-- =====================================================================

DROP DATABASE IF EXISTS quiz_platform;
CREATE DATABASE quiz_platform
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE quiz_platform;

-- ---------------------------------------------------------------------
-- 1. users
-- ---------------------------------------------------------------------
CREATE TABLE users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,           -- BCrypt hash, never plain text
    role          ENUM('STUDENT','ADMIN') NOT NULL DEFAULT 'STUDENT',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. quizzes
-- ---------------------------------------------------------------------
CREATE TABLE quizzes (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    title        VARCHAR(150) NOT NULL,
    description  TEXT         NULL,
    category     VARCHAR(100) NULL,
    duration_min INT          NULL,
    status       ENUM('DRAFT','PUBLISHED') NOT NULL DEFAULT 'DRAFT',
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_quizzes_status (status)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. questions
-- ---------------------------------------------------------------------
CREATE TABLE questions (
    id             BIGINT    NOT NULL AUTO_INCREMENT,
    quiz_id        BIGINT    NOT NULL,
    question_text  TEXT      NOT NULL,
    correct_option CHAR(1)   NOT NULL,
    explanation    TEXT      NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_questions_quiz (quiz_id),
    CONSTRAINT fk_questions_quiz
        FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE,
    CONSTRAINT chk_questions_correct_option
        CHECK (correct_option IN ('A','B','C','D'))
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. question_options  (normalized - not four columns inside questions)
-- ---------------------------------------------------------------------
CREATE TABLE question_options (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    question_id BIGINT       NOT NULL,
    option_key  CHAR(1)      NOT NULL,
    option_text VARCHAR(500) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_option_per_question (question_id, option_key),
    KEY idx_options_question (question_id),
    CONSTRAINT fk_options_question
        FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE,
    CONSTRAINT chk_options_key
        CHECK (option_key IN ('A','B','C','D'))
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. attempts
-- ---------------------------------------------------------------------
CREATE TABLE attempts (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    quiz_id         BIGINT       NOT NULL,
    score           INT          NOT NULL DEFAULT 0,
    total_questions INT          NOT NULL DEFAULT 0,
    percentage      DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    started_at      TIMESTAMP    NULL,
    submitted_at    TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_attempts_user (user_id),
    KEY idx_attempts_quiz (quiz_id),
    CONSTRAINT fk_attempts_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_attempts_quiz
        FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 6. answers  (one row per question of an attempt)
-- ---------------------------------------------------------------------
CREATE TABLE answers (
    id              BIGINT  NOT NULL AUTO_INCREMENT,
    attempt_id      BIGINT  NOT NULL,
    question_id     BIGINT  NOT NULL,
    selected_option CHAR(1) NULL,        -- NULL = question left unanswered
    is_correct      BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uq_answer_per_attempt (attempt_id, question_id),
    KEY idx_answers_attempt (attempt_id),
    CONSTRAINT fk_answers_attempt
        FOREIGN KEY (attempt_id) REFERENCES attempts (id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_question
        FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Verification
-- ---------------------------------------------------------------------
SELECT 'quiz_platform schema created' AS status;
SHOW TABLES;
