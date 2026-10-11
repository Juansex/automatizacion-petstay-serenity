package Atm.Question;

import Atm.task.ConfirmarReserva;
import net.serenitybdd.screenplay.Question;

import static Atm.PO.PaginaMisReservas.estadoReserva;
import static Atm.PO.PaginaReservar.*;

/** Valores del resumen de la cotización y de la reserva confirmada. */
public class ResumenReserva {

    private ResumenReserva() {
    }

    public static Question<String> total() {
        return Question.about("el total de la reserva")
                .answeredBy(actor -> TextoVisible.de(resTotal).answeredBy(actor));
    }

    public static Question<String> descuento() {
        return Question.about("el descuento por estadía larga")
                .answeredBy(actor -> TextoVisible.de(resDescuento).answeredBy(actor));
    }

    public static Question<Integer> noches() {
        return Question.about("las noches de la estadía")
                .answeredBy(actor -> Integer.parseInt(TextoVisible.de(resNoches).answeredBy(actor)));
    }

    public static Question<String> codigoGenerado() {
        return Question.about("el código de la reserva confirmada")
                .answeredBy(actor -> actor.recall(ConfirmarReserva.codigoGenerado));
    }

    /** Estado (Confirmada/Cancelada) de la última reserva confirmada por el actor, leído en Mis reservas. */
    public static Question<String> estadoEnMisReservas() {
        return Question.about("el estado de la reserva en Mis reservas")
                .answeredBy(actor -> {
                    String codigo = actor.recall(ConfirmarReserva.codigoGenerado);
                    return TextoVisible.de(estadoReserva.of(codigo)).answeredBy(actor);
                });
    }
}
