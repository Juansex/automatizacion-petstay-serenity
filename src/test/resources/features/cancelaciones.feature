@HU-04
Feature: HU-04 Cancelación de reservas
  Como dueño de una mascota
  Quiero cancelar una reserva confirmada
  Para recibir el reembolso que indica la política de cancelación

  Background:
    Given el dueño inició sesión en PetStay
    And tiene registrada la mascota "Luna" de especie "Gato" con peso "4" kg

  @Issue-15 @positivo @smoketest
  Scenario Outline: Cancelación con reembolso según la anticipación - <politica>
    Given tiene una reserva confirmada para "Luna" con entrada en <entrada> días y salida en <salida> días
    When cancela la reserva confirmando la acción
    Then se muestra el mensaje de cancelación con "Reembolso: <reembolso>"
    And la reserva aparece en Mis reservas con estado "Cancelada"
    Examples:
      | politica                    | entrada | salida | reembolso |
      | 3 días o más antes: 100%    | 5       | 6      | $40.000   |
      | 1 o 2 días antes: 50%       | 1       | 2      | $20.000   |
      | mismo día de entrada: 0%    | 0       | 1      | $0        |

  @Issue-16 @negativo
  Scenario: El dueño desiste de cancelar y la reserva sigue confirmada
    Given tiene una reserva confirmada para "Luna" con entrada en 5 días y salida en 6 días
    When inicia la cancelación de la reserva pero decide no cancelar
    Then la reserva aparece en Mis reservas con estado "Confirmada"
