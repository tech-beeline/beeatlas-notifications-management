insert into notification.status_enum (id, status)
values (1, 'WAIT_NOTIFY');

insert into notification.change_type_enum (id, change_type)
values (1, 'UPDATE');

insert into notification.change_type_enum (id, change_type)
values (2, 'CREATE');

insert into notification.entity_type_enum (id, type)
values (1, 'TECH_CAPABILITY');

insert into notification.entity_type_enum (id, type)
values (2, 'BUSINESS_CAPABILITY');

DROP SEQUENCE  IF EXISTS  entity_change_id_seq  CASCADE;
CREATE SEQUENCE entity_change_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

DROP SEQUENCE  IF EXISTS  entity_change_sub_id_seq  CASCADE;
CREATE SEQUENCE entity_change_sub_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

DROP SEQUENCE  IF EXISTS  entity_subscribe_sub_id_seq  CASCADE;
CREATE SEQUENCE entity_subscribe_sub_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;