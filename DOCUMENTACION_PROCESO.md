# Automatización de Pruebas Front-End con Serenity BDD — PetStay

**Aplicación bajo prueba:** Hotel para Mascotas PetStay — `https://petstay-aphn.onrender.com`
**Stack:** Java 17 · Gradle 8.14.3 (wrapper) · Serenity BDD 4.2.32 · Cucumber · Screenplay · Selenium/Chrome headless · GitHub Actions

---

## 1. Punto de partida

El proyecto de clase (`C:\Automatizacion_de_pruebas`) se copió a `C:\Automatizacion_PetStay` y se completó ahí. Al ejecutar la copia sin cambios el resultado fue: **1 escenario, 1 fallido**. Diagnóstico:

| Problema encontrado | Causa | Corrección |
|---|---|---|
| El login fallaba | Los `Target` seguían apuntando a OrangeHRM (`//input[@name='username']`, `oxd-topbar...`) | Localizadores reescritos para PetStay |
| `Logueo` con campos `static` | Los datos del login se compartían entre escenarios/actores | Campos de instancia + `instrumented(Logueo.class, usuario, pass)` |
| `Logueo` hacía clic en "forgot password" si no veía el botón | Lógica copiada de OrangeHRM, no existe en PetStay | Eliminada |
| Versiones mezcladas (`serenity-junit 4.3.1` vs `4.2.32`, Selenium `4.38` y `4.15`) | Dependencias agregadas a mano | Una sola variable `serenityVersion = 4.2.32`; Selenium llega como dependencia transitiva de Serenity |
| No existía `gradle/wrapper/gradle-wrapper.jar` y `.gitignore` ignoraba `gradlew`, `gradlew.bat` y `wrapper` | El pipeline ejecuta `chmod +x ./gradlew` → fallaría en un clon limpio | `gradle wrapper --gradle-version 8.14.3` y se dejó de ignorar el wrapper (`!gradle/wrapper/gradle-wrapper.jar`) |
| La configuración de Chrome estaba solo dentro de `environments.chrome` | Esa configuración (incluido headless) solo se aplica si se pasa `-Denvironment=chrome` | Configuración de Chrome movida al bloque `webdriver` raíz (headless por defecto, necesario en CI) |
| `serenity.project.name = orangeHRMDemo`, typo `configureondeman` | Restos de la plantilla | Corregidos |
| `no such element ... {"method":"xpath","selector":"input[name='peso']"}` (detectado al ejecutar) | `Target.locatedBy(...)` adivina el tipo de selector y tomó un CSS con `[...]` como XPath | Todos los selectores CSS llevan el prefijo explícito `css:` (ej. `css:input[name='peso']`) |
| `There is insufficient memory for the Java Runtime Environment` (equipo local con ~7,5 GB de RAM) | Gradle, la JVM de pruebas y Chrome reservaban más memoria de la disponible | Límites en `gradle.properties` (`-Xmx384m`) y `build.gradle` (`maxHeapSize = "384m"`, `SerialGC`); Chrome con `--disable-gpu --disable-extensions --renderer-process-limit=2` |
| El comentario que el pipeline publica en el Issue tenía varias líneas y `jq -R .` generaba un JSON inválido | `jq -R` convierte cada línea en un string separado | `jq -Rs .` (lee todo el texto como un solo string) |

Para la sustentación se agregó el entorno `-Denvironment=visible` (Chrome con ventana) y la guía [GUIA_PRESENTACION.md](GUIA_PRESENTACION.md).

---

## 2. Análisis de la aplicación

PetStay es una SPA estática: todo el estado se guarda en el **`localStorage`** del navegador (`petstay_sesion`, `petstay_mascotas`, `petstay_reservas`). Consecuencias para la automatización:

