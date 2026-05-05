CREATE TABLE IF NOT EXISTS action_log (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    device_type VARCHAR(20) NOT NULL,
    action VARCHAR(30) NOT NULL,
    old_value INT,
    new_value INT,
    source VARCHAR(20),
    room_id BIGINT REFERENCES rooms(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
