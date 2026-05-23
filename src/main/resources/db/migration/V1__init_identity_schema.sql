CREATE TABLE users (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    email           VARCHAR(150) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100),
    last_name       VARCHAR(100),
    phone           VARCHAR(50),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    is_system       BOOLEAN NOT NULL DEFAULT FALSE,
    token_version   INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT NULL,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by      BIGINT NULL,
    deleted_at      TIMESTAMP NULL,
    deleted_by      BIGINT NULL,
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE roles (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(150) NOT NULL,
    description     VARCHAR(500),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    is_system       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT NULL,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by      BIGINT NULL,
    deleted_at      TIMESTAMP NULL,
    deleted_by      BIGINT NULL,
    CONSTRAINT uk_roles_name UNIQUE (name)
);

CREATE TABLE permissions (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(150) NOT NULL,
    module          VARCHAR(100) NOT NULL,
    action          VARCHAR(50) NOT NULL,
    description     VARCHAR(500),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    is_system       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT NULL,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by      BIGINT NULL,
    deleted_at      TIMESTAMP NULL,
    deleted_by      BIGINT NULL,
    CONSTRAINT uk_permissions_name UNIQUE (name)
);

CREATE TABLE user_roles (
    user_id         BIGINT NOT NULL,
    role_id         BIGINT NOT NULL,
    assigned_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by     BIGINT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE role_permissions (
    role_id         BIGINT NOT NULL,
    permission_id   BIGINT NOT NULL,
    assigned_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by     BIGINT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions (id)
);

CREATE TABLE permission_dependencies (
    permission_id               BIGINT NOT NULL,
    dependency_permission_id    BIGINT NOT NULL,
    PRIMARY KEY (permission_id, dependency_permission_id),
    CONSTRAINT fk_permission_dependencies_permission FOREIGN KEY (permission_id) REFERENCES permissions (id),
    CONSTRAINT fk_permission_dependencies_dependency FOREIGN KEY (dependency_permission_id) REFERENCES permissions (id)
);
