-- Создаем таблицу business_event_enum в схеме notification
CREATE TABLE notification.business_event_enum (
                                                  id INTEGER PRIMARY KEY,  -- Первичный ключ
                                                  name TEXT NOT NULL,      -- Обязательное поле
                                                  description TEXT NOT NULL -- Обязательное поле
);

-- Создаем последовательность seq_business_notify_id в схеме notification
CREATE SEQUENCE notification.seq_business_notify_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Создаем таблицу business_notify в схеме notification
CREATE TABLE notification.business_notify (
                                              id INTEGER PRIMARY KEY DEFAULT nextval('notification.seq_business_notify_id'), -- Значение по умолчанию из последовательности
                                              user_id INTEGER NOT NULL REFERENCES notification.user(id), -- Внешний ключ на таблицу user
                                              entity_id INTEGER NOT NULL, -- Обязательное поле
                                              entity_type_id INTEGER NOT NULL REFERENCES notification.business_event_enum(id),
                                              web_notify BOOLEAN NOT NULL DEFAULT FALSE, -- По умолчанию false
                                              created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL -- Обязательное поле
); 