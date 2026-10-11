# Guía de presentación — PetStay con Serenity BDD

Guía para estudiar el framework y demostrar en la sustentación que el proyecto funciona.
El detalle técnico completo del desarrollo está en [DOCUMENTACION_PROCESO.md](DOCUMENTACION_PROCESO.md).

---

## 1. Cómo funciona el framework (en 2 minutos)

```
gradlew test
   └─ Gradle (JUnit Platform + junit-vintage-engine)
        └─ runners/Runner.java  @RunWith(CucumberWithSerenity)
             ├─ lee  src/test/resources/features/*.feature   (Gherkin: QUÉ se prueba)
             ├─ filtra por tags  -Dcucumber.filter.tags="@Issue-11"
             └─ por cada paso busca su método en  stepdefinitions/   (glue)
                  └─ el Actor "Dueño" ejecuta Tasks y hace Questions   (Screenplay: CÓMO)
                       ├─ Tasks    → Interactions (Click, Enter, Open…) → WebDriver → Chrome
                       ├─ Targets  (Atm/PO) → localizadores CSS / XPath
                       └─ Questions → leen la página → assert con Hamcrest (seeThat)
   └─ aggregate → target/site/serenity/index.html   (reporte con pasos y capturas)
```

| Capa | Carpeta | Responsabilidad | Ejemplo |
|---|---|---|---|
| Feature (Gherkin) | `src/test/resources/features` | Qué hace el usuario y qué espera, en lenguaje de negocio | `reservas.feature` |
| Step definitions | `src/test/java/stepdefinitions` | Traducen cada frase a una llamada del actor; **no** tienen localizadores | `ReservasStepdefs` |
| Hooks | `stepdefinitions/Hooks.java` | `@Before`: crea el actor con un navegador nuevo. `@After`: cierra el navegador | `OnStage.setTheStage(new OnlineCast())` |
| Tasks | `src/main/java/Atm/task` | Acciones de negocio compuestas de interacciones | `CotizarReserva`, `ConfirmarReserva` |
| Targets (PO) | `src/main/java/Atm/PO` | Un único lugar por página con los localizadores | `PaginaReservar.resTotal` |
| Questions | `src/main/java/Atm/Question` | Leen el estado de la pantalla para validar | `ResumenReserva.total()` |
| Utils | `src/main/java/Atm/Utils` | URLs, credenciales, fechas relativas a hoy | `Fechas.dentroDeDias(3)` |

**Por qué Screenplay y no Page Object clásico:** el actor *tiene habilidades* (`BrowseTheWeb`), *realiza tareas* y *hace preguntas*. Las tareas se reutilizan (por ejemplo, `IngresarAPetStay` sirve de precondición en 3 features), los pasos se leen como el negocio y el reporte narra cada acción ("Dueño clicks on el botón Confirmar reserva").

### Recorrido de un escenario: `@Issue-11` (confirmar reserva)

| Paso Gherkin | Step definition | Screenplay |
|---|---|---|
| `Given el dueño inició sesión en PetStay` | `LoginStepdefs` | `IngresarAPetStay.comoDueno()` → `AbrirPetStay` (limpia localStorage) + `Logueo` + espera el saludo |
| `And tiene registrada la mascota "Toby"…` | `MascotasStepdefs` | `RegistrarMascota.conDatos(...)` |
| `When cotiza una estadía para "Toby"…` | `ReservasStepdefs` | `CotizarReserva.conServicios(...)` → elige mascota, escribe fechas (`Fechas.dentroDeDias`), clic en Calcular |
| `And confirma la reserva` | `ReservasStepdefs` | `ConfirmarReserva.cotizada()` → clic, espera el código y lo guarda: `actor.remember("codigoReserva", …)` |
| `Then se genera un código… "PS-####"` | `ReservasStepdefs` | `seeThat(ResumenReserva.codigoGenerado(), matchesPattern("PS-\d\d\d\d"))` |
| `And la reserva aparece… "Confirmada"` | `ReservasStepdefs` | `VerMisReservas` + `ResumenReserva.estadoEnMisReservas()` busca la fila **con ese código** |

La memoria del actor (`remember` / `recall`) es lo que permite que un paso posterior use un dato que se generó en la ejecución.

### Cómo se validan los resultados (no solo "que no falle")

Cada `Then` compara un valor **leído de la pantalla** con el valor esperado del Gherkin usando un matcher de Hamcrest:

