# Imágenes locales: procedencia, licencia y registros pendientes

## Qué se incluye

Todos los recursos gráficos de esta carpeta se sirven desde el propio WAR; el frontend no carga imágenes desde sitios externos.

| Archivo | Uso | Procedencia y licencia |
| --- | --- | --- |
| `portada/portada.jpg` | Fondo principal de la portada | Imagen incorporada manualmente por el alumno. No consta su fuente ni una licencia verificable; autorización pendiente antes de publicar o redistribuir. Se conserva localmente y no se incluye en el commit. |
| `personajes/*.jpg` | 16 retratos vinculados a personajes | Imágenes incorporadas manualmente por el alumno. No constan las fuentes ni licencias verificables; autorización pendiente antes de publicar o redistribuir. Se conservan localmente y no se incluyen en el commit. Asociación por ID detallada abajo. |
| `portada/panorama.svg` | Panorama vectorial original, conservado como recurso alternativo | Dibujo vectorial creado para este proyecto; no contiene recursos externos ni arte de Bleach. No se declara una licencia independiente para el archivo. |
| `personajes/marcador.svg` | Marcador genérico para cada personaje | Dibujo vectorial original del proyecto; no es el retrato de ningún personaje. No se declara una licencia independiente para el archivo. |
| `divisiones/marcador.svg` | Marcador genérico para cada división | Símbolo geométrico original, no es una insignia oficial. No se declara una licencia independiente para el archivo. |
| `zanpakutos/marcador.svg` | Marcador genérico para cada zanpakuto | Dibujo vectorial genérico original, no representa una zanpakuto concreta. No se declara una licencia independiente para el archivo. |
| `habilidades/marcador.svg` | Marcador genérico para cada habilidad | Símbolo de energía original, no representa una técnica concreta. No se declara una licencia independiente para el archivo. |

Los marcadores se usan para las divisiones, zanpakutos y habilidades que no tienen imagen específica, y como alternativa para personajes sin foto o cuando falla la carga de su retrato. Se asocian al `id` y nombre devueltos por la API, pero no representan de forma fiel ni autorizada las obras descritas. El repositorio no incluye un archivo `LICENSE`; por ello, no se atribuye a estos SVG una licencia abierta o de terceros.

## Derechos de terceros y criterio de descarga

Las imágenes de personajes, insignias, armas y técnicas de Bleach pueden estar protegidas aunque aparezcan en una wiki o en una página de fans. La página de derechos de VIZ publica avisos de copyright de Bleach (entre ellos Tite Kubo/Shueisha y, para la animación, TV TOKYO, dentsu y Pierrot); la política de imágenes de Bleach Wiki explica que el material oficial no pasa a ser de libre uso por estar alojado allí.

- Referencias consultadas: [VIZ Copyrights](https://www.viz.com/copyrights) y [Bleach Wiki Image Policy](https://bleach.fandom.com/wiki/Bleach_Wiki:Image_Policy).
- Fecha de consulta: 25-11-2026.
- El alumno incorporó manualmente las 16 fotografías de personajes y `portada.jpg`. Se verificó visualmente la correspondencia de los retratos con los nombres del catálogo, pero no constan la procedencia, autoría, titularidad ni licencia de esos archivos; la comprobación visual no concede derechos de uso.
- No se descargaron imágenes nuevas ni se contactó a los titulares de derechos durante esta tarea. No se considera que las imágenes locales estén autorizadas: se debe obtener y documentar el permiso o licencia antes de publicar o redistribuir el repositorio, el WAR o el ZIP.
- Las páginas citadas sirven para identificar derechos y titulares, no conceden permiso de uso para este proyecto.
- Si se obtienen imágenes autorizadas, guarda cada archivo localmente, conserva la atribución y el texto de licencia exigidos, y añade el origen, autor, licencia, enlace y cualquier modificación a este inventario antes de distribuir el WAR o ZIP.

## Inventario por registros

Los siguientes son los registros semilla de `src/main/resources/db/init.sql`. El frontend vincula las fotos de personajes por ID, usando los nombres de archivo existentes; no se renombraron porque pudieron asociarse sin ambigüedad. La autorización de todas las imágenes suministradas manualmente sigue pendiente.

### Personajes: 16 retratos locales, licencia pendiente

| ID | Registro de MySQL | Archivo asociado |
| ---: | --- | --- |
| 1 | Ichigo Kurosaki | `Ichigo Kurosaki.jpg` |
| 2 | Rukia Kuchiki | `Rukia kuchiki.jpg` |
| 3 | Renji Abarai | `Renji Abarai .jpg` |
| 4 | Byakuya Kuchiki | `Byakuya Kuchiki.jpg` |
| 5 | Kenpachi Zaraki | `Kenpachi ZARAKI .jpg` |
| 6 | Toshiro Hitsugaya | `Tōshirō Hitsugaya.jpg` |
| 7 | Sosuke Aizen | `Aizen.jpg` |
| 8 | Kisuke Urahara | `Urahara.jpg` |
| 9 | Yoruichi Shihouin | `Yoruichi Shihouin.jpg` |
| 10 | Orihime Inoue | `Orihime Inoue.jpg` |
| 11 | Yasutora Sado | `Chad Sado.jpg` |
| 12 | Uryu Ishida | `Ishida Uryuu.jpg` |
| 13 | Gin Ichimaru | `Gin ichimaru.jpg` |
| 14 | Rangiku Matsumoto | `matsumoto.jpg` |
| 15 | Jushiro Ukitake | `ukitake.jpg` |
| 16 | Shunsui Kyoraku | `Shunsui Kyōraku.jpg` |

La portada usa `portada/portada.jpg`; también requiere que se verifique su procedencia y licencia.

### Divisiones: 13 imágenes específicas pendientes

| ID | Registro |
| ---: | --- |
| 1 | Primera División |
| 2 | Segunda División |
| 3 | Tercera División |
| 4 | Cuarta División |
| 5 | Quinta División |
| 6 | Sexta División |
| 7 | Séptima División |
| 8 | Octava División |
| 9 | Novena División |
| 10 | Décima División |
| 11 | Undécima División |
| 12 | Duodécima División |
| 13 | Decimotercera División |

### Zanpakutos: 12 imágenes específicas pendientes

| ID | Registro |
| ---: | --- |
| 1 | Zangetsu |
| 2 | Sode no Shirayuki |
| 3 | Zabimaru |
| 4 | Senbonzakura |
| 5 | Nozarashi |
| 6 | Hyorinmaru |
| 7 | Kyoka Suigetsu |
| 8 | Benihime |
| 10 | Shinsō |
| 11 | Haineko |
| 12 | Sogyo no Kotowari |
| 13 | Katen Kyokotsu |

### Habilidades: 12 imágenes específicas pendientes

| ID | Registro |
| ---: | --- |
| 1 | Zanjutsu |
| 2 | Hakuda |
| 3 | Hohō |
| 4 | Kidō |
| 5 | Shunpo |
| 6 | Getsuga Tensho |
| 7 | Shunko |
| 8 | Santen Kesshun |
| 9 | Heilig Pfeil |
| 10 | Pesquisa |
| 11 | Shikai |
| 12 | Bankai |

**Pendiente:** faltan imágenes específicas para las 13 divisiones, 12 zanpakutos y 12 habilidades (37 registros); en esos casos se muestran los marcadores locales. La licencia de los 16 retratos de personajes y de `portada.jpg` tampoco está verificada y debe autorizarse antes de publicar o redistribuir. `panorama.svg` es un recurso vectorial original conservado localmente; ya no es la imagen principal de inicio.