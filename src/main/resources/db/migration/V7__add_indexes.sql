CREATE INDEX IF NOT EXISTS idx_schedules_room_id ON schedules(room_id);
CREATE INDEX IF NOT EXISTS idx_schedules_enabled ON schedules(enabled);
CREATE INDEX IF NOT EXISTS idx_action_log_timestamp ON action_log(timestamp);
CREATE INDEX IF NOT EXISTS idx_action_log_room_id ON action_log(room_id);
CREATE INDEX IF NOT EXISTS idx_weather_log_timestamp ON weather_log(timestamp);
