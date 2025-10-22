ALTER TABLE notification.entity_type_enum
    ADD COLUMN base_link_template TEXT;

CREATE TABLE notification.entity_type_template_link
(
    id             INTEGER PRIMARY KEY,
    entity_type_id INTEGER NOT NULL,
    change_type    TEXT    NOT NULL,
    link_template  TEXT    NOT NULL
);

ALTER TABLE notification.entity_type_template_link
    ADD CONSTRAINT fk_entity_type_template_link_entity_type_id
        FOREIGN KEY (entity_type_id)
            REFERENCES notification.entity_type_enum (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE;