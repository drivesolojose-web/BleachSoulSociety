# Registro de trabajo asistido por IA

## Fase 1: API y base de datos

### Prompt registrado

> Actúa como desarrollador Full-Stack Java. Estoy realizando un proyecto académico de Acceso a Datos de 2º DAM llamado BleachSoulSociety, basado en el anime Bleach. Trabaja sobre el proyecto Maven existente y crea directamente todos los archivos necesarios, no te limites a explicar el código. Esta es la primera de tres fases. Configura Java 21, Maven, empaquetado WAR, Tomcat 10.1, Gson, JDBC, MySQL, HikariCP y JUnit 5. Implementa una arquitectura estrictamente desacoplada con paquetes model, dto, dao, service, controller, config y exception. Crea una base de datos MySQL normalizada con cinco tablas relacionadas: personajes, divisiones, zanpakutos, habilidades y personaje_habilidad, incluyendo relaciones 1:N y N:M, claves primarias, claves foráneas y restricciones de integridad referencial. Crea init.sql con DDL y DML e introduce datos coherentes del anime Bleach, incluyendo Ichigo, Rukia, Renji, Byakuya, Kenpachi, Toshiro, Aizen, Urahara, Yoruichi y otros personajes. Implementa CRUD completo para personajes, divisiones, zanpakutos y habilidades, incluyendo la asignación de habilidades a personajes. Implementa búsquedas y filtros. Utiliza PreparedStatement, HikariCP, try-with-resources, transacciones, DTOs y excepciones personalizadas. Separa estrictamente Controller, Service y DAO. Implementa una API mediante Servlets y JSON con validaciones de servidor y respuestas HTTP 200, 201, 400, 404 y 500. No escribas credenciales ni URLs de conexión directamente en Java; utiliza variables de entorno. Crea .env.example y .gitignore excluyendo .env, target y archivos innecesarios. Crea AI_WORKFLOW.md en español registrando este prompt y las decisiones arquitectónicas reales, dejando espacio para mis revisiones y correcciones manuales. Utiliza código sencillo, organizado y comentarios en español adecuados para un estudiante principiante de 2º DAM. Comprueba que el proyecto compila y corrige los errores encontrados. Registra los cambios en commits Git atómicos y descriptivos, sin publicar el repositorio. Trabaja únicamente dentro del proyecto actual. No instales programas del sistema. No inventes resultados de pruebas. Todavía no implementes la interfaz web, Docker Compose ni la batería completa de pruebas, porque serán las siguientes fases. Al terminar, indica los archivos creados, los endpoints disponibles, las comprobaciones realizadas y cualquier problema pendiente.

### Decisiones arquitectónicas y cambios de la fase

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

## Fase 2: interfaz web

### Prompt registrado

> Continúa trabajando sobre el proyecto BleachSoulSociety existente, conservando el backend y la base de datos creados en la primera fase. Esta es la segunda de tres fases. Analiza los endpoints, DTOs y respuestas JSON existentes y crea directamente una interfaz web completa utilizando HTML5 semántico, CSS3 y JavaScript moderno con fetch. Quiero una estética profesional inspirada en el anime Bleach, con fondo oscuro, colores negro, blanco, naranja y detalles rojos, tipografía moderna, tarjetas visuales, efectos hover, transiciones suaves y diseño responsivo para ordenadores, tablets y móviles. Crea una página de inicio atractiva con el título Bleach Soul Society, una presentación del universo Bleach y un menú de navegación con las secciones Inicio, Personajes, Divisiones del Gotei 13, Zanpakutos y Habilidades. Implementa todas las pantallas necesarias para consultar, añadir, modificar y eliminar personajes, divisiones, zanpakutos y habilidades, además de asignar y retirar habilidades de los personajes. Incluye tarjetas de personajes, vistas de detalle, buscador, filtros por raza y división, formularios intuitivos, validaciones, confirmaciones antes de eliminar registros, indicadores de carga, mensajes de éxito y mensajes de error. Quiero que visualmente parezca una enciclopedia interactiva del universo Bleach y no una simple aplicación de gestión de bases de datos. Utiliza imágenes locales autorizadas si están disponibles y, en caso contrario, crea marcadores visuales originales y prepara una carpeta de imágenes con una estructura que permita añadir posteriormente fotografías de los personajes fácilmente. Conecta todas las funcionalidades con los endpoints reales creados en la primera fase mediante fetch y rutas compatibles con el contexto de despliegue de Tomcat. No utilices datos ficticios en JavaScript para simular operaciones que deben realizarse en MySQL. Mantén HTML, CSS y JavaScript en archivos separados y no mezcles código Java ni SQL con las vistas. Utiliza código sencillo, organizado y comentado en español, adecuado para un estudiante principiante de 2º DAM. Revisa la coherencia entre frontend y backend y corrige las incompatibilidades que encuentres. Registra este prompt, las decisiones técnicas y los cambios reales en AI_WORKFLOW.md y actualiza README.md. Realiza commits Git atómicos y descriptivos sin publicar el repositorio. Trabaja únicamente dentro de la carpeta del proyecto actual. No instales programas del sistema ni inventes resultados de comprobaciones. Todavía no implementes Docker Compose ni finalices la batería de pruebas, porque serán parte de la tercera fase, donde también resolveremos la compilación pendiente de Java y Maven. Al terminar, indica las pantallas creadas, las funcionalidades implementadas, los archivos modificados y los problemas pendientes.