- Los escenarios deben ser **independientes entre sí**. En la primera ejecución, 5 escenarios fallaron porque no aparecía el campo usuario. Hay dos causas posibles: (a) el navegador se reutilizó y el `localStorage` conservaba la sesión, así que `index.html` redirigía a `mascotas.html`; (b) el equipo se quedó sin memoria y el navegador no alcanzó a cargar la página (esa ejecución terminó cancelada por falta de memoria). Medidas tomadas para cubrir ambas: la tarea `AbrirPetStay.sinDatosPrevios()` abre la app, ejecuta `localStorage.clear()` y vuelve a cargarla (todos los escenarios empiezan por ella), y `Hooks` cierra el navegador al terminar cada escenario (`@After` → `OnStage.drawTheCurtain()`).
- Si un escenario necesita una mascota o una reserva, la crea en sus propios pasos `Given`.
- La app simula latencia con esperas aleatorias (300–1500 ms) y una capa `#capa-cargando`. Por eso las tareas esperan con `WaitUntil` (nunca `Thread.sleep`).
- Las fechas se validan contra **hoy**, así que los escenarios no usan fechas fijas: usan desplazamientos ("entrada en 2 días") que `Atm.Utils.Fechas` convierte a `DD/MM/AAAA`.

Reglas de negocio extraídas del código fuente (`js/comun.js` y cada `.html`):

| Pantalla | Regla |
|---|---|
| Login (`index.html`) | Usuario válido `dueno` / `huellitas123`; si no, "Usuario o contraseña incorrectos". Páginas privadas sin sesión → redirigen a `index.html` |
| Mis mascotas | Nombre 2–30 caracteres y único (sin distinguir mayúsculas). Peso > 0 y ≤ 80. Edad entero 0–25. Sin vacunas → no se registra. Tamaño: ≤ 10 kg Pequeño, < 25 kg Mediano, ≥ 25 kg Grande |
| Reservar | Entrada ≥ hoy; salida > entrada; máximo 30 noches; sin cruce con otra reserva confirmada de la misma mascota. Tarifa/noche: Pequeño $40.000, Mediano $55.000, Grande $70.000. Baño $25.000 por estadía. Paseo $10.000 (solo perros). ≥ 7 noches → 15% de descuento sobre el alojamiento |
| Mis reservas | Cancelación: ≥ 3 días antes 100%, 1–2 días 50%, mismo día 0% |

> **Hallazgo:** la pantalla dice "Paseo diario — $10.000 **por noche**", pero el código suma `PRECIO_PASEO` **una sola vez** por estadía. El escenario de paseo (`@Issue-9`, 2 noches → $150.000) valida el comportamiento real de la app; si la regla correcta fuera "por noche" el total esperado sería $160.000 y el caso fallaría. Es un defecto a reportar al equipo de desarrollo.

---

## 3. Diseño de casos de prueba (Gherkin) y trazabilidad

Cada historia de usuario es un **requisito** (`@HU-0X`, tag a nivel de `Feature`) y cada escenario es un **caso de prueba** con su Issue (`@Issue-N`). Además se marcan `@positivo` / `@negativo`, y `@smoketest` en los escenarios que corre el pipeline en cada push.

