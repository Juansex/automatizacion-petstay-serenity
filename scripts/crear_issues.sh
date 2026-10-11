#!/usr/bin/env bash
# Crea los 16 Issues (casos de prueba) en el repositorio de GitHub, en el MISMO ORDEN
# que los tags @Issue-N de los .feature, y los agrega al GitHub Project.
#
# Requisitos: GitHub CLI autenticado (gh auth login) con scope "project"
#   gh auth refresh -s project
# Uso (desde la raíz del repo):
#   bash scripts/crear_issues.sh <owner> <numero-del-project>
# Ejemplo:
#   bash scripts/crear_issues.sh mi-usuario 1
#
# IMPORTANTE: ejecutar sobre un repositorio sin issues ni PRs previos, para que
# GitHub asigne los números 1..16 y coincidan con @Issue-1..@Issue-16.

set -euo pipefail

OWNER="${1:?Falta el owner (usuario u organización)}"
PROJECT_NUMBER="${2:?Falta el número del GitHub Project}"

# Labels de requisito, tipo y estado (el pipeline usa status:passed / status:failed)
crear_label() { gh label create "$1" --color "$2" --description "$3" --force >/dev/null; }
crear_label "HU-01" "1d76db" "Requisito: inicio y cierre de sesión"
crear_label "HU-02" "0e8a16" "Requisito: registro de mascotas"
crear_label "HU-03" "fbca04" "Requisito: reserva de estadías"
crear_label "HU-04" "d93f0b" "Requisito: cancelación de reservas"
crear_label "positivo" "c2e0c6" "Escenario positivo (camino feliz)"
crear_label "negativo" "f9d0c4" "Escenario negativo (validación / error)"
crear_label "test-case" "5319e7" "Caso de prueba automatizado con Serenity BDD"
crear_label "status:passed" "0e8a16" "Última ejecución en GitHub Actions: exitosa"
crear_label "status:failed" "b60205" "Última ejecución en GitHub Actions: fallida"

