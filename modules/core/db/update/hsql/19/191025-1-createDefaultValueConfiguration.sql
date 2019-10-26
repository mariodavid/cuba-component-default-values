create table DDCDV_DEFAULT_VALUE_CONFIGURATION (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ENTITY varchar(255) not null,
    ENTITY_ATTRIBUTE varchar(255) not null,
    VALUE_ varchar(255),
    --
    primary key (ID)
);