- `equalTo("$190.000")` → total exacto calculado por la app (tarifa × noches + servicios).
- `equalTo("-$42.000")` → descuento del 15 % a partir de 7 noches.
- `matchesPattern("PS-\d\d\d\d")` → formato del código.
- `containsString("Reembolso: $20.000")` → política de reembolso.
- `isEnabled()` (`ServicioPaseo`) → el paseo está deshabilitado para gatos.

Si el valor no coincide, Serenity marca el escenario **FAILED**, guarda la captura de pantalla y muestra *Expected / but was* en el reporte.

---

## 2. Cómo ejecutar

```bash
# Windows: gradlew.bat   |   Linux/Mac/Git Bash: ./gradlew
gradlew.bat clean test                                         # los 16 escenarios (30 ejecuciones)
gradlew.bat clean test -Dcucumber.filter.tags="@smoketest"     # 1 por requisito (lo que corre el pipeline en push)
gradlew.bat clean test -Dcucumber.filter.tags="@HU-03"         # un requisito
gradlew.bat clean test -Dcucumber.filter.tags="@Issue-11"      # un caso de prueba (Issue)
gradlew.bat clean test -Dcucumber.filter.tags="@negativo and @HU-02"

# Navegador visible para la demo en vivo
gradlew.bat clean test -Dcucumber.filter.tags="@Issue-11" -Denvironment=visible
```

Reporte: abrir `target/site/serenity/index.html`. Copia del último reporte completo (30/30 en verde, 11/10/2026): `evidencias/reporte-serenity-30-de-30/index.html` (no se sube a git).

> **Equipo con poca memoria:** si aparece `There is insufficient memory for the Java Runtime Environment`, cerrar navegadores/pestañas y ejecutar por requisito con capturas solo en fallos:
> `gradlew.bat clean test -Dcucumber.filter.tags="@HU-02" -Dserenity.take.screenshots=FOR_FAILURES`

---

## 3. Guion de la sustentación (10–15 min)

Antes de entrar: tener abiertos el repositorio en GitHub, el Project, la pestaña Actions, el IDE y un reporte ya generado (por si falla la red).

1. **Requisitos y casos (2 min).** Abrir `features/`. Mostrar un feature: historia de usuario (Como / Quiero / Para), tag `@HU-0X` del requisito a nivel de Feature y `@Issue-N` + `@positivo/@negativo` por escenario. Mostrar un `Scenario Outline` con su tabla `Examples` (valores límite: peso 10 → Pequeño, 12.5 → Mediano, 25 → Grande).
2. **Trazabilidad (2 min).** En GitHub Project abrir el Issue #11 → mismo Gherkin, labels `HU-03`, `positivo`, `test-case`. Explicar: *Issue #11 ↔ `@Issue-11` ↔ escenario ↔ `@HU-03` ↔ Feature (requisito)*.
3. **Código Screenplay (3 min).** Seguir `@Issue-11` como en la tabla de arriba: paso → step definition → Task → Target → Question. Mostrar `actor.remember`/`recall`.
4. **Ejecución en vivo (3 min).** `gradlew.bat clean test -Dcucumber.filter.tags="@Issue-11" -Denvironment=visible` → se ve Chrome haciendo el flujo. Abrir el reporte: pasos narrados, capturas y la pestaña *Requirements* agrupada por HU.
5. **Pipeline (2 min).** Actions → *CI / CD Pipeline* → *Run workflow* → `testTag = @Issue-11`. Mostrar que al terminar el Issue #11 recibe un comentario y el label `status:passed`, y que el artefacto `serenity-reports` se puede descargar. Mencionar que en cada push corre `@smoketest`.
6. **Prueba de que las validaciones sirven (1 min).** Ver sección 4.
7. **Localizadores a mano (si lo pide).** Ver sección 5.

---

## 4. Demostrar que las pruebas detectan errores

Una prueba que nunca falla no prueba nada. Para demostrarlo en vivo:

1. En `reservas.feature`, ejemplo "gato pequeño sin servicios", cambiar `$40.000` por `$45.000`.
2. Ejecutar `gradlew.bat clean test -Dcucumber.filter.tags="@Issue-9"`.
3. Resultado: ese ejemplo queda **FAILED**; el reporte muestra
   `Expected: "$45.000" but: was "$40.000"` con la captura de la pantalla de resumen. Los otros dos ejemplos pasan.
4. Revertir el cambio (`git checkout -- src/test/resources/features/reservas.feature`).

