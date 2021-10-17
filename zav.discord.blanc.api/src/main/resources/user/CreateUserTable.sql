CREATE TABLE IF NOT EXISTS 'User' (
    'id' INTEGER NOT NULL,
    'name' TEXT NOT NULL,
    'discriminator' INTEGER NOT NULL,
    'rank' TEXT NOT NULL,
    PRIMARY KEY ('id')
);