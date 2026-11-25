-- Docker selecciona la base indicada por MYSQL_DATABASE antes de ejecutar este archivo.
-- Para una ejecución manual, crea y selecciona primero la base de datos que vayas a utilizar.

CREATE TABLE IF NOT EXISTS divisiones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(500),
    CONSTRAINT chk_divisiones_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) > 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS personajes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100),
    especie VARCHAR(60) NOT NULL,
    rango VARCHAR(100),
    division_id INT,
    CONSTRAINT fk_personajes_divisiones FOREIGN KEY (division_id)
        REFERENCES divisiones (id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT chk_personajes_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) > 0),
    INDEX idx_personajes_nombre (nombre),
    INDEX idx_personajes_division (division_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS zanpakutos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    personaje_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    shikai VARCHAR(500),
    bankai VARCHAR(500),
    CONSTRAINT fk_zanpakutos_personajes FOREIGN KEY (personaje_id)
        REFERENCES personajes (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chk_zanpakutos_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) > 0),
    INDEX idx_zanpakutos_personaje (personaje_id),
    INDEX idx_zanpakutos_nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS habilidades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(500),
    tipo VARCHAR(50) NOT NULL,
    CONSTRAINT chk_habilidades_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) > 0),
    CONSTRAINT chk_habilidades_tipo CHECK (CHAR_LENGTH(TRIM(tipo)) > 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS personaje_habilidad (
    personaje_id INT NOT NULL,
    habilidad_id INT NOT NULL,
    PRIMARY KEY (personaje_id, habilidad_id),
    CONSTRAINT fk_personaje_habilidad_personajes FOREIGN KEY (personaje_id)
        REFERENCES personajes (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_personaje_habilidad_habilidades FOREIGN KEY (habilidad_id)
        REFERENCES habilidades (id) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_personaje_habilidad_habilidad (habilidad_id)
) ENGINE=InnoDB;

-- Datos iniciales del Gotei 13 y sus aliados.
INSERT INTO divisiones (id, nombre, descripcion) VALUES
    (1, 'Primera División', 'Comandancia y liderazgo del Gotei 13.'),
    (2, 'Segunda División', 'Operaciones especiales y sigilo.'),
    (3, 'Tercera División', 'División del Gotei 13.'),
    (4, 'Cuarta División', 'Atención médica y apoyo.'),
    (5, 'Quinta División', 'División del Gotei 13.'),
    (6, 'Sexta División', 'División conocida por su disciplina.'),
    (7, 'Séptima División', 'División del Gotei 13.'),
    (8, 'Octava División', 'División del Gotei 13.'),
    (9, 'Novena División', 'División del Gotei 13.'),
    (10, 'Décima División', 'División del Gotei 13.'),
    (11, 'Undécima División', 'Combate directo.'),
    (12, 'Duodécima División', 'Investigación y desarrollo.'),
    (13, 'Decimotercera División', 'Protección de la Sociedad de Almas.')
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

INSERT INTO personajes (id, nombre, apellido, especie, rango, division_id) VALUES
    (1, 'Ichigo', 'Kurosaki', 'Shinigami sustituto', 'Shinigami sustituto', NULL),
    (2, 'Rukia', 'Kuchiki', 'Shinigami', 'Teniente', 13),
    (3, 'Renji', 'Abarai', 'Shinigami', 'Teniente', 6),
    (4, 'Byakuya', 'Kuchiki', 'Shinigami', 'Capitán', 6),
    (5, 'Kenpachi', 'Zaraki', 'Shinigami', 'Capitán', 11),
    (6, 'Toshiro', 'Hitsugaya', 'Shinigami', 'Capitán', 10),
    (7, 'Sosuke', 'Aizen', 'Shinigami', 'Capitán', 5),
    (8, 'Kisuke', 'Urahara', 'Shinigami', 'Antiguo capitán', NULL),
    (9, 'Yoruichi', 'Shihouin', 'Shinigami', 'Antigua capitana', 2),
    (10, 'Orihime', 'Inoue', 'Humana', 'Aliada', NULL),
    (11, 'Yasutora', 'Sado', 'Humano', 'Aliado', NULL),
    (12, 'Uryu', 'Ishida', 'Quincy', 'Aliado', NULL),
    (13, 'Gin', 'Ichimaru', 'Shinigami', 'Capitán', 3),
    (14, 'Rangiku', 'Matsumoto', 'Shinigami', 'Teniente', 10),
    (15, 'Jushiro', 'Ukitake', 'Shinigami', 'Capitán', 13),
    (16, 'Shunsui', 'Kyoraku', 'Shinigami', 'Capitán', 8)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), apellido = VALUES(apellido),
    especie = VALUES(especie), rango = VALUES(rango), division_id = VALUES(division_id);

INSERT INTO zanpakutos (id, personaje_id, nombre, shikai, bankai) VALUES
    (1, 1, 'Zangetsu', 'Getsuga Tensho', 'Tensa Zangetsu'),
    (2, 2, 'Sode no Shirayuki', 'Danza de la luna blanca', 'Hakka no Togame'),
    (3, 3, 'Zabimaru', 'Aúlla, Zabimaru', 'Hihio Zabimaru'),
    (4, 4, 'Senbonzakura', 'Desmorónate, Senbonzakura', 'Senbonzakura Kageyoshi'),
    (5, 5, 'Nozarashi', 'Bebe, Nozarashi', 'Bankai de Kenpachi'),
    (6, 6, 'Hyorinmaru', 'Gobierna los cielos helados', 'Daiguren Hyorinmaru'),
    (7, 7, 'Kyoka Suigetsu', 'Rompe, Kyoka Suigetsu', NULL),
    (8, 8, 'Benihime', 'Despierta, Benihime', 'Kannonbiraki Benihime Aratame'),
    (10, 13, 'Shinsō', 'Dispara, Shinsō', 'Kamishini no Yari'),
    (11, 14, 'Haineko', 'Ronronea, Haineko', NULL),
    (12, 15, 'Sogyo no Kotowari', 'Que todas las olas sean mi escudo', 'Desconocido'),
    (13, 16, 'Katen Kyokotsu', 'Las flores se convierten en viento', 'Karamatsu Shinju')
ON DUPLICATE KEY UPDATE personaje_id = VALUES(personaje_id), nombre = VALUES(nombre),
    shikai = VALUES(shikai), bankai = VALUES(bankai);

INSERT INTO habilidades (id, nombre, descripcion, tipo) VALUES
    (1, 'Zanjutsu', 'Técnica de combate con espada.', 'Combate'),
    (2, 'Hakuda', 'Combate cuerpo a cuerpo.', 'Combate'),
    (3, 'Hohō', 'Técnicas de desplazamiento rápido.', 'Movimiento'),
    (4, 'Kidō', 'Hechizos y técnicas espirituales.', 'Espiritual'),
    (5, 'Shunpo', 'Desplazamiento de alta velocidad.', 'Movimiento'),
    (6, 'Getsuga Tensho', 'Onda de energía de Zangetsu.', 'Zanpakuto'),
    (7, 'Shunko', 'Combina Hakuda con energía espiritual.', 'Combate'),
    (8, 'Santen Kesshun', 'Técnica defensiva de Orihime.', 'Defensa'),
    (9, 'Heilig Pfeil', 'Flecha espiritual Quincy.', 'Espiritual'),
    (10, 'Pesquisa', 'Percepción de energía espiritual.', 'Percepción'),
    (11, 'Shikai', 'Liberación inicial de una zanpakuto.', 'Zanpakuto'),
    (12, 'Bankai', 'Liberación final de una zanpakuto.', 'Zanpakuto')
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion), tipo = VALUES(tipo);

INSERT INTO personaje_habilidad (personaje_id, habilidad_id) VALUES
    (1, 1), (1, 3), (1, 5), (1, 6), (1, 11), (1, 12),
    (2, 1), (2, 3), (2, 4), (2, 11), (2, 12),
    (3, 1), (3, 3), (3, 4), (3, 11), (3, 12),
    (4, 1), (4, 3), (4, 4), (4, 11), (4, 12),
    (5, 1), (5, 2), (5, 11), (5, 12),
    (6, 1), (6, 3), (6, 4), (6, 11), (6, 12),
    (7, 1), (7, 3), (7, 4), (7, 11), (7, 12),
    (8, 1), (8, 4), (8, 11), (8, 12),
    (9, 2), (9, 3), (9, 5), (9, 7),
    (10, 8), (11, 2), (12, 9), (12, 10),
    (13, 1), (13, 3), (13, 11), (13, 12),
    (14, 1), (14, 3), (14, 11),
    (15, 1), (15, 4), (15, 11),
    (16, 1), (16, 3), (16, 11), (16, 12)
ON DUPLICATE KEY UPDATE habilidad_id = VALUES(habilidad_id);