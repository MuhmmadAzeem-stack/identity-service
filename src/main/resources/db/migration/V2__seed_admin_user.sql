INSERT INTO permissions(code, name, resource, action, description, active)
VALUES
    ('user:read', 'Read Users', 'user', 'read', 'Can view users', true),
    ('user:create', 'Create Users', 'user', 'create', 'Can create users', true),
    ('user:update', 'Update Users', 'user', 'update', 'Can update users', true),
    ('user:delete', 'Delete Users', 'user', 'delete', 'Can delete users', true),
    ('role:read', 'Read Roles', 'role', 'read', 'Can view roles', true),
    ('role:create', 'Create Roles', 'role', 'create', 'Can create roles', true),
    ('role:update', 'Update Roles', 'role', 'update', 'Can update roles', true),
    ('permission:read', 'Read Permissions', 'permission', 'read', 'Can view permissions', true),
    ('permission:create', 'Create Permissions', 'permission', 'create', 'Can create permissions', true),
    ('permission:update', 'Update Permissions', 'permission', 'update', 'Can update permissions', true);

INSERT INTO roles(code, name, description, system_role, active)
VALUES ('SUPER_ADMIN', 'Super Admin', 'Full system access', true, true);

INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p
WHERE r.code = 'SUPER_ADMIN';

INSERT INTO users(username, email, password_hash, status, first_name, last_name)
VALUES (
           'admin',
           'admin@omnicore.local',
           '$2a$10$4T3dZovJ5aojwWDjohDO3u4b0pUUDG7ikHQz6nmzKAXqkW4w1La/K',
           'ACTIVE',
           'System',
           'Admin'
       );

INSERT INTO user_roles(user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r
WHERE u.username = 'admin'
  AND r.code = 'SUPER_ADMIN';