| Issue | Requisito | Tipo | Escenario | Feature |
|---|---|---|---|---|
| @Issue-1 | @HU-01 | Positivo · smoke | Inicio de sesión exitoso con credenciales válidas | login.feature |
| @Issue-2 | @HU-01 | Negativo | Inicio de sesión rechazado con credenciales inválidas (3 ejemplos) | login.feature |
| @Issue-3 | @HU-01 | Positivo | Cierre de sesión | login.feature |
| @Issue-4 | @HU-01 | Negativo | Acceso a una página privada sin sesión | login.feature |
| @Issue-5 | @HU-02 | Positivo · smoke | Registro exitoso y tamaño por peso (10 / 12.5 / 25 kg — valores límite) | mascotas.feature |
| @Issue-6 | @HU-02 | Negativo | Registro sin vacunas al día | mascotas.feature |
| @Issue-7 | @HU-02 | Negativo | Datos fuera de rango (nombre, peso 0, peso 80.5, peso texto, edad 26) | mascotas.feature |
| @Issue-8 | @HU-02 | Negativo | Nombre duplicado (sin distinguir mayúsculas) | mascotas.feature |
| @Issue-9 | @HU-03 | Positivo · smoke | Cálculo del total por tamaño, noches y servicios (3 ejemplos) | reservas.feature |
| @Issue-10 | @HU-03 | Positivo | Descuento 15% por 7 noches | reservas.feature |
| @Issue-11 | @HU-03 | Positivo | Confirmar reserva → código `PS-####` y estado "Confirmada" | reservas.feature |
| @Issue-12 | @HU-03 | Negativo | Fechas inválidas (entrada pasada, salida = entrada, 31 noches) | reservas.feature |
| @Issue-13 | @HU-03 | Negativo | Paseo deshabilitado para gatos | reservas.feature |
| @Issue-14 | @HU-03 | Negativo | Cruce de fechas con otra reserva activa | reservas.feature |
| @Issue-15 | @HU-04 | Positivo · smoke | Cancelación con reembolso 100% / 50% / 0% | cancelaciones.feature |
| @Issue-16 | @HU-04 | Negativo | Desistir de la cancelación → sigue "Confirmada" | cancelaciones.feature |

**Total:** 16 escenarios (8 positivos, 8 negativos) → 30 ejecuciones contando los ejemplos de los `Scenario Outline`.

Técnicas de diseño aplicadas: **valores límite** (peso 10/25 en el cambio de tamaño, 80.5 kg, 30 vs 31 noches, 7 noches para el descuento, 0/1/3 días en la política de reembolso) y **partición de equivalencia** (credenciales inválidas, datos fuera de rango).

Ejemplo (`login.feature`):

```gherkin
@HU-01
Feature: HU-01 Inicio y cierre de sesión en PetStay

  @Issue-2 @negativo
  Scenario Outline: Inicio de sesión rechazado con credenciales inválidas - <caso>
    Given acceso a la pagina de hotel para mascotas
    When ingreso nombre de "<usuario>" y "<contrasena>" e ingreso al sistema
    Then se muestra el mensaje de error de login "Usuario o contraseña incorrectos"
    Examples:
      | caso                   | usuario | contrasena   |
      | contraseña incorrecta  | dueno   | gatitos456   |
      | usuario inexistente    | admin   | huellitas123 |
      | campos vacíos          |         |              |
```

---

## 4. Implementación con el patrón Screenplay

Se respetó la estructura de paquetes que ya tenía el proyecto de clase:

```
src/main/java/Atm/
├── PO/          → Targets (localizadores). Ninguna otra capa tiene localizadores.
│   ├── PaginaLogin, EncabezadoPetStay, PaginaMascotas, PaginaReservar, PaginaMisReservas
├── task/        → Tareas con objetivo de negocio
│   ├── AbrirPetStay, Logueo, IngresarAPetStay, CerrarSesion, RegistrarMascota,
│   ├── ElegirMascotaParaReservar, CotizarReserva, ConfirmarReserva,
│   └── VerMisReservas, CancelarReserva
├── Question/    → Lo que el actor consulta de la pantalla
│   ├── ValidarDashboard, ValidarPantallaLogin, TextoVisible, Mensajes,
│   └── ResumenReserva, ServicioPaseo, MascotaRegistrada
└── Utils/       → RutasYConstantes (URLs, credenciales, timeout), Fechas
src/test/java/
├── runners/Runner.java              → CucumberWithSerenity, features + glue
└── stepdefinitions/
    ├── Hooks.java                   → @Before: OnStage.setTheStage(new OnlineCast())
    ├── LoginStepdefs, MascotasStepdefs, ReservasStepdefs
src/test/resources/
├── features/ login · mascotas · reservas · cancelaciones .feature
└── serenity.conf
```

