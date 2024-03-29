/* Create Tables */

CREATE TABLE notification.change_type_enum
(
    id integer NOT NULL,
    change_type varchar(50) NOT NULL
)
;

CREATE TABLE notification.entity_auto_subscribe
(
    id integer NOT NULL,
    user_id integer NOT NULL,
    entity_type_id integer NOT NULL
)
;

CREATE TABLE notification.entity_change
(
    id integer NOT NULL,
    entity_id integer NOT NULL,
    link varchar(100) NULL,
    change_type_id integer NOT NULL,
    status_id integer NOT NULL,
    entity_type_id integer NOT NULL
)
;

CREATE TABLE notification.entity_change_sub
(
    id integer NOT NULL,
    id_sub integer NOT NULL,
    id_entity_change integer NOT NULL
)
;

CREATE TABLE notification.entity_subscribe
(
    id integer NOT NULL,
    user_id integer NOT NULL,
    entity_id integer NOT NULL,
    entity_type_id integer NOT NULL
)
;

CREATE TABLE notification.entity_type_enum
(
    id integer NOT NULL,
    type varchar(50) NOT NULL
)
;

CREATE TABLE notification.status_enum
(
    id integer NOT NULL,
    status varchar(50) NOT NULL
)
;

CREATE TABLE notification.subscribe_rule
(
    id integer NOT NULL,
    auto_sub_id integer NOT NULL,
    parameter_name varchar(50) NOT NULL,
    parameter_value varchar(50) NOT NULL
)
;

CREATE TABLE notification."user"
(
    id integer NOT NULL,
    user_id integer NOT NULL,
    email integer NOT NULL
)
;

/* Create Primary Keys, Indexes, Uniques, Checks */

ALTER TABLE notification.change_type_enum ADD CONSTRAINT pk_change_type_enum
    PRIMARY KEY (id)
;

ALTER TABLE notification.entity_auto_subscribe ADD CONSTRAINT pk_entity_auto_subscribe
    PRIMARY KEY (id)
;

CREATE INDEX ixfk_entity_auto_subscribe_entity_type_enum ON notification.entity_auto_subscribe (entity_type_id ASC)
;

CREATE INDEX ixfk_entity_auto_subscribe_user ON notification.entity_auto_subscribe (user_id ASC)
;

ALTER TABLE notification.entity_change ADD CONSTRAINT pk_entity_change
    PRIMARY KEY (id)
;

CREATE INDEX ixfk_entity_change_change_type_enum ON notification.entity_change (change_type_id ASC)
;

CREATE INDEX ixfk_entity_change_entity_type_enum ON notification.entity_change (entity_type_id ASC)
;

CREATE INDEX ixfk_entity_change_status_enum ON notification.entity_change (status_id ASC)
;

ALTER TABLE notification.entity_change_sub ADD CONSTRAINT pk_entity_change_sub
    PRIMARY KEY (id)
;

CREATE INDEX ixfk_entity_change_sub_entity_change ON notification.entity_change_sub (id_entity_change ASC)
;

CREATE INDEX ixfk_entity_change_sub_entity_subscribe ON notification.entity_change_sub (id_sub ASC)
;

ALTER TABLE notification.entity_subscribe ADD CONSTRAINT pk_entity_subscribe
    PRIMARY KEY (id)
;

CREATE INDEX ixfk_entity_subscribe_entity_type_enum ON notification.entity_subscribe (entity_type_id ASC)
;

CREATE INDEX ixfk_entity_subscribe_user ON notification.entity_subscribe (user_id ASC)
;

ALTER TABLE notification.entity_type_enum ADD CONSTRAINT pk_entity_type
    PRIMARY KEY (id)
;

ALTER TABLE notification.status_enum ADD CONSTRAINT pk_status_enum
    PRIMARY KEY (id)
;

ALTER TABLE notification.subscribe_rule ADD CONSTRAINT pk_subscribe_rule
    PRIMARY KEY (id)
;

CREATE INDEX ixfk_subscribe_rule_entity_auto_subscribe ON notification.subscribe_rule (auto_sub_id ASC)
;

ALTER TABLE notification."user" ADD CONSTRAINT pk_user
    PRIMARY KEY (id)
;

/* Create Foreign Key Constraints */

ALTER TABLE notification.entity_auto_subscribe ADD CONSTRAINT fk_entity_auto_subscribe_entity_type_enum
    FOREIGN KEY (entity_type_id) REFERENCES notification.entity_type_enum (id) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE notification.entity_auto_subscribe ADD CONSTRAINT fk_entity_auto_subscribe_user
    FOREIGN KEY (user_id) REFERENCES notification."user" (id) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE notification.entity_change ADD CONSTRAINT fk_entity_change_change_type_enum
    FOREIGN KEY (change_type_id) REFERENCES notification.change_type_enum (id) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE notification.entity_change ADD CONSTRAINT fk_entity_change_entity_type_enum
    FOREIGN KEY (entity_type_id) REFERENCES notification.entity_type_enum (id) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE notification.entity_change ADD CONSTRAINT fk_entity_change_status_enum
    FOREIGN KEY (status_id) REFERENCES notification.status_enum (id) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE notification.entity_change_sub ADD CONSTRAINT fk_entity_change_sub_entity_change
    FOREIGN KEY (id_entity_change) REFERENCES notification.entity_change (id) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE notification.entity_change_sub ADD CONSTRAINT fk_entity_change_sub_entity_subscribe
    FOREIGN KEY (id_sub) REFERENCES notification.entity_subscribe (id) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE notification.entity_subscribe ADD CONSTRAINT fk_entity_subscribe_entity_type_enum
    FOREIGN KEY (entity_type_id) REFERENCES notification.entity_type_enum (id) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE notification.entity_subscribe ADD CONSTRAINT fk_entity_subscribe_user
    FOREIGN KEY (user_id) REFERENCES notification."user" (id) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE notification.subscribe_rule ADD CONSTRAINT fk_subscribe_rule_entity_auto_subscribe
    FOREIGN KEY (auto_sub_id) REFERENCES notification.entity_auto_subscribe (id) ON DELETE No Action ON UPDATE No Action
;
