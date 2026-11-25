# BleachSoulSociety

Backend académico con Java 21, Maven, WAR para Tomcat 10.1, Servlets Jakarta, Gson, JDBC, MySQL y HikariCP.

## Preparación

1. Crea la base de datos ejecutando `src/main/resources/db/init.sql` en MySQL.
2. Configura las variables de entorno `DB_URL`, `DB_USER` y `DB_PASSWORD` usando `.env.example` como referencia. El proyecto no carga automáticamente archivos `.env`.
3. Compila el WAR con `mvn clean package` y despliega `target/BleachSoulSociety.war` en Tomcat 10.1.

La aplicación obtiene las credenciales únicamente del entorno. `DB_POOL_SIZE` es opcional y vale `10` si no se define.

## API

Todas las rutas usan el prefijo `/api` y reciben/devuelven JSON. Las respuestas de error contienen `{ "error": "..." }`.

| Recurso | Operaciones | Filtros de lista |
| --- | --- | --- |
| `/personajes` | `GET`, `POST`; `GET /{id}`, `PUT /{id}`, `DELETE /{id}` | `nombre`, `divisionId` |
| `/divisiones` | `GET`, `POST`; `GET /{id}`, `PUT /{id}`, `DELETE /{id}` | `nombre` |
| `/zanpakutos` | `GET`, `POST`; `GET /{id}`, `PUT /{id}`, `DELETE /{id}` | `nombre`, `personajeId` |
| `/habilidades` | `GET`, `POST`; `GET /{id}`, `PUT /{id}`, `DELETE /{id}` | `nombre`, `tipo` |
| `/personajes/{id}/habilidades` | `GET`, `POST` (cuerpo `{ "habilidadId": 1 }`) | — |
| `/personajes/{id}/habilidades/{habilidadId}` | `DELETE` | — |

Las creaciones responden `201`; consultas, actualizaciones, eliminaciones y asignaciones responden `200`. Las validaciones devuelven `400`, los recursos inexistentes `404` y los errores internos `500`.

Los cuerpos de escritura usan las propiedades de los DTO correspondientes: personaje (`nombre`, `apellido`, `especie`, `rango`, `divisionId`), división (`nombre`, `descripcion`), zanpakuto (`personajeId`, `nombre`, `shikai`, `bankai`) y habilidad (`nombre`, `descripcion`, `tipo`).