### Decisiones y cambios reales

- Se crearon la página de inicio y las secciones de personajes, divisiones, zanpakutos y habilidades en `src/main/webapp/index.html`; CSS y JavaScript se mantienen separados en `assets/css/styles.css` y `assets/js/app.js`.
- La interfaz usa `fetch` contra los endpoints y las propiedades JSON comprobadas en los servlets/DTOs de fase 1, con el contexto deducido de la URL para el despliegue WAR. Los registros y métricas se cargan desde la API; no hay datos de dominio simulados.
- El backend admite `nombre` y `divisionId` para la lista de personajes; el filtro por raza se aplica en el cliente a los registros recibidos. Los filtros restantes usan los campos que proporciona cada API.
- La ficha de personaje obtiene zanpakutos y habilidades de sus endpoints reales y permite asignar/retirar habilidades. Las acciones CRUD muestran estados de carga, errores o confirmaciones según corresponde.
- No se encontraron imágenes locales autorizadas. Se añadieron marcadores originales y la guía `assets/images/personajes/README.md` para retratos WebP autorizados por ID.
- Al cerrar esta fase no se pudo validar la interfaz en navegador, Tomcat o MySQL porque Java y Maven no estaban disponibles. La fase 3 incorpora la validación del WAR, la API y MySQL mediante Docker; no había una herramienta de navegador disponible para inspección visual.

## Fase 3: Docker, integración y cierre

### Prompt registrado

> Continúa trabajando sobre el proyecto BleachSoulSociety existente, conservando el backend, la base de datos y la interfaz web creados en las dos fases anteriores. Esta es la tercera y última fase del proyecto académico de Acceso a Datos de 2º DAM. Crea un Dockerfile multi-stage que utilice Maven y Java 21 para compilar el archivo WAR y Tomcat 10.1 para desplegarlo. Crea docker-compose.yml con tres servicios independientes llamados app, db y phpmyadmin, utilizando MySQL, una red interna, un volumen persistente nombrado, healthcheck de MySQL y depends_on con condition service_healthy. Monta el script init.sql existente en /docker-entrypoint-initdb.d/ para inicializar automáticamente las cinco tablas y sus datos. Configura correctamente las variables de entorno mediante .env y .env.example, sin credenciales ni URLs escritas directamente en Java y sin incluir .env en Git. Utiliza el puerto 8080 para la aplicación y 8081 para phpMyAdmin, salvo que exista algún conflicto. Resuelve la compilación pendiente utilizando Docker con Java y Maven dentro del contenedor si Docker está disponible, o el Maven Wrapper y el JDK existente si procede. No instales programas del sistema sin autorización. Completa las pruebas unitarias y de integración con JUnit 5 y Mockito cuando sea necesario, cubriendo las operaciones CRUD de los DAO, la lógica de los servicios, las validaciones, las claves duplicadas, los registros inexistentes y los casos límite. Utiliza una base de datos aislada para las pruebas y no elimines los datos de la aplicación. Revisa todo el código Java, SQL, HTML, CSS y JavaScript, corrige errores de compilación, conexiones, rutas, validaciones y posibles incompatibilidades entre frontend y backend. Si Docker está disponible, ejecuta docker compose up --build -d, comprueba el estado de los contenedores, verifica los endpoints reales, las operaciones CRUD y la persistencia de datos después de detener y volver a iniciar los contenedores sin eliminar los volúmenes. Comprueba la interfaz en el navegador si tienes herramientas disponibles para ello. Completa README.md en español incluyendo descripción del proyecto, tecnologías utilizadas, diagrama entidad-relación, diagrama de arquitectura de contenedores, estructura de carpetas, configuración de variables de entorno, credenciales de prueba, puertos, instrucciones de instalación y despliegue, ejecución de pruebas y manual de uso. Actualiza AI_WORKFLOW.md registrando los tres prompts, las decisiones arquitectónicas, los errores encontrados, las correcciones reales, las comprobaciones realizadas y dejando espacio para que el alumno documente sus revisiones y correcciones manuales. Revisa que .gitignore excluya .env, target y archivos innecesarios. Comprueba el cumplimiento de todos los requisitos y realiza commits Git atómicos y descriptivos sin publicar el repositorio. No inventes resultados ni afirmes haber ejecutado pruebas que no hayas podido ejecutar. Mantén el código sencillo, organizado y comentado en español, adecuado para un estudiante principiante de 2º DAM. Trabaja únicamente dentro de la carpeta actual. Al finalizar, presenta un informe completo de las comprobaciones realizadas, los resultados de las pruebas, los errores corregidos, los requisitos cumplidos, las instrucciones exactas para abrir la aplicación y cualquier tarea pendiente que deba realizar manualmente antes de entregar el repositorio Git y el archivo ZIP.

