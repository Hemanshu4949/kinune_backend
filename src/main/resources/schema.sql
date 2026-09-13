CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username TEXT NOT NULL, 
    email TEXT, 
    phone_number TEXT, 
    password_hash TEXT, 
    display_name TEXT NOT NULL, 
    avatar_url TEXT, 
    bio TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE, 
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    last_seen_at TIMESTAMPTZ, 
    created_at TIMESTAMPTZ NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT clock_timestamp(),
    CONSTRAINT chk_username_length CHECK (char_length(username) BETWEEN 3 AND 30),
    CONSTRAINT chk_username_format CHECK (username ~ '^[a-zA-Z0-9_.]+$'),
    CONSTRAINT chk_display_name_length CHECK (char_length(display_name) BETWEEN 1 AND 50),
    CONSTRAINT chk_bio_length CHECK (bio IS NULL OR char_length(bio) <= 250)
);

CREATE UNIQUE INDEX idx_users_username_lower ON users (lower(username));
CREATE UNIQUE INDEX idx_users_email_lower ON users (lower(email)) WHERE email IS NOT NULL;
CREATE UNIQUE INDEX idx_users_phone ON users (phone_number) WHERE phone_number IS NOT NULL;
CREATE INDEX idx_users_display_name ON users (display_name);
CREATE INDEX idx_users_last_seen ON users (last_seen_at DESC NULLS LAST);

CREATE OR REPLACE FUNCTION update_updated_at_column() RETURNS TRIGGER AS $$
BEGIN NEW.updated_at = clock_timestamp(); RETURN NEW; END; $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TYPE chat_type AS ENUM ('DIRECT', 'GROUP');

CREATE TABLE chats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), 
    type chat_type NOT NULL DEFAULT 'DIRECT',
    title TEXT, 
    avatar_url TEXT, 
    created_at TIMESTAMPTZ NOT NULL DEFAULT clock_timestamp()
);

CREATE TABLE chat_participants (
    chat_id UUID NOT NULL REFERENCES chats(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    last_read_message_id UUID, 
    joined_at TIMESTAMPTZ NOT NULL DEFAULT clock_timestamp(),
    PRIMARY KEY (chat_id, user_id)
);

CREATE INDEX idx_participants_user ON chat_participants(user_id);

CREATE TYPE message_type AS ENUM ('TEXT', 'IMAGE', 'VIDEO', 'AUDIO', 'DOCUMENT', 'SYSTEM');

CREATE TABLE messages (
    id UUID PRIMARY KEY,
    chat_id UUID NOT NULL REFERENCES chats(id) ON DELETE CASCADE,
    sender_id UUID REFERENCES users(id) ON DELETE SET NULL,
    type message_type NOT NULL DEFAULT 'TEXT',
    content TEXT, 
    media_url TEXT, 
    metadata JSONB, 
    created_at TIMESTAMPTZ NOT NULL DEFAULT clock_timestamp()
);

CREATE INDEX idx_messages_chat_id ON messages (chat_id, id DESC);
