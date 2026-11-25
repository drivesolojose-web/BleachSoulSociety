-- Ejecutar seleccionando la base de datos existente; no elimina tablas ni registros.
SET NAMES utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER DATABASE CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

ALTER TABLE divisiones CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE personajes CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE zanpakutos CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE habilidades CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE personaje_habilidad CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Reinterpreta los bytes UTF-8 que quedaron guardados como caracteres Latin-1.
-- El filtro hace que la reparación sea idempotente y deje intactos los textos correctos.
START TRANSACTION;

UPDATE personajes SET nombre = CONVERT(CAST(CONVERT(nombre USING latin1) AS BINARY) USING utf8mb4)
WHERE HEX(nombre) LIKE '%C383C2%' OR HEX(nombre) LIKE '%C385C2%' OR HEX(nombre) LIKE '%C382C2%';
UPDATE personajes SET apellido = CONVERT(CAST(CONVERT(apellido USING latin1) AS BINARY) USING utf8mb4)
WHERE HEX(apellido) LIKE '%C383C2%' OR HEX(apellido) LIKE '%C385C2%' OR HEX(apellido) LIKE '%C382C2%';
UPDATE personajes SET especie = CONVERT(CAST(CONVERT(especie USING latin1) AS BINARY) USING utf8mb4)
WHERE HEX(especie) LIKE '%C383C2%' OR HEX(especie) LIKE '%C385C2%' OR HEX(especie) LIKE '%C382C2%';
UPDATE personajes SET rango = CONVERT(CAST(CONVERT(rango USING latin1) AS BINARY) USING utf8mb4)
WHERE HEX(rango) LIKE '%C383C2%' OR HEX(rango) LIKE '%C385C2%' OR HEX(rango) LIKE '%C382C2%';

UPDATE divisiones SET nombre = CONVERT(CAST(CONVERT(nombre USING latin1) AS BINARY) USING utf8mb4)
WHERE HEX(nombre) LIKE '%C383C2%' OR HEX(nombre) LIKE '%C385C2%' OR HEX(nombre) LIKE '%C382C2%';
UPDATE divisiones SET descripcion = CONVERT(CAST(CONVERT(descripcion USING latin1) AS BINARY) USING utf8mb4)
WHERE HEX(descripcion) LIKE '%C383C2%' OR HEX(descripcion) LIKE '%C385C2%' OR HEX(descripcion) LIKE '%C382C2%';

UPDATE zanpakutos SET nombre = CONVERT(CAST(CONVERT(nombre USING latin1) AS BINARY) USING utf8mb4)
WHERE HEX(nombre) LIKE '%C383C2%' OR HEX(nombre) LIKE '%C385C2%' OR HEX(nombre) LIKE '%C382C2%';
UPDATE zanpakutos SET shikai = CONVERT(CAST(CONVERT(shikai USING latin1) AS BINARY) USING utf8mb4)
WHERE HEX(shikai) LIKE '%C383C2%' OR HEX(shikai) LIKE '%C385C2%' OR HEX(shikai) LIKE '%C382C2%';
UPDATE zanpakutos SET bankai = CONVERT(CAST(CONVERT(bankai USING latin1) AS BINARY) USING utf8mb4)
WHERE HEX(bankai) LIKE '%C383C2%' OR HEX(bankai) LIKE '%C385C2%' OR HEX(bankai) LIKE '%C382C2%';

UPDATE habilidades SET nombre = CONVERT(CAST(CONVERT(nombre USING latin1) AS BINARY) USING utf8mb4)
WHERE HEX(nombre) LIKE '%C383C2%' OR HEX(nombre) LIKE '%C385C2%' OR HEX(nombre) LIKE '%C382C2%';
UPDATE habilidades SET descripcion = CONVERT(CAST(CONVERT(descripcion USING latin1) AS BINARY) USING utf8mb4)
WHERE HEX(descripcion) LIKE '%C383C2%' OR HEX(descripcion) LIKE '%C385C2%' OR HEX(descripcion) LIKE '%C382C2%';
UPDATE habilidades SET tipo = CONVERT(CAST(CONVERT(tipo USING latin1) AS BINARY) USING utf8mb4)
WHERE HEX(tipo) LIKE '%C383C2%' OR HEX(tipo) LIKE '%C385C2%' OR HEX(tipo) LIKE '%C382C2%';

COMMIT;