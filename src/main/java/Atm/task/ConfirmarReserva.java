package Atm.task;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static Atm.PO.PaginaReservar.*;
import static Atm.Utils.RutasYConstantes.tiempoEsperaSegundos;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/** Confirma la reserva cotizada y guarda el código generado en la memoria del actor. */
public class ConfirmarReserva implements Task {

    public static final String codigoGenerado = "codigoReserva";

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                WaitUntil.the(btnConfirmar, isVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds(),
                Click.on(btnConfirmar),
                WaitUntil.the(codigoReserva, isVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds()
        );
        actor.remember(codigoGenerado, Text.of(codigoReserva).answeredBy(actor).trim());
    }

    public static ConfirmarReserva cotizada() {
        return instrumented(ConfirmarReserva.class);
    }
}
