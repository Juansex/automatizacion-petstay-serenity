package Atm.task;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static Atm.PO.EncabezadoPetStay.tituloPagina;
import static Atm.Utils.RutasYConstantes.tiempoEsperaSegundos;
import static Atm.Utils.RutasYConstantes.urlMisReservas;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class VerMisReservas implements Task {

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.url(urlMisReservas),
                WaitUntil.the(tituloPagina, isVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds()
        );
    }

    public static VerMisReservas enElSistema() {
        return instrumented(VerMisReservas.class);
    }
}
