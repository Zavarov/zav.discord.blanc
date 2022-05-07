CREATE TABLE IF NOT EXISTS 'User' (
    'id' INTEGER NOT NULL,
    'name' TEXT NOT NULL,
    'discriminator' TEXT NOT NULL,
    'ranks' TEXT NOT NULL,
    PRIMARY KEY ('id')
);