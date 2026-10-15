# Automatización de pruebas Front-End con Serenity BDD — PetStay

Automatización de pruebas de interfaz de la aplicación **PetStay – Hotel para Mascotas** (`https://petstay-aphn.onrender.com`) con Serenity BDD, Cucumber y el patrón Screenplay.

- **Stack:** Java 17+, Gradle (wrapper), Serenity BDD 4.2.32, Cucumber, Selenium WebDriver (Chrome), GitHub Actions
- **Casos:** 14 escenarios (7 positivos, 7 negativos) sobre 4 historias de usuario, 28 ejecuciones contando las filas de `Examples`

## Historias de usuario y casos

| Requisito | Feature | Issues |
|---|---|---|
| HU-01 Inicio y cierre de sesión | `login.feature` | #1, #2, #3 |
| HU-02 Registro de mascotas | `mascotas.feature` | #5, #6, #7, #8 |
| HU-03 Reserva de estadías | `reservas.feature` | #9, #10, #11, #12, #14 |
| HU-04 Cancelación de reservas | `cancelaciones.feature` | #15, #16 |

| Issue | Tipo | Escenario |
|---|---|---|
| #1 | positivo | Inicio de sesión exitoso con credenciales válidas |
| #2 | negativo | Inicio de sesión rechazado con credenciales inválidas (3 ejemplos) |
| #3 | positivo | Cierre de sesión |
| #5 | positivo | Registro exitoso y asignación de tamaño por peso (10 / 12.5 / 25 kg) |
| #6 | negativo | Registro rechazado sin vacunas al día |
| #7 | negativo | Registro rechazado por datos fuera de rango (5 ejemplos) |
| #8 | negativo | Registro rechazado por nombre duplicado |
| #9 | positivo | Cálculo del total según tamaño, noches y servicios (3 ejemplos) |
| #10 | positivo | Descuento del 15 % por estadía de 7 noches o más |
| #11 | positivo | Confirmación de una reserva y consulta en Mis reservas |
| #12 | negativo | Cotización rechazada por fechas inválidas (3 ejemplos) |
| #14 | negativo | Reserva rechazada por cruce de fechas con otra reserva activa |
| #15 | positivo | Cancelación con reembolso según la anticipación (100 % / 50 % / 0 %) |
| #16 | negativo | El dueño desiste de cancelar y la reserva sigue confirmada |

Técnicas de diseño: particiones de equivalencia (credenciales, datos fuera de rango) y valores límite (peso 10/25 en el cambio de tamaño, 80.5 kg, 7 noches para el descuento, 30 noches como máximo, 0/1/3 días en la política de reembolso).

## Estructura (Screenplay)

```
.github/workflows/ci.yml          Pipeline de GitHub Actions
src/main/java/Atm/
  PO/          Targets (localizadores) por pantalla: PaginaLogin, PaginaMascotas, PaginaReservar,
               PaginaMisReservas, EncabezadoPetStay
  task/        Tareas de negocio: AbrirPetStay, Logueo, IngresarAPetStay, CerrarSesion, RegistrarMascota,
               ElegirMascotaParaReservar, CotizarReserva, ConfirmarReserva, VerMisReservas, CancelarReserva
  Question/    Consultas sobre la pantalla: Mensajes, ResumenReserva, MascotaRegistrada, TextoVisible,
               ValidarDashboard, ValidarPantallaLogin
  Utils/       RutasYConstantes (URLs, credenciales, timeout) y Fechas (fechas relativas a hoy)
src/test/java/
  runners/Runner.java               CucumberWithSerenity: features + glue
  stepdefinitions/                  Hooks (actor y navegador por escenario) y steps de Login, Mascotas y Reservas
src/test/resources/
  features/                         login, mascotas, reservas, cancelaciones
  serenity.conf                     Navegador, capturas y carpeta del reporte
```

| Elemento Screenplay | Implementación |
|---|---|
| Actor | "Dueño", creado en `Hooks` |
| Habilidad | `BrowseTheWeb`, entregada por `OnlineCast` con un navegador nuevo por escenario |
| Interacciones | `Open.url`, `Enter.theValue`, `Click.on`, `WaitUntil` |
| Tareas | Clases de `Atm.task`, parametrizadas con `instrumented(...)` |
| Questions | Clases de `Atm.Question`, validadas con `seeThat` y matchers de Hamcrest |
| Targets | Clases de `Atm.PO` |

Decisiones relevantes:

