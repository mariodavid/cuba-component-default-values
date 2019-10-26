create table DDCDV_CONTEXTUALIZED_CONFIGURATION_ENTITY (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    USER_ID varchar(36),
    VALUE_ longvarchar,
    PROPERTY varchar(255),
    LOCALE varchar(255),
    SCREEN_ID varchar(255),
    ENTITY_ATTRIBUTE varchar(255),
    ENTITY varchar(255),
    --
    primary key (ID)
);