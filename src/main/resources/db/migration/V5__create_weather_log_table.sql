CREATE TABLE IF NOT EXISTS weather_log (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    city VARCHAR(100),
    temperature DOUBLE PRECISION,
    feels_like DOUBLE PRECISION,
    description VARCHAR(255),
    humidity INT,
    pressure INT,
    wind_speed DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
