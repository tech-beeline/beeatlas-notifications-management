ALTER TABLE notification.entity_change
    ADD COLUMN children_entity_id INTEGER;

CREATE TABLE notification.change_type_enum (
                                               id INTEGER NOT NULL PRIMARY KEY,
                                               name TEXT NOT NULL,
                                               description TEXT NOT NULL
);

INSERT INTO notification.change_type_enum (id, name, description)
VALUES
    (1, 'CREATE', 'Добавлен новый'),
    (2, 'UPDATE', 'Обновлен'),
    (3, 'DELETE', 'Удален'),
    (4, 'CREATE_OPERATION', 'Создан новый метод'),
    (5, 'UPDATE__OPERATION', 'Обновлен метод'),
    (6, 'DELETE_OPERATION', 'Удален метод');