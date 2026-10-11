@HU-02
Feature: HU-02 Registro de mascotas
  Como dueño de una mascota
  Quiero registrar a mis mascotas con sus datos
  Para poder reservarles una estadía en el hotel

  Background:
    Given el dueño inició sesión en PetStay

  @Issue-5 @positivo @smoketest
  Scenario Outline: Registro exitoso de una mascota y asignación de tamaño por peso - <peso> kg
    When registra la mascota "<nombre>" de especie "<especie>" con peso "<peso>" kg, edad "<edad>" años y vacunas al día "si"
    Then se muestra el mensaje en mascotas "<nombre> fue registrado con tamaño <tamano>"
    And la mascota "<nombre>" aparece en la lista con tamaño "<tamano>"
    Examples:
      | nombre | especie | peso | edad | tamano  |
      | Luna   | Gato    | 10   | 2    | Pequeño |
      | Toby   | Perro   | 12.5 | 4    | Mediano |
      | Rocky  | Perro   | 25   | 6    | Grande  |

  @Issue-6 @negativo
  Scenario: Registro rechazado de una mascota sin vacunas al día
    When registra la mascota "Michi" de especie "Gato" con peso "4" kg, edad "1" años y vacunas al día "no"
    Then se muestra el mensaje en mascotas "No podemos hospedar mascotas sin vacunas al día"

  @Issue-7 @negativo
  Scenario Outline: Registro rechazado por datos fuera de rango - <caso>
    When registra la mascota "<nombre>" de especie "Perro" con peso "<peso>" kg, edad "<edad>" años y vacunas al día "si"
    Then se muestra el mensaje en mascotas "Revisa los campos marcados"
    And el campo "<campo>" de la mascota muestra el error "<error>"
    Examples:
      | caso                 | nombre | peso | edad | campo  | error                                         |
      | nombre muy corto     | A      | 10   | 3    | nombre | El nombre debe tener entre 2 y 30 caracteres  |
      | peso en cero         | Bruno  | 0    | 3    | peso   | El peso debe ser mayor que 0 y máximo 80 kg   |
      | peso mayor a 80 kg   | Bruno  | 80.5 | 3    | peso   | El peso debe ser mayor que 0 y máximo 80 kg   |
      | peso no numérico     | Bruno  | diez | 3    | peso   | El peso debe ser mayor que 0 y máximo 80 kg   |
      | edad mayor a 25 años | Bruno  | 10   | 26   | edad   | La edad debe estar entre 0 y 25 años          |

  @Issue-8 @negativo
  Scenario: Registro rechazado de una mascota con nombre duplicado
    Given tiene registrada la mascota "Toby" de especie "Perro" con peso "12" kg
    When registra la mascota "toby" de especie "Perro" con peso "8" kg, edad "2" años y vacunas al día "si"
    Then el campo "nombre" de la mascota muestra el error "Ya existe una mascota registrada con ese nombre"
