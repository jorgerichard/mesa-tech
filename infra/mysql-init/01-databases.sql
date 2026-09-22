CREATE DATABASE IF NOT EXISTS mesatech_solicitudes;
CREATE DATABASE IF NOT EXISTS mesatech_catalogo;
GRANT ALL PRIVILEGES ON mesatech_solicitudes.* TO 'mesatech'@'%';
GRANT ALL PRIVILEGES ON mesatech_catalogo.* TO 'mesatech'@'%';
FLUSH PRIVILEGES;