| Pieza Screenplay | En este proyecto |
|---|---|
| **Actor** | `"Dueño"` (creado en `Hooks` con `theActorCalled`) |
| **Habilidad** | `BrowseTheWeb`, la entrega `OnlineCast` con un Chrome nuevo por escenario |
| **Interacciones** | `Open.url`, `Enter.theValue`, `Click.on`, `WaitUntil` (vienen con Serenity) |
| **Tareas** | `RegistrarMascota`, `CotizarReserva`, `ConfirmarReserva`… (agrupan interacciones) |
| **Questions** | `Mensajes.deMascotas()`, `ResumenReserva.total()`, `ServicioPaseo.estaHabilitado()`… |
| **Targets** | Clases de `Atm.PO` |

### Recorrido de un escenario (ejemplo `@Issue-11`)

```
reservas.feature  "cotiza una estadía para "Toby" con entrada en 3 días, salida en 5 días..."
   └─> ReservasStepdefs.cotiza_una_estadia(...)
         └─> actor.attemptsTo(CotizarReserva.conServicios("Toby", 3, 5, false, false))
               ├─> ElegirMascotaParaReservar  → Open.url(reservar.html) + Click en <option> "Toby (...)"
               ├─> Enter.theValue(Fechas.dentroDeDias(3)).into(PaginaReservar.fechaEntrada)
               ├─> Enter.theValue(Fechas.dentroDeDias(5)).into(PaginaReservar.fechaSalida)
               └─> Click.on(PaginaReservar.btnCalcular)
"confirma la reserva"
   └─> ConfirmarReserva → clic en "Confirmar reserva", espera [data-testid='codigo-reserva']
                          y guarda el código en la memoria del actor: actor.remember("codigoReserva", ...)
"se genera un código de reserva con formato "PS-####""
   └─> seeThat(ResumenReserva.codigoGenerado(), matchesPattern("PS-\d\d\d\d"))
"la reserva aparece en Mis reservas con estado "Confirmada""
   └─> VerMisReservas + seeThat(ResumenReserva.estadoEnMisReservas(), equalTo("Confirmada"))
```

### Decisiones técnicas

- **Tareas parametrizadas sin estado estático:** `instrumented(Clase.class, args...)` + constructor. Serenity crea la instancia y la registra en el reporte.
- **Memoria del actor (`remember/recall`):** el código `PS-####` es aleatorio; la tarea `ConfirmarReserva` lo guarda y luego `CancelarReserva` y las Questions lo recuperan. Así los pasos no comparten variables.
- **`TextoVisible`:** Question base que espera a que el elemento sea visible antes de leer su texto. Evita leer `""` de un mensaje que todavía tiene el atributo `hidden` por la latencia simulada.
- **Targets dinámicos** con `{0}`: `PaginaMascotas.especie.of("Gato")`, `PaginaMisReservas.btnCancelar.of(codigo)`, `PaginaReservar.errorCampo.of("salida")`.
- **Selección de mascota:** se hace clic en el `<option>` cuyo texto empieza por el nombre (`Toby (Perro, Mediano)`); hacer clic en la opción dispara el evento `change` que la app usa para habilitar/deshabilitar el paseo.

---

## 5. Localizadores

La app mezcla a propósito `id`, `name`, `data-testid`, clases y texto. Orden de preferencia usado: **id → name/data-testid → CSS → XPath relativo** (nunca XPath absoluto).