crear_issue() {
  local numero="$1" hu="$2" tipo="$3" feature="$4" titulo="$5" escenario="$6"
  local cuerpo
  cuerpo=$(cat <<EOF
**Requisito:** ${hu}
**Tipo:** ${tipo}
**Feature:** \`src/test/resources/features/${feature}\`
**Tags del escenario:** \`@${hu}\` \`@Issue-${numero}\` \`@${tipo}\`

### Escenario Gherkin
\`\`\`gherkin
${escenario}
\`\`\`

### Ejecución desde el TestCase
Actions → *CI / CD Pipeline* → **Run workflow** → testTag = \`@Issue-${numero}\`.
El pipeline ejecuta solo este escenario, comenta el resultado aquí y pone el label \`status:passed\` o \`status:failed\`.
EOF
)
  local url
  url=$(gh issue create --title "[${hu}] ${titulo}" --body "$cuerpo" --label "${hu},${tipo},test-case")
  echo "Issue #${numero} -> ${url}"
  gh project item-add "$PROJECT_NUMBER" --owner "$OWNER" --url "$url" >/dev/null
}

crear_issue 1 HU-01 positivo login.feature "Inicio de sesión exitoso con credenciales válidas" \
"Scenario: Inicio de sesión exitoso con credenciales válidas
  Given acceso a la pagina de hotel para mascotas
  When ingreso nombre de \"dueno\" y \"huellitas123\" e ingreso al sistema
  Then valido que haya ingresado
  And el sistema saluda al usuario con \"Hola, dueno\""

crear_issue 2 HU-01 negativo login.feature "Inicio de sesión rechazado con credenciales inválidas" \
"Scenario Outline: Inicio de sesión rechazado con credenciales inválidas
  Given acceso a la pagina de hotel para mascotas
  When ingreso nombre de \"<usuario>\" y \"<contrasena>\" e ingreso al sistema
  Then se muestra el mensaje de error de login \"Usuario o contraseña incorrectos\"
  (Ejemplos: contraseña incorrecta, usuario inexistente, campos vacíos)"

crear_issue 3 HU-01 positivo login.feature "Cierre de sesión" \
"Scenario: Cierre de sesión
  Given el dueño inició sesión en PetStay
  When cierra la sesión
  Then regresa a la pantalla de inicio de sesión"

crear_issue 4 HU-01 negativo login.feature "Acceso a una página privada sin haber iniciado sesión" \
"Scenario: Acceso a una página privada sin haber iniciado sesión
  Given un visitante sin sesión intenta abrir la página de mis mascotas
  Then regresa a la pantalla de inicio de sesión"

crear_issue 5 HU-02 positivo mascotas.feature "Registro exitoso de mascota y asignación de tamaño por peso" \
"Scenario Outline: Registro exitoso de una mascota y asignación de tamaño por peso
  When registra la mascota \"<nombre>\" de especie \"<especie>\" con peso \"<peso>\" kg, edad \"<edad>\" años y vacunas al día \"si\"
  Then se muestra el mensaje en mascotas \"<nombre> fue registrado con tamaño <tamano>\"
  And la mascota \"<nombre>\" aparece en la lista con tamaño \"<tamano>\"
  (Ejemplos: 10 kg -> Pequeño, 12.5 kg -> Mediano, 25 kg -> Grande)"

crear_issue 6 HU-02 negativo mascotas.feature "Registro rechazado de una mascota sin vacunas al día" \
"Scenario: Registro rechazado de una mascota sin vacunas al día
  When registra la mascota \"Michi\" de especie \"Gato\" con peso \"4\" kg, edad \"1\" años y vacunas al día \"no\"
  Then se muestra el mensaje en mascotas \"No podemos hospedar mascotas sin vacunas al día\""

crear_issue 7 HU-02 negativo mascotas.feature "Registro rechazado por datos fuera de rango" \
"Scenario Outline: Registro rechazado por datos fuera de rango
  When registra la mascota \"<nombre>\" de especie \"Perro\" con peso \"<peso>\" kg, edad \"<edad>\" años y vacunas al día \"si\"
  Then se muestra el mensaje en mascotas \"Revisa los campos marcados\"
  And el campo \"<campo>\" de la mascota muestra el error \"<error>\"
  (Ejemplos: nombre corto, peso 0, peso 80.5, peso no numérico, edad 26)"

crear_issue 8 HU-02 negativo mascotas.feature "Registro rechazado de una mascota con nombre duplicado" \
"Scenario: Registro rechazado de una mascota con nombre duplicado
  Given tiene registrada la mascota \"Toby\" de especie \"Perro\" con peso \"12\" kg
  When registra la mascota \"toby\" de especie \"Perro\" con peso \"8\" kg, edad \"2\" años y vacunas al día \"si\"
  Then el campo \"nombre\" de la mascota muestra el error \"Ya existe una mascota registrada con ese nombre\""

crear_issue 9 HU-03 positivo reservas.feature "Cálculo del total según tamaño, noches y servicios" \
"Scenario Outline: Cálculo del total según tamaño, noches y servicios adicionales
  Given tiene registrada la mascota \"<nombre>\" de especie \"<especie>\" con peso \"<peso>\" kg
  When cotiza una estadía para \"<nombre>\" con entrada en 2 días, salida en <salida> días, baño \"<bano>\" y paseo \"<paseo>\"
  Then las noches calculadas son <noches>
  And el total de la reserva es \"<total>\""

crear_issue 10 HU-03 positivo reservas.feature "Descuento del 15% por estadía larga" \
"Scenario: Descuento del 15% por estadía larga de 7 noches o más
  Given tiene registrada la mascota \"Luna\" de especie \"Gato\" con peso \"4\" kg
  When cotiza una estadía para \"Luna\" con entrada en 1 días, salida en 8 días, baño \"no\" y paseo \"no\"
  Then las noches calculadas son 7
  And el descuento por estadía larga es \"-\$42.000\"
  And el total de la reserva es \"\$238.000\""

crear_issue 11 HU-03 positivo reservas.feature "Confirmación de una reserva y consulta en Mis reservas" \
"Scenario: Confirmación de una reserva y consulta en Mis reservas
  Given tiene registrada la mascota \"Toby\" de especie \"Perro\" con peso \"12\" kg
  When cotiza una estadía para \"Toby\" con entrada en 3 días, salida en 5 días, baño \"no\" y paseo \"no\"
  And confirma la reserva
  Then se genera un código de reserva con formato \"PS-####\"
  And la reserva aparece en Mis reservas con estado \"Confirmada\""

crear_issue 12 HU-03 negativo reservas.feature "Cotización rechazada por fechas inválidas" \
"Scenario Outline: Cotización rechazada por fechas inválidas
  Given tiene registrada la mascota \"Toby\" de especie \"Perro\" con peso \"12\" kg
  When cotiza una estadía para \"Toby\" con entrada en <entrada> días, salida en <salida> días, baño \"no\" y paseo \"no\"
  Then se muestra el mensaje de reserva \"Revisa los campos marcados\"
  And el campo \"<campo>\" de la reserva muestra el error \"<error>\"
  (Ejemplos: entrada anterior a hoy, salida igual a la entrada, 31 noches)"

crear_issue 13 HU-03 negativo reservas.feature "El servicio de paseo no está disponible para gatos" \
"Scenario: El servicio de paseo diario no está disponible para gatos
  Given tiene registrada la mascota \"Luna\" de especie \"Gato\" con peso \"4\" kg
  When elige a \"Luna\" en el formulario de reserva
  Then el servicio de paseo no está habilitado
  And se informa que \"El paseo solo está disponible para perros.\""

crear_issue 14 HU-03 negativo reservas.feature "Reserva rechazada por cruce de fechas" \
"Scenario: Reserva rechazada por cruce de fechas con otra reserva activa
  Given tiene registrada la mascota \"Toby\" de especie \"Perro\" con peso \"12\" kg
  And tiene una reserva confirmada para \"Toby\" con entrada en 5 días y salida en 8 días
  When cotiza una estadía para \"Toby\" con entrada en 6 días, salida en 10 días, baño \"no\" y paseo \"no\"
  Then se muestra el mensaje de reserva \"Esta mascota ya tiene una reserva activa en esas fechas\""

crear_issue 15 HU-04 positivo cancelaciones.feature "Cancelación con reembolso según la anticipación" \
"Scenario Outline: Cancelación con reembolso según la anticipación
  Given tiene una reserva confirmada para \"Luna\" con entrada en <entrada> días y salida en <salida> días
  When cancela la reserva confirmando la acción
  Then se muestra el mensaje de cancelación con \"Reembolso: <reembolso>\"
  And la reserva aparece en Mis reservas con estado \"Cancelada\"
  (Ejemplos: 5 días -> 100%, 1 día -> 50%, mismo día -> 0%)"

crear_issue 16 HU-04 negativo cancelaciones.feature "El dueño desiste de cancelar y la reserva sigue confirmada" \
"Scenario: El dueño desiste de cancelar y la reserva sigue confirmada
  Given tiene una reserva confirmada para \"Luna\" con entrada en 5 días y salida en 6 días
  When inicia la cancelación de la reserva pero decide no cancelar
  Then la reserva aparece en Mis reservas con estado \"Confirmada\""

echo "Listo: 16 issues creados y agregados al Project #${PROJECT_NUMBER}."
