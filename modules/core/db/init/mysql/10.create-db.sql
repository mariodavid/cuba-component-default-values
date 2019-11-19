-- begin DDCDV_ENTITY_ATTRIBUTE_DV
create table DDCDV_ENTITY_ATTRIBUTE_DV (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    ENTITY varchar(255),
    ENTITY_ATTRIBUTE varchar(255),
    --
    VALUE_ longtext,
    TYPE_ varchar(50) not null,
    --
    primary key (ID)
)^
-- end DDCDV_ENTITY_ATTRIBUTE_DV
