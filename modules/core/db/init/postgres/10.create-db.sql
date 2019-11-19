-- begin DDCDV_ENTITY_ATTRIBUTE_DV
create table DDCDV_ENTITY_ATTRIBUTE_DV (
    ID uuid,
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
    VALUE_ text,
    TYPE_ varchar(50) not null,
    --
    primary key (ID)
)^
-- end DDCDV_ENTITY_ATTRIBUTE_DV
