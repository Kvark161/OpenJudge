CREATE TABLE USERS
(
  ID       BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  NAME     VARCHAR(128)                      NOT NULL,
  PASSWORD VARCHAR(128)                      NOT NULL,
  LOCALE   VARCHAR(128)                      NOT NULL,
  ROLE     VARCHAR(128)                      NOT NULL
);

CREATE TABLE SUBMISSIONS
(
  ID                BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  USER_ID           BIGINT                            NOT NULL,
  CONTEST_ID        BIGINT                            NOT NULL,
  PROBLEM_ID        BIGINT                            NOT NULL,
  SOURCE_CODE       CLOB                              NOT NULL,
  VERDICT           CLOB                              NOT NULL,
  SENDING_DATE_TIME TIMESTAMP                         NOT NULL,
  TEST_NUMBER       BIGINT,
  CONSTRAINT FK_SUBMISSIONS_USERS FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);

CREATE TABLE CONTESTS
(
  ID                  BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  NAME                VARCHAR(128)                      NOT NULL,
  START_TIME          TIMESTAMP WITH TIME ZONE,
  DURATION_IN_MINUTES INTEGER
);

CREATE TABLE STATEMENTS
(
  ID         BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  CONTEST_ID BIGINT                            NOT NULL,
  LANGUAGE   VARCHAR(128)                      NOT NULL,
  FILE_NAME  CLOB                              NOT NULL,
  CONSTRAINT FK_STATEMENTS_CONTESTS FOREIGN KEY (CONTEST_ID) REFERENCES CONTESTS (ID)
);

CREATE TABLE PROBLEMS
(
  ID            BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  CONTEST_ID    BIGINT                            NOT NULL,
  CONTEST_INDEX INTEGER                           NOT NULL,
  NAME          VARCHAR(128)                      NOT NULL,
  TIME_LIMIT    BIGINT                            NOT NULL,
  MEMORY_LIMIT  BIGINT                            NOT NULL,
  TESTS_COUNT   BIGINT                            NOT NULL,
  CONSTRAINT FK_PROBLEMS_CONTESTS FOREIGN KEY (CONTEST_ID) REFERENCES CONTESTS (ID)
);

CREATE TABLE PROGRAMMING_LANGUAGES
(
  ID          BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  NAME        VARCHAR(128) UNIQUE               NOT NULL,
  DESCRIPTION CLOB                              NOT NULL
);

CREATE TABLE USER_SESSIONS
(
  ID                BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  USER_ID           BIGINT                            NOT NULL,
  TOKEN             VARCHAR(128)                      NOT NULL,
  USER_AGENT        VARCHAR(256)                      NOT NULL,
  IP                VARCHAR(128)                      NOT NULL,
  LAST_REQUEST_TIME TIMESTAMP                         NOT NULL,
  CONSTRAINT AUTHORISATION_USERS_ID_FK FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);

INSERT INTO USERS
(name, password, locale, role)
VALUES
  ('admin', 'admin', 'ru', 'ADMIN'),
  ('user', 'user', 'ru', 'USER');