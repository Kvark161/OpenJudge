drop SCHEMA IF EXISTS public CASCADE;

CREATE SCHEMA public;

create sequence users_id_seq;

create sequence submissions_id_seq;

create sequence contests_id_seq;

create sequence statements_id_seq;

create sequence problems_id_seq;

create table databasechangeloglock
(
	id integer not null
		constraint pk_databasechangeloglock
			primary key,
	locked boolean not null,
	lockgranted timestamp,
	lockedby varchar(255)
);

create table databasechangelog
(
	id varchar(255) not null,
	author varchar(255) not null,
	filename varchar(255) not null,
	dateexecuted timestamp not null,
	orderexecuted integer not null,
	exectype varchar(10) not null,
	md5sum varchar(35),
	description varchar(255),
	comments varchar(255),
	tag varchar(255),
	liquibase varchar(20),
	contexts varchar(255),
	labels varchar(255),
	deployment_id varchar(10)
);

create table users
(
	id bigint default nextval('users_id_seq'::regclass) not null
		constraint pk_users
			primary key,
	name text not null
		constraint users_name_key
			unique,
	password text not null,
	is_admin boolean not null,
	locale text not null
);

create table submissions
(
	id bigint default nextval('submissions_id_seq'::regclass) not null
		constraint pk_submissions
			primary key,
	user_id bigint not null
		constraint fk_submissions_users
			references users,
	contest_id bigint not null,
	problem_id bigint not null,
	source_code text not null,
	verdict text not null,
	sending_date_time timestamp not null,
	test_number bigint
);

create table contests
(
	id bigint default nextval('contests_id_seq'::regclass) not null
		constraint pk_contests
			primary key,
	name text not null,
	start_time timestamp with time zone,
	duration_in_minutes integer
);

create table statements
(
	id bigint default nextval('statements_id_seq'::regclass) not null
		constraint pk_statements
			primary key,
	contest_id bigint not null
		constraint fk_statements_contests
			references contests,
	language text not null,
	file_path text not null
);

create table problems
(
	id bigint default nextval('problems_id_seq'::regclass) not null
		constraint pk_problems
			primary key,
	contest_id bigint not null
		constraint fk_problems_contests
			references contests,
	number_in_contest integer not null,
	name text not null,
	time_limit bigint not null,
	memory_limit bigint not null,
	tests_count bigint not null
);

