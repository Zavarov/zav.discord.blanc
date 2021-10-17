CREATE TABLE IF NOT EXISTS 'Guild' (
    'id' INTEGER NOT NULL,
    'name' TEXT NOT NULL,
    'prefix' TEXT,
    'blacklist' TEXT NOT NULL,
    PRIMARY KEY ('id')
);