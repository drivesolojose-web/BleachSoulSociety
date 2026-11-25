# Registro de trabajo asistido por IA

## Prompt de esta fase

> Actúa como desarrollador Full-Stack Java. Estoy realizando un proyecto académico de Acceso a Datos de 2º DAM llamado BleachSoulSociety, basado en el anime Bleach. Trabaja sobre el proyecto Maven existente y crea directamente todos los archivos necesarios, no te limites a explicar el código. Esta es la primera de tres fases. Configura Java 21, Maven, empaquetado WAR, Tomcat 10.1, Gson, JDBC, MySQL, HikariCP y JUnit 5. Implementa una arquitectura estrictamente desacoplada con paquetes model, dto, dao, service, controller, config y exception. Crea una base de datos MySQL normalizada con cinco tablas relacionadas: personajes, divisiones, zanpakutos, habilidades y personaje_habilidad, incluyendo relaciones 1:N y N:M, claves primarias, claves foráneas y restricciones de integridad referencial. Crea init.sql con DDL y DML e introduce datos coherentes del anime Bleach, incluyendo Ichigo, Rukia, Renji, Byakuya, Kenpachi, Toshiro, Aizen, Urahara, Yoruichi y otros personajes. Implementa CRUD completo para personajes, divisiones, zanpakutos y habilidades, incluyendo la asignación de habilidades a personajes. Implementa búsquedas y filtros. Utiliza PreparedStatement, HikariCP, try-with-resources, transacciones, DTOs y excepciones personalizadas. Separa estrictamente Controller, Service y DAO. Implementa una API mediante Servlets y JSON con validaciones de servidor y respuestas HTTP 200, 201, 400, 404 y 500. No escribas credenciales ni URLs de conexión directamente en Java; utiliza variables de entorno. Crea .env.example y .gitignore excluyendo .env, target y archivos innecesarios. Crea AI_WORKFLOW.md en español registrando este prompt y las decisiones arquitectónicas reales, dejando espacio para mis revisiones y correcciones manuales. Utiliza código sencillo, organizado y comentarios en español adecuados para un estudiante principiante de 2º DAM. Comprueba que el proyecto compila y corrige los errores encontrados. Registra los cambios en commits Git atómicos y descriptivos, sin publicar el repositorio. Trabaja únicamente dentro del proyecto actual. No instales programas del sistema. No inventes resultados de pruebas. Todavía no implementes la interfaz web, Docker Compose ni la batería completa de pruebas, porque serán las siguientes fases. Al terminar, indica los archivos creados, los endpoints disponibles, las comprobaciones realizadas y cualquier problema pendiente.

## Decisiones arquitectónicas aplicadas

- Java 21 se configura con `maven.compiler.release`; Maven produce un WAR compatible con Servlet 6.0, destinado a Tomcat 10.1.
- Los paquetes `model`, `dto`, `dao`, `service`, `controller`, `config` y `exception` separan entidades, entrada/salida JSON, persistencia, reglas de negocio, HTTP, composición de dependencias y errores.
- Los servlets no ejecutan SQL: llaman a servicios, que validan y coordinan DAOs; los DAOs encapsulan JDBC y usan consultas parametrizadas.
- HikariCP se inicializa al desplegar la aplicación. La URL y las credenciales se obtienen de `DB_URL`, `DB_USER` y `DB_PASSWORD`; el tamaño de pool opcional usa `DB_POOL_SIZE`.
- `personajes.division_id` establece la relación opcional 1:N entre divisiones y personajes; `personajes` a `zanpakutos` es 1:N; `personaje_habilidad` implementa la relación N:M con clave primaria compuesta y borrado en cascada.
- La asignación de habilidades comprueba ambos registros y confirma o revierte la operación en una transacción JDBC.
- El esquema y los datos semilla se mantienen mediante `IF NOT EXISTS` y `ON DUPLICATE KEY UPDATE`.
- No se añadió interfaz web, Docker Compose ni una batería completa de pruebas, de acuerdo con el alcance de esta fase.

## Revisiones manuales pendientes

- Notas y correcciones del estudiante:

  _Escribe aquí tus observaciones, cambios manuales y decisiones que quieras revisar._

- Fecha de revisión: ____________________
- Archivos revisados: ____________________