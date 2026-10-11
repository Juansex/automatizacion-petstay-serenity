@HU-01
Feature: HU-01 Inicio y cierre de sesión en PetStay
  Como dueño de una mascota
  Quiero iniciar sesión en PetStay
  Para gestionar las mascotas y reservas de mi cuenta

  @Issue-1 @positivo @smoketest
  Scenario: Inicio de sesión exitoso con credenciales válidas
    Given acceso a la pagina de hotel para mascotas
    When ingreso nombre de "dueno" y "huellitas123" e ingreso al sistema
    Then valido que haya ingresado
    And el sistema saluda al usuario con "Hola, dueno"

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

  @Issue-3 @positivo
  Scenario: Cierre de sesión
    Given el dueño inició sesión en PetStay
    When cierra la sesión
    Then regresa a la pantalla de inicio de sesión

  @Issue-4 @negativo
  Scenario: Acceso a una página privada sin haber iniciado sesión
    Given un visitante sin sesión intenta abrir la página de mis mascotas
    Then regresa a la pantalla de inicio de sesión
