alter table DDCDV_CONTEXTUALIZED_CONFIGURATION_ENTITY add constraint FK_DDCDV_CONTEXTUALIZED_CONFIGURATION_ENTITY_ON_USER foreign key (USER_ID) references SEC_USER(ID);
create index IDX_DDCDV_CONTEXTUALIZED_CONFIGURATION_ENTITY_ON_USER on DDCDV_CONTEXTUALIZED_CONFIGURATION_ENTITY (USER_ID);