| Elemento | Localizador | Por qué |
|---|---|---|
| Usuario | `//input[@name='usuario']` | No tiene id; `name` es estable |
| Contraseña | `#pass-01` | Tiene id |
| Botón Ingresar | `[data-testid='btn-ingresar']` | Atributo pensado para pruebas |
| Error de login | `#error-login` | id |
| Saludo | `header.barra .saludo` | CSS: el encabezado se pinta por JS sin ids |
| Cerrar sesión | `//button[@name='cerrar-sesion']` | name |
| Nombre mascota | `#nombreMascota` | id |
| Especie | `//input[@name='especie'][@value='{0}']` | Radio identificado por su `value` |
| Peso / Edad | `input[name='peso']` / `[data-testid='edad']` | Los ids (`pw-4k`, `e-2`) parecen generados → se evitan |
| Error de un campo | `//form[@id='form-mascota']//span[@class='error-campo'][@data-campo='{0}']` | Un solo Target parametrizado para todos los campos |
| Tamaño en la tabla | `//table[@id='tabla-mascotas']//tr[td[@class='nombre' and normalize-space()='{0}']]//span[@class='etiqueta-tamano']` | Fila ubicada por el nombre de la mascota |
| Opción de mascota | `//select[@name='mascota']/option[starts-with(normalize-space(.),'{0} (')]` | Texto de la opción |
| Fechas | `#fecha-entrada` / `[data-testid='fecha-salida']` | id / data-testid |
| Servicios | `#srv-bano` / `input[name='paseo']` | id / name |
| Total / Descuento / Noches | `[data-testid='res-total']`, `res-descuento`, `res-noches` | data-testid |
| Confirmar reserva | `//aside[contains(@class,'panel-resumen')]//button[normalize-space()='Confirmar reserva']` | Sin id: se ubica por contenedor + texto |
| Código de reserva | `[data-testid='codigo-reserva']` | data-testid |
| Estado de una reserva | `//tr[td[@class='codigo' and normalize-space()='{0}']]//span[contains(@class,'estado')]` | Fila ubicada por el código |
| Botón Cancelar de una reserva | `//button[@data-codigo='{0}']` | Atributo con el código |
| Confirmar / volver en el modal | `#confirmar-cancelacion` / `//button[@data-accion='volver']` | id / atributo de datos |

**Cómo generar un localizador a mano:** clic derecho → *Inspeccionar* → elegir el atributo más estable del elemento → probarlo en la pestaña *Elements* con `Ctrl+F` (acepta CSS y XPath) y confirmar que encuentra **1 de 1** coincidencias.

---

## 6. Ejecución y reporte

```bash
# Todos los escenarios (Windows: gradlew.bat)
./gradlew clean test

# Por requisito, por issue o smoke
./gradlew clean test -Dcucumber.filter.tags="@HU-03"
./gradlew clean test -Dcucumber.filter.tags="@Issue-12"
./gradlew clean test -Dcucumber.filter.tags="@smoketest"
./gradlew clean test -Dcucumber.filter.tags="@negativo and @HU-02"
```

- `build.gradle` hace `test.finalizedBy(aggregate)` → el reporte se genera aunque haya fallos.
- `systemProperties = System.properties` → el `-Dcucumber.filter.tags` llega al runner.
- El runner es JUnit 4 (`CucumberWithSerenity`) y Gradle usa JUnit Platform, por eso se agregó `junit-vintage-engine`.
- Reporte: `target/site/serenity/index.html` (pasos narrados, capturas por acción, pestaña *Requirements* agrupada por feature/HU).

**Iteraciones de ejecución local:**

1. Proyecto de clase sin cambios: 1 escenario, 1 fallido (localizadores de OrangeHRM).
2. Primera ejecución de los 16 escenarios (cancelada por falta de memoria en el equipo): los 4 escenarios de `@HU-01` pasaron; los demás fallaron por problemas de la automatización (no de la app): selector CSS tomado como XPath y campo usuario no encontrado (ver secciones 1 y 2). Se corrigieron.
3. Primera suite completa con los cambios: la JVM no pudo arrancar por falta de memoria del equipo (`Native memory allocation (malloc) failed`), no por las pruebas. Se limitaron la memoria de Gradle, la JVM de pruebas y Chrome (sección 1).
4. **Ejecución final (11/10/2026), por requisito y acumulando un solo reporte:**

   | Requisito | Ejecuciones | Pasaron | Fallaron |
   |---|---|---|---|
   | `@HU-01` Login | 6 | 6 | 0 |
   | `@HU-02` Mascotas | 10 | 10 | 0 |
   | `@HU-03` Reservas | 10 | 10 | 0 |
   | `@HU-04` Cancelaciones | 4 | 4 | 0 |
   | **Total** | **30 (16 escenarios)** | **30** | **0** |

   El reporte de Serenity tiene 16 escenarios y todos sus pasos terminaron en `SUCCESS` (ninguno `PENDING`, `SKIPPED` ni `ERROR`).
