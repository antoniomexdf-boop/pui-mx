-- Crear bases separadas en MariaDB
CREATE DATABASE IF NOT EXISTS platform_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS datos_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Crear usuarios sugeridos
CREATE USER IF NOT EXISTS 'pui_platform'@'%' IDENTIFIED BY 'CambiarPasswordPlatform123!';
CREATE USER IF NOT EXISTS 'pui_datos'@'%' IDENTIFIED BY 'CambiarPasswordDatos123!';

-- Permisos minimos sugeridos
GRANT ALL PRIVILEGES ON platform_db.* TO 'pui_platform'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON datos_db.* TO 'pui_datos'@'%';

FLUSH PRIVILEGES;
