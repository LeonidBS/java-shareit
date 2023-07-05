drop table IF EXISTS users cascade ;

drop table IF EXISTS items cascade ;

drop table IF EXISTS bookings;

drop table  IF EXISTS comments;

drop table IF EXISTS item_requests;


CREATE TABLE IF NOT EXISTS users
(
    id    INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  varchar(200),
    email varchar(320),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS item_requests
(
    id           INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description  varchar(200),
    user_id      INT,
    CONSTRAINT fk_item_requests_to_users
        FOREIGN KEY (user_id) REFERENCES users (id),
    requested timestamp without time zone
);

CREATE TABLE IF NOT EXISTS items
(
    id          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        varchar(200),
    description varchar(200),
    available   BOOLEAN,
    user_id     INT,
    CONSTRAINT fk_items_to_users
        FOREIGN KEY (user_id) REFERENCES users (id),
    request_id  INT,
    CONSTRAINT fk_requests_to_users
        FOREIGN KEY (request_id) REFERENCES item_requests (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date timestamp without time zone,
    end_date   timestamp without time zone,
    item_id    INT,
    CONSTRAINT fk_bookings_to_items
        FOREIGN KEY (item_id) REFERENCES items (id),
    user_id    INT,
    CONSTRAINT fk_bookings_to_users
        FOREIGN KEY (user_id) REFERENCES users (id),
    status     varchar(16) NOT NULL
);

CREATE TABLE IF NOT EXISTS comments
(
    id      INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text    text,
    item_id INT,
    CONSTRAINT fk_comments_to_items
        FOREIGN KEY (item_id) REFERENCES items (id),
    user_id INT,
    CONSTRAINT fk_comments_to_users
        FOREIGN KEY (user_id) REFERENCES users (id),
    created timestamp without time zone
);
