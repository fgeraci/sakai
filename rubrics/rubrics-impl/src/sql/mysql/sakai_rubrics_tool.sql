-- set schema up

create table if not exists RUBRIC_CELL_T (
    CELL_ID bigint not null auto_increment,
    ROW_ID bigint not null,
    RUBRIC_ID bigint not null,
    COLUMN_TEXT varchar(255) not null,
    COLUMN_WEIGHT integer default 0 not null,
    CELL_TEXT longtext,
    CELL_TYPE varchar(255),
    COLUMN_SEQUENCE integer not null,
    primary key (CELL_ID)
);

create table if not exists RUBRIC_GRADE_T (
    GRADE_ID bigint not null auto_increment,
    SUBMISSION_ID varchar(255) not null,
    POINTES_EARNED double precision,
    RU_CELL_ID bigint not null,
    COMMENT varchar(255),
    primary key (GRADE_ID)
);

create table if not exists RUBRIC_ROW_T (
    ROW_ID bigint not null auto_increment,
    RUBRIC_ID bigint not null,
    SEQUENCE integer not null,
    row_TEXT longtext not null,
    REMOVED bit,
    primary key (ROW_ID)
);

create table if not exists RUBRIC_T (
    RUBRIC_ID bigint not null auto_increment,
    VERSION integer,
    TITLE varchar(255) not null,
    TITLE_KEY varchar(50),
    TEMPLATE bit not null,
    DESCRIPTION varchar(255),
    CREATED_BY varchar(255) not null,
    CREATED_DATE datetime not null,
    MODIFIED_BY varchar(255),
    MODIFIED_DATE datetime,
    ICON varchar(255),
    REMOVED bit,
    DATA_SET longtext,
    primary key (RUBRIC_ID)
);

create table if not exists RUBRIC_BY_ITEM (
	ITEM_ID bigint not null,
	TOOL_ID varchar(250) not null,
	RUBRIC_ID bigint not null,
	foreign key (RUBRIC_ID) references RUBRIC_T(RUBRIC_ID)
    	on delete cascade,
	primary key (ITEM_ID,TOOL_ID)
);