Lo mismo con un negativo: cambiar el texto del error esperado en `@Issue-12` y ver que falla con el mensaje real de la app.

**Hallazgo para mencionar:** la etiqueta del paseo dice "$10.000 por noche", pero la app lo cobra una sola vez por estadía (2 noches con paseo = $140.000 + $10.000 = $150.000). El escenario valida el comportamiento actual y el defecto está documentado en `DOCUMENTACION_PROCESO.md` §2; en un proyecto real se reportaría como bug.

---

## 5. Escribir localizadores a mano

En Chrome: clic derecho → **Inspeccionar** → `Ctrl+F` en la pestaña *Elements* → escribir el selector → debe decir **1 of 1**.

Orden de preferencia: `id` → `data-testid` → `name` → atributo estable → texto visible → estructura.

| Elemento | CSS | XPath |
|---|---|---|
| Usuario | `input[name='usuario']` | `//input[@name='usuario']` |
| Contraseña | `#pass-01` | `//input[@id='pass-01']` |
| Botón Ingresar | `[data-testid='btn-ingresar']` | `//button[@data-testid='btn-ingresar']` |
| Error de login | `#error-login` | `//*[@id='error-login']` |
| Total de la reserva | `[data-testid='res-total']` | `//*[@data-testid='res-total']` |
| Botón por texto | — (CSS no filtra por texto) | `//button[normalize-space()='Confirmar reserva']` |
| Fila por contenido | — | `//tr[td[@class='codigo' and normalize-space()='PS-1234']]//span[contains(@class,'estado')]` |


Sintaxis clave:
- CSS: `#id`, `.clase`, `[atributo='valor']`, `padre hijo`, `padre > hijo`.
- XPath: `//etiqueta[@atributo='valor']`, `[contains(@class,'x')]`, `[normalize-space()='texto']`, `//a//b` (descendiente), `[td[...]]` (filtro por hijo).
- En Serenity, un `Target` con `css:` usa CSS; sin prefijo y empezando con `//` usa XPath. **Un selector CSS con `[...]` sin prefijo `css:` lo interpreta como XPath y falla** (fue uno de los errores corregidos).
- `{0}` en un Target es un parámetro: `estadoReserva.of(codigo)`.

---

## 6. Preguntas probables

| Pregunta | Respuesta corta |
|---|---|
| ¿Diferencia entre Task e Interaction? | La Interaction es atómica (Click, Enter). La Task agrupa interacciones con significado de negocio (CotizarReserva). |
| ¿Qué es una Question? | Un objeto que el actor "pregunta" a la UI y devuelve un valor para comparar en `seeThat`. |
| ¿Por qué `instrumented(...)`? | Serenity crea la Task con un proxy para registrarla como paso en el reporte. |
| ¿Cómo se independizan los escenarios? | Navegador nuevo por escenario (`restart.browser.for.each = scenario`) y `localStorage.clear()` al abrir la app, porque PetStay guarda todo en localStorage. |
| ¿Cómo manejan fechas si la app valida contra "hoy"? | Los escenarios usan días relativos ("entrada en 3 días") y `Fechas.dentroDeDias` calcula la fecha real. |
| ¿Esperas? | `WaitUntil` explícito sobre el overlay `#capa-cargando` y los mensajes (la app tiene un retardo aleatorio de 300–1500 ms). |
| ¿Qué técnica de diseño de casos? | Particiones de equivalencia y valores límite (peso 10/12.5/25/80.5, 7 noches, 30 noches, días de anticipación 0/1/3+). |
| ¿Cómo se ejecuta un caso desde su Issue? | `workflow_dispatch` con `testTag=@Issue-N`; el pipeline extrae N, comenta el resultado en el Issue y pone `status:passed/failed`. |
| ¿Por qué headless? | GitHub Actions no tiene pantalla. Localmente se puede ver con `-Denvironment=visible`. |

---

## 7. Checklist antes de presentar

- [ ] `gradlew.bat clean test` en verde localmente y reporte generado.
- [ ] Repositorio en GitHub con el código (incluye `gradlew`, `gradle/wrapper`, `.github/workflows/ci.yml`).
- [ ] Project creado y vinculado al repositorio, con los 16 Issues (`scripts/crear_issues.sh`).
- [ ] Al menos una ejecución verde en Actions por push (`@smoketest`) y una manual con `@Issue-N` que comentó el Issue.
- [ ] Saber explicar el recorrido de `@Issue-11` y escribir 3 localizadores a mano.
