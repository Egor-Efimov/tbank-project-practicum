ALTER TABLE schedules
    RENAME COLUMN action TO action_type;

ALTER TABLE schedules
    ADD COLUMN IF NOT EXISTS target_value INT,
    ADD COLUMN IF NOT EXISTS last_executed_at TIMESTAMP;

ALTER TABLE schedules
    ALTER COLUMN action_type TYPE VARCHAR(50),
    ALTER COLUMN action_type SET NOT NULL;

