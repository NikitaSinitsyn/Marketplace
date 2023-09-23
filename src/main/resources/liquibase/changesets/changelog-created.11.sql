CREATE TABLE app_user (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    firstName VARCHAR(255),
    lastName VARCHAR(255),
    phone VARCHAR(20),
    role VARCHAR(20),
    image VARCHAR(255)
);

CREATE TABLE ad (
    pk SERIAL PRIMARY KEY,
    title VARCHAR(255),
    price INTEGER,
    description TEXT,
    user_id INTEGER REFERENCES app_user(id),
    image TEXT,
    CONSTRAINT fk_ad_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE comment (
    pk SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES app_user(id),
    author_image VARCHAR(255),
    author_first_name VARCHAR(255),
    created_at INTEGER,
    ad_id INTEGER REFERENCES ad(pk),
    text TEXT,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT fk_comment_ad FOREIGN KEY (ad_id) REFERENCES ad(pk)
);