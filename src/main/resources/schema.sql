CREATE TABLE IF NOT EXISTS user_info (
    user_id INTEGER PRIMARY KEY,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    privilege INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS train_scheduler (
    train_id TEXT PRIMARY KEY,
    seat_num INTEGER,
    start_time TEXT,
    passing_num INTEGER,
    stations TEXT,
    duration TEXT,
    price TEXT
);

CREATE TABLE IF NOT EXISTS route_section (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    train_id TEXT,
    departure_id INTEGER,
    arrival_id INTEGER,
    price INTEGER,
    duration INTEGER
);

CREATE TABLE IF NOT EXISTS station_component (
    station_id INTEGER PRIMARY KEY,
    component_id INTEGER
);

CREATE TABLE IF NOT EXISTS ticket_info (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    train_id TEXT,
    departure_time TEXT,
    departure_station INTEGER,
    arrival_station INTEGER,
    seat_num INTEGER,
    price INTEGER,
    duration INTEGER,
    UNIQUE(train_id, departure_time, departure_station)
);

CREATE TABLE IF NOT EXISTS trip_info (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    train_id TEXT,
    departure_station INTEGER,
    arrival_station INTEGER,
    type INTEGER,
    duration INTEGER,
    price INTEGER,
    departure_time TEXT,
    arrival_time TEXT
);

CREATE TABLE IF NOT EXISTS station (
    id INTEGER PRIMARY KEY,
    name TEXT
);