### Decisiones arquitectónicas y cambios reales

- `Dockerfile` usa Maven 3.9.9 con JDK 21 para `clean verify` en la etapa de compilación y copia el WAR a Tomcat 10.1. El WAR incluye MySQL Connector/J en `WEB-INF/lib`.
- Compose define `app`, `db` (MySQL 8.0) y `phpmyadmin` (5.2.3). `db` solo se conecta a `bleach-internal`, una red bridge interna; `app` y `phpmyadmin` usan esa red y `bleach-ingress` para publicar 8080 y 8081 en el host. La base tiene volumen nombrado y healthcheck; `app` y phpMyAdmin esperan `service_healthy`.
- El entrypoint de MySQL crea `DB_NAME` y ejecuta `init.sql` dentro de esa base. Se quitaron `CREATE DATABASE` y `USE` fijos del SQL para que el nombre configurado se respete.
- `.env.example` contiene credenciales locales de desarrollo; `.env` está excluido por `.gitignore` y `.dockerignore`. Compose inyecta la URL con host `db`; Java solo lee las variables de entorno.
- Mockito cubre lógica y validaciones de servicios. H2 en modo MySQL crea una base en memoria distinta por prueba para verificar los cinco DAO sin usar ni borrar la base de la aplicación.
- README documenta tecnologías, diagramas ER y de contenedores, capas, variables, credenciales locales, puertos, instrucciones, ejecución de pruebas y uso de la web.

### Errores encontrados y correcciones

- Tomcat no reconoció automáticamente MySQL Connector/J desde el classloader del WAR: se indicó `com.mysql.cj.jdbc.Driver` explícitamente a Hikari en `DatabaseConfig`.
- La red exclusivamente interna impedía publicar puertos hacia Windows en Docker Desktop: se mantuvo la red interna para el tráfico privado de base de datos y se añadió una red de entrada para los puertos de aplicación y phpMyAdmin.
- El script inicial seleccionaba una base fija: ahora el contenedor lo ejecuta sobre `MYSQL_DATABASE`.
- El cliente PowerShell usado para la prueba HTTP necesitó enviar el JSON como bytes UTF-8; no era un defecto del servidor ni requirió cambiar la API.

### Comprobaciones realizadas

- `docker compose up --build -d` terminó correctamente; MySQL quedó saludable y los tres servicios iniciados.
- `mvn clean verify` dentro de la imagen de compilación: 15 pruebas, 0 fallos, 0 errores y 0 omitidas.
- La base MySQL inicializó las cinco tablas y mostró 16 personajes y 13 divisiones; la API informó además 12 zanpakutos y 12 habilidades.
- Web y phpMyAdmin respondieron HTTP 200. La API real completó crear, consultar, actualizar y eliminar para los cuatro recursos y asignar/retirar habilidades con los estados esperados.
- La revisión sintáctica previa de `app.js` con `node --check` había terminado correctamente; navegador visual no disponible en las herramientas de esta sesión.
- `docker compose stop` seguido de `docker compose start` mantuvo los contenedores y el volumen `bleachsoulsociety_mysql_data`; un GET posterior devolvió el marcador temporal con ID 16 y contenido exacto. Se eliminó después solo tras volver a verificar nombre y descripción; no se usó `down -v` ni se borró el volumen.

### Revisiones y correcciones manuales del alumno

- Observaciones y cambios manuales: _Escribe aquí tus revisiones, correcciones y decisiones._
- Fecha de revisión: ____________________
- Archivos revisados: ____________________