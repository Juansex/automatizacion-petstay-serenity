@HU-03
Feature: HU-03 Reserva de estadías
  Como dueño de una mascota registrada
  Quiero cotizar y confirmar una estadía en el hotel
  Para asegurar el alojamiento de mi mascota en las fechas que necesito

  Background:
    Given el dueño inició sesión en PetStay

  @Issue-9 @positivo @smoketest
  Scenario Outline: Cálculo del total según tamaño, noches y servicios adicionales - <caso>
    Given tiene registrada la mascota "<nombre>" de especie "<especie>" con peso "<peso>" kg
    When cotiza una estadía para "<nombre>" con entrada en 2 días, salida en <salida> días, baño "<bano>" y paseo "<paseo>"
    Then las noches calculadas son <noches>
    And el total de la reserva es "<total>"
    Examples:
      | caso                          | nombre | especie | peso | salida | bano | paseo | noches | total    |
      | perro mediano con baño        | Toby   | Perro   | 12   | 5      | si   | no    | 3      | $190.000 |
      | perro grande con paseo        | Rocky  | Perro   | 30   | 4      | no   | si    | 2      | $150.000 |
      | gato pequeño sin servicios    | Luna   | Gato    | 4    | 3      | no   | no    | 1      | $40.000  |

  @Issue-10 @positivo
  Scenario: Descuento del 15% por estadía larga de 7 noches o más
    Given tiene registrada la mascota "Luna" de especie "Gato" con peso "4" kg
    When cotiza una estadía para "Luna" con entrada en 1 días, salida en 8 días, baño "no" y paseo "no"
    Then las noches calculadas son 7
    And el descuento por estadía larga es "-$42.000"
    And el total de la reserva es "$238.000"

  @Issue-11 @positivo
  Scenario: Confirmación de una reserva y consulta en Mis reservas
    Given tiene registrada la mascota "Toby" de especie "Perro" con peso "12" kg
    When cotiza una estadía para "Toby" con entrada en 3 días, salida en 5 días, baño "no" y paseo "no"
    And confirma la reserva
    Then se genera un código de reserva con formato "PS-####"
    And la reserva aparece en Mis reservas con estado "Confirmada"

  @Issue-12 @negativo
  Scenario Outline: Cotización rechazada por fechas inválidas - <caso>
    Given tiene registrada la mascota "Toby" de especie "Perro" con peso "12" kg
    When cotiza una estadía para "Toby" con entrada en <entrada> días, salida en <salida> días, baño "no" y paseo "no"
    Then se muestra el mensaje de reserva "Revisa los campos marcados"
    And el campo "<campo>" de la reserva muestra el error "<error>"
    Examples:
      | caso                      | entrada | salida | campo   | error                                                 |
      | entrada anterior a hoy    | -1      | 2      | entrada | La fecha de entrada no puede ser anterior a hoy       |
      | salida igual a la entrada | 2       | 2      | salida  | La fecha de salida debe ser posterior a la de entrada |
      | estadía de 31 noches      | 1       | 32     | salida  | La estadía máxima es de 30 noches                     |

  @Issue-14 @negativo
  Scenario: Reserva rechazada por cruce de fechas con otra reserva activa
    Given tiene registrada la mascota "Toby" de especie "Perro" con peso "12" kg
    And tiene una reserva confirmada para "Toby" con entrada en 5 días y salida en 8 días
    When cotiza una estadía para "Toby" con entrada en 6 días, salida en 10 días, baño "no" y paseo "no"
    Then se muestra el mensaje de reserva "Esta mascota ya tiene una reserva activa en esas fechas"