5. **Control negativo** (para comprobar que las validaciones detectan errores): en `@Issue-9` se cambió el total esperado de `$40.000` a `$45.000`. Resultado: 3 ejecuciones, 1 fallida con `Expected: "$45.000" but: was "$40.000"`. Después se revirtió el cambio.
6. El entorno `-Denvironment=visible` se probó con `@Issue-4` (pasó).

Con poca memoria conviene ejecutar por requisito y con capturas solo en fallos:
`./gradlew clean test -Dcucumber.filter.tags="@HU-02" -Dserenity.take.screenshots=FOR_FAILURES`

---

## 7. Pipeline (GitHub Actions) y trazabilidad con Issues

Archivo: `.github/workflows/ci.yml` (basado en el `ci.yml` entregado en el curso, adaptado de Karate a Serenity):

| Cambio | Antes (Karate) | Ahora (Serenity) |
|---|---|---|
| Filtro de tags | `-Dkarate.options="--tags $tag"` | `-Dcucumber.filter.tags="$tag"` |
| Reporte publicado | `target/karate-reports` | `target/site/serenity` (artefacto `serenity-reports`) |
| Resultados JUnit | `target/test-results` | `build/test-results/test` |

Funcionamiento:

1. **push / pull_request** a `main` o `develop` → corre `@smoketest` (4 escenarios, uno por HU).
2. **workflow_dispatch** (Actions → *Run workflow*) → corre el tag escrito en `testTag`; vacío = todos.
3. Si el tag es `@Issue-N`, el pipeline extrae `N`, comenta el resultado en el Issue #N y le pone el label `status:passed` o `status:failed`. Esto es la **ejecución del escenario desde el TestCase**.

Trazabilidad completa:

```
Issue #7 (GitHub Project)  ←→  @Issue-7  ←→  Scenario Outline "Registro rechazado por datos fuera de rango"
                                @HU-02   ←→  Feature "HU-02 Registro de mascotas"  (requisito)
```

`scripts/crear_issues.sh` crea los labels (`HU-0X`, `positivo/negativo`, `test-case`, `status:*`) y los 16 Issues **en orden** (para que el Issue #N corresponda a `@Issue-N`), con el Gherkin del escenario en el cuerpo, y los agrega al GitHub Project.

### Pasos para publicar

```bash
# 1. Crear el repositorio vacío en GitHub (sin README para no ocupar números) y subir el código
git remote add origin https://github.com/<usuario>/<repo>.git
git push -u origin main

# 2. Crear el Project (Projects → New project) y vincularlo al repositorio (Project → Settings → Linked repositories)

# 3. Crear los Issues y agregarlos al Project
gh auth login && gh auth refresh -s project
bash scripts/crear_issues.sh <usuario> <numero-del-project>

# 4. Ejecutar un caso desde su Issue: Actions → CI / CD Pipeline → Run workflow → testTag = @Issue-5
```

---

## 8. Resumen de cumplimiento

| Requisito del taller | Cómo se cumple |
|---|---|
| 5.1 ≥ 10 escenarios con Issue, positivos y negativos | 16 escenarios (8 + / 8 −), cada uno con `@Issue-N` |
| 5.2 Código en repositorio | Repositorio git listo para `push` (wrapper incluido) |
| 5.3 Despliegue en GitHub Actions | `.github/workflows/ci.yml` adaptado a Serenity |
| 5.4 TestCases con Issues vinculados | Tags `@Issue-N` + `@HU-0X`, script de creación de Issues + Project |
| 4.4 Tags de requisito | `@HU-01`…`@HU-04` a nivel de Feature |
| Casos funcionando | 30/30 ejecuciones en verde localmente (sección 6) |
