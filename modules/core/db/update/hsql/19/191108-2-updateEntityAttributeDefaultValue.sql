alter table DDCDV_ENTITY_ATTRIBUTE_DEFAULT_VALUE add column TYPE_ varchar(50) ^
update DDCDV_ENTITY_ATTRIBUTE_DEFAULT_VALUE set TYPE_ = 'STATIC_VALUE' where TYPE_ is null ;
alter table DDCDV_ENTITY_ATTRIBUTE_DEFAULT_VALUE alter column TYPE_ set not null ;