- PetStay guarda sesión, mascotas y reservas en el `localStorage`. Cada escenario abre la app, limpia el `localStorage` (`AbrirPetStay`) y usa un navegador nuevo (`serenity.restart.browser.for.each = scenario`), así los escenarios son independientes.
- La app simula latencia (300–1500 ms). Las tareas usan `WaitUntil` y `TextoVisible` espera a que el mensaje sea visible antes de leerlo.
- Las fechas se validan contra el día actual, por eso los escenarios usan desplazamientos ("entrada en 3 días") que `Fechas.dentroDeDias` convierte a `DD/MM/AAAA`.
- El código de reserva es aleatorio: `ConfirmarReserva` lo guarda con `actor.remember(...)` y las tareas y preguntas posteriores lo recuperan con `recall`.
- Localizadores: se prefiere `id`, luego `name` / `data-testid`, luego CSS y por último XPath relativo. Los selectores CSS llevan el prefijo `css:`.

## Trazabilidad

| Tag | Nivel | Uso |
|---|---|---|
| `@HU-01`..`@HU-04` | Feature | Requisito (historia de usuario) al que pertenecen los escenarios |
| `@Issue-N` | Scenario | Vincula el escenario con el Issue #N del repositorio |
| `@positivo` / `@negativo` | Scenario | Tipo de caso |
| `@smoketest` | Scenario | Casos que corren en cada push (#1, #5, #9, #15) |

Los Issues están en el GitHub Project **TestCases**, vinculado al repositorio. Cada Issue contiene el escenario Gherkin y los labels de la historia y del tipo.

## Pipeline

`.github/workflows/ci.yml`:

- **push / pull request** a `main` o `develop`: ejecuta `@smoketest`.
- **Ejecución manual** (`workflow_dispatch`): ejecuta el tag indicado en `testTag`. Si el tag es `@Issue-N`, comenta el resultado en el Issue #N y le asigna el label `status:passed` o `status:failed`.
- Chrome corre en modo headless. Publica los artefactos `serenity-reports` y `test-results`.

---

# Manual de uso

## Requisitos

- JDK 17 o superior
- Google Chrome (el driver se descarga automáticamente)
- Conexión a internet (la app está publicada en Render; si estuvo inactiva, la primera carga puede tardar)
- No hace falta instalar Gradle: se usa `gradlew` / `gradlew.bat`

## Ejecutar en local

Windows (PowerShell). Las comillas deben envolver todo el argumento `-D...`:

```powershell
.\gradlew.bat clean test                                                 # todos los escenarios
.\gradlew.bat clean test "-Dcucumber.filter.tags=@smoketest"             # smoke
.\gradlew.bat clean test "-Dcucumber.filter.tags=@Issue-11"              # un caso
.\gradlew.bat clean test "-Dcucumber.filter.tags=@HU-03"                 # una historia de usuario
.\gradlew.bat clean test "-Dcucumber.filter.tags=@negativo and @HU-02"   # combinación de tags
```

Linux / macOS / Git Bash:

```bash
./gradlew clean test -Dcucumber.filter.tags="@Issue-11"
```

Navegador:

| Opción | Argumento |
|---|---|
| Chrome headless (por defecto) | — |
| Chrome con ventana visible | `"-Denvironment=visible"` |
| Edge | `"-Denvironment=edge"` |
| Firefox | `"-Denvironment=firefox"` |

En equipos con poca memoria conviene ejecutar por historia de usuario y con capturas solo en fallos:
`.\gradlew.bat clean test "-Dcucumber.filter.tags=@HU-02" "-Dserenity.take.screenshots=FOR_FAILURES"`

## Ver resultados

`target/site/serenity/index.html`: pasos narrados por el actor, captura por acción y pestaña *Requirements* agrupada por historia. El reporte se genera aunque haya fallos (`test.finalizedBy(aggregate)`).

## Ejecutar un caso desde GitHub

1. **Actions** → **CI / CD Pipeline** → **Run workflow**.
2. Rama `main`, `testTag` = `@Issue-11` (o el número del caso) → **Run workflow**.
3. Al terminar, el Issue #11 tiene un comentario con el resultado y el enlace a la corrida, y el label `status:passed` o `status:failed`.
4. En la corrida, sección **Artifacts**, descargar `serenity-reports` y abrir `index.html`.

Con GitHub CLI:

```bash
gh workflow run ci.yml -f testTag=@Issue-11
gh run watch
gh issue view 11 --comments
```

## Agregar un caso

1. Localizadores nuevos en `Atm/PO`, la acción en `Atm/task` y lo que se valida en `Atm/Question`.
2. Steps en `src/test/java/stepdefinitions`.
3. Escenario en el `.feature` de su historia con `@Issue-N` y `@positivo` / `@negativo`.
4. Ejecutarlo en local con `"-Dcucumber.filter.tags=@Issue-N"`.
5. Crear el Issue en GitHub con el mismo número del tag (GitHub no reutiliza números; el siguiente libre es el 17) y agregarlo al Project TestCases.
