CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       status VARCHAR(30) NOT NULL,
                       first_name VARCHAR(100),
                       last_name VARCHAR(100),
                       last_login_at TIMESTAMP NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE roles (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       code VARCHAR(100) NOT NULL UNIQUE,
                       name VARCHAR(150) NOT NULL,
                       description VARCHAR(500),
                       system_role BOOLEAN NOT NULL DEFAULT FALSE,
                       active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE permissions (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             code VARCHAR(150) NOT NULL UNIQUE,
                             name VARCHAR(150) NOT NULL,
                             resource VARCHAR(100) NOT NULL,
                             action VARCHAR(50) NOT NULL,
                             description VARCHAR(500),
                             active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
                            CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE role_permissions (
                                  role_id BIGINT NOT NULL,
                                  permission_id BIGINT NOT NULL,
                                  PRIMARY KEY (role_id, permission_id),
                                  CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id),
                                  CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id)
);