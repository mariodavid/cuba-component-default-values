-- begin DDCDV_ENTITY_ATTRIBUTE_DEFAULT_VALUE
create table DDCDV_ENTITY_ATTRIBUTE_DEFAULT_VALUE (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    ENTITY varchar(255),
    ENTITY_ATTRIBUTE varchar(255),
    --
    VALUE_ varchar(255),
    --
    primary key (ID)
)^
-- end DDCDV_ENTITY_ATTRIBUTE_DEFAULT_VALUE
