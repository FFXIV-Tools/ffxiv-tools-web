CREATE TABLE IF NOT EXISTS account
(
    id            BIGINT NOT NULL,
    name          TEXT   NOT NULL DEFAULT '',
    datacenter_id INT    NOT NULL DEFAULT 0,
    world_id      INT    NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS item
(
    id         INT  NOT NULL,
    name       TEXT NOT NULL,
    icon       INT  NOT NULL,
    marketable INT  NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS item_names ON item (name);
CREATE INDEX IF NOT EXISTS item_names_lower ON item (LOWER(name));

CREATE TABLE IF NOT EXISTS item_prices
(
    item_id       INT  NOT NULL REFERENCES item (id) ON DELETE CASCADE NOT NULL,
    datacenter_id INT  NOT NULL DEFAULT 0,
    world_id      INT  NOT NULL DEFAULT 0,
    minimum       INT  NOT NULL,
    mean          INT  NOT NULL,
    deviation     REAL NOT NULL,
    nq_velocity   REAL NOT NULL DEFAULT 0,
    hq_velocity   REAL NOT NULL DEFAULT 0,
    PRIMARY KEY (item_id, datacenter_id, world_id)
);

CREATE TABLE IF NOT EXISTS recipe
(
    id       INT NOT NULL,
    item_id  INT NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS recipe_materials
(
    recipe_id INT NOT NULL REFERENCES recipe (id) ON DELETE CASCADE NOT NULL,
    item_id   INT NOT NULL REFERENCES item (id) ON DELETE CASCADE NOT NULL,
    quantity  INT NOT NULL,
    PRIMARY KEY (recipe_id, item_id)
);

CREATE TABLE IF NOT EXISTS watch
(
    id         BIGSERIAL,
    item_id    INT REFERENCES item (id) ON DELETE CASCADE,
    recipe_id  INT REFERENCES recipe (id) ON DELETE CASCADE,
    account_id BIGINT REFERENCES account (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS watch_unique_items ON watch (account_id, item_id) WHERE recipe_id IS NULL;
CREATE UNIQUE INDEX IF NOT EXISTS watch_unique_recipes ON watch (account_id, recipe_id) WHERE item_id IS NULL;

CREATE TABLE IF NOT EXISTS currency_items
(
    currency_id INT NOT NULL REFERENCES item (id) ON DELETE CASCADE,
    item_id     INT NOT NULL REFERENCES item (id) ON DELETE CASCADE,
    cost        INT NOT NULL,
    quantity    INT NOT NULL,
    PRIMARY KEY (currency_id, item_id)
);
