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

## Interfaz web

La segunda fase añade una interfaz en `src/main/webapp`, servida por Tomcat en la raíz del contexto (`/BleachSoulSociety/` con el nombre WAR configurado). La página de inicio muestra cifras consultadas desde la API y el menú permite abrir las secciones de personajes, divisiones, zanpakutos y habilidades.

- Personajes: búsqueda por nombre, filtros por raza y división, fichas de detalle, CRUD y asignación o retirada de habilidades.
- Divisiones, zanpakutos y habilidades: búsqueda/filtros disponibles, fichas de detalle y CRUD. Las relaciones de portador y división se eligen con registros obtenidos de la API.
- Los formularios envían JSON a las rutas documentadas arriba; los errores del servidor aparecen en pantalla y los borrados requieren confirmación.
- Los filtros se aplican sobre datos reales recuperados del backend. La API solo admite filtros de personaje por `nombre` y `divisionId`; el filtro de raza se realiza en el navegador.
- Los archivos están separados en `index.html`, `assets/css/styles.css` y `assets/js/app.js`. No hay retratos con licencia incorporados; se utilizan marcadores propios y la carpeta `assets/images/personajes` documenta cómo añadir imágenes WebP autorizadas con nombre basado en el ID.

La web necesita que Tomcat y la API puedan acceder a MySQL con las variables de entorno indicadas en la sección de preparación. La batería completa de pruebas y Docker Compose quedan pendientes para la tercera fase.