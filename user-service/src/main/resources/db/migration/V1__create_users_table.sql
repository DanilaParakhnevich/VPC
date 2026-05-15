CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    bio VARCHAR(500),
    locale VARCHAR(10),
    timezone VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    privacy_mode VARCHAR(10) NOT NULL DEFAULT 'PUBLIC',
    is_mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    mfa_secret VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    last_activity_at TIMESTAMP,
    deleted_at TIMESTAMP,
    deleted_reason VARCHAR(255),

    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_username UNIQUE (username),

    CONSTRAINT ck_users_status CHECK (status IN ('PENDING_VERIFICATION', 'ACTIVE', 'SUSPENDED', 'BANNED', 'ARCHIVED')),
    CONSTRAINT ck_users_role CHECK (role IN ('USER', 'MODERATOR', 'ADMIN')),
    CONSTRAINT ck_users_privacy_mode CHECK (privacy_mode IN ('PUBLIC', 'PRIVATE'))
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_users_last_login ON users(last_login_at);

CREATE INDEX IF NOT EXISTS idx_users_deleted_at ON users(deleted_at) WHERE deleted_at IS NOT NULL;