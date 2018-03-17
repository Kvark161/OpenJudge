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
  STATUS            VARCHAR(64)                       NOT NULL,
  SENDING_DATE_TIME TIMESTAMP                         NOT NULL,
  USED_TIME         BIGINT                            NOT NULL,
  USED_MEMORY       BIGINT                            NOT NULL,
  RESULT_DATA       CLOB                              NOT NULL,
  NUMBER_TESTS      BIGINT                            NOT NULL,
  PASSED_TESTS      BIGINT                            NOT NULL,
  CONSTRAINT FK_SUBMISSIONS_USERS FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);

CREATE TABLE CONTESTS
(
  ID                  BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  NAME                VARCHAR(128)                      NOT NULL,
  START_TIME          TIMESTAMP WITH TIME ZONE,
  DURATION_IN_MINUTES INTEGER
);

CREATE TABLE PROBLEMS
(
  ID                         BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  CONTEST_ID                 BIGINT                            NOT NULL,
  CONTEST_INDEX              INTEGER                           NOT NULL,
  TIME_LIMIT                 BIGINT                            NOT NULL,
  MEMORY_LIMIT               BIGINT                            NOT NULL,
  TESTS_COUNT                BIGINT                            NOT NULL,
  ANSWERS_GENERATION_STATUS  VARCHAR(64)                       NOT NULL,
  ANSWERS_GENERATION_MESSAGE VARCHAR(4096)                     NOT NULL,
  CHECKER_COMPILATION_STATUS  VARCHAR(64)                       NOT NULL,
  CHECKER_COMPILATION_MESSAGE VARCHAR(4096)                     NOT NULL,
  CONSTRAINT FK_PROBLEMS_CONTESTS FOREIGN KEY (CONTEST_ID) REFERENCES CONTESTS (ID)
);

CREATE TABLE PROGRAMMING_LANGUAGES
(
  ID                       BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  NAME                     VARCHAR(128) UNIQUE               NOT NULL,
  DESCRIPTION              VARCHAR(256)                      NOT NULL,
  COMPILER_PATH            VARCHAR(4096),
  IS_COMPILED              BOOLEAN                           NOT NULL,
  INTERPRETER_PATH         VARCHAR(4096),
  EXTENSION                VARCHAR(10)                       NOT NULL,
  BINARY_EXTENSION         VARCHAR(10)                       NOT NULL,
  COMPILE_COMMAND          VARCHAR(4096),
  RUN_COMMAND              VARCHAR(4096)                     NOT NULL,
  COMPILATION_MEMORY_LIMIT BIGINT                            NOT NULL,
  COMPILATION_TIME_LIMIT   BIGINT                            NOT NULL,
  TIME_LIMIT               BIGINT                            NOT NULL,
  MEMORY_LIMIT             BIGINT                            NOT NULL
);

CREATE TABLE STATEMENTS
(
  ID          BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  PROBLEM_ID  BIGINT                            NOT NULL,
  LANGUAGE    VARCHAR(128)                      NOT NULL,
  INPUT_FILE  VARCHAR(128)                      NOT NULL,
  OUTPUT_FILE VARCHAR(128)                      NOT NULL,
  NAME        VARCHAR(128)                      NOT NULL,
  LEGEND      CLOB                              NOT NULL,
  INPUT       CLOB                              NOT NULL,
  OUTPUT      CLOB                              NOT NULL,
  SAMPLES     CLOB                              NOT NULL,
  NOTES       CLOB                              NOT NULL,
  CONSTRAINT STATEMENTS_PROBLEMS_ID_FK FOREIGN KEY (PROBLEM_ID) REFERENCES PROBLEMS (ID)
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
(NAME, PASSWORD, LOCALE, ROLE)
VALUES
  ('admin', 'admin', 'ru', 'ADMIN'),
  ('user', 'user', 'ru', 'USER');

INSERT INTO PROGRAMMING_LANGUAGES
(name, DESCRIPTION, COMPILER_PATH, IS_COMPILED, INTERPRETER_PATH, EXTENSION, BINARY_EXTENSION, COMPILE_COMMAND, RUN_COMMAND, TIME_LIMIT, MEMORY_LIMIT)
VALUES
  ('g++17', 'C++ 17', 'g++', TRUE, NULL, 'cpp', 'exe', '{COMPILER_PATH} {SOURCE_CODE} -I {TESTLIB_FILE} -o {OUTPUT_EXE}',
            '{SOLUTION_EXE}', 30000, 5242880),
  ('java8', 'Java 8', 'javac', TRUE, 'java', 'java', 'class', '{COMPILER_PATH} {SOURCE_CODE}',
            '{INTERPRETER_PATH} {SOLUTION_EXE}', 30000, 5242880);
