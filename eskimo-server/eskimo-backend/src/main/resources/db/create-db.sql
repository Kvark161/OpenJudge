CREATE TABLE users
(
  id       BIGINT AUTO_INCREMENT NOT NULL
    CONSTRAINT pk_users
    PRIMARY KEY,
  name     VARCHAR(128)          NOT NULL
    CONSTRAINT users_name_key UNIQUE,
  password VARCHAR(128)          NOT NULL,
  is_admin BOOLEAN               NOT NULL,
  locale   VARCHAR(128)          NOT NULL
);

CREATE TABLE submissions
(
  id                BIGINT AUTO_INCREMENT NOT NULL
    CONSTRAINT pk_submissions
    PRIMARY KEY,
  user_id           BIGINT                NOT NULL
    CONSTRAINT fk_submissions_users
    REFERENCES users,
  contest_id        BIGINT                NOT NULL,
  problem_id        BIGINT                NOT NULL,
  source_code       CLOB                  NOT NULL,
  verdict           CLOB                  NOT NULL,
  sending_date_time TIMESTAMP             NOT NULL,
  test_number       BIGINT
);

CREATE TABLE contests
(
  id                  BIGINT AUTO_INCREMENT NOT NULL
    CONSTRAINT pk_contests
    PRIMARY KEY,
  name                VARCHAR(128)          NOT NULL,
  start_time          TIMESTAMP WITH TIME ZONE,
  duration_in_minutes INTEGER
);

CREATE TABLE statements
(
  id         BIGINT AUTO_INCREMENT NOT NULL
    CONSTRAINT pk_statements
    PRIMARY KEY,
  contest_id BIGINT                NOT NULL
    CONSTRAINT fk_statements_contests
    REFERENCES contests,
  language   VARCHAR(128)          NOT NULL,
  file_path  CLOB                  NOT NULL
);

CREATE TABLE problems
(
  id                BIGINT AUTO_INCREMENT NOT NULL
    CONSTRAINT pk_problems
    PRIMARY KEY,
  contest_id        BIGINT                NOT NULL
    CONSTRAINT fk_problems_contests
    REFERENCES contests,
  number_in_contest INTEGER               NOT NULL,
  name              VARCHAR(128)          NOT NULL,
  time_limit        BIGINT                NOT NULL,
  memory_limit      BIGINT                NOT NULL,
  tests_count       BIGINT                NOT NULL
);


CREATE TABLE programming_languages
(
  id          BIGINT AUTO_INCREMENT NOT NULL
    CONSTRAINT pk_programming_languages
    PRIMARY KEY,
  name        VARCHAR(128)          NOT NULL
    CONSTRAINT programming_languages_name_key UNIQUE,
  description CLOB                  NOT NULL
);

