-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Phrases table
CREATE TABLE IF NOT EXISTS phrases (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Audio files table
CREATE TABLE IF NOT EXISTS audio_files (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    phrase_id BIGINT NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_phrase_id FOREIGN KEY (phrase_id) REFERENCES phrases(id),
    CONSTRAINT uk_user_phrase UNIQUE (user_id, phrase_id)
);

CREATE INDEX IF NOT EXISTS idx_audio_files_lookup ON audio_files(user_id, phrase_id);

-- Insert some sample valid users and phrases for testing
INSERT INTO users (id) VALUES (1), (2), (3)
ON CONFLICT DO NOTHING;

INSERT INTO phrases (id) VALUES (1), (2), (3)
ON CONFLICT DO NOTHING;
