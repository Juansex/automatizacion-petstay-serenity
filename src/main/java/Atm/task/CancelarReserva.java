package Atm.task;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.conditions.Check;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static Atm.PO.EncabezadoPetStay.capaCargando;
import static Atm.PO.PaginaMisReservas.*;
import static Atm.Utils.RutasYConstantes.tiempoEsperaSegundos;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/** Abre el modal de cancelación de una reserva y lo confirma o lo descarta. */
public class CancelarReserva implements Task {

    private final String codigo;
    private final boolean confirmar;

    public CancelarReserva(String codigo, Boolean confirmar) {
        this.codigo = codigo;
        this.confirmar = confirmar;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                VerMisReservas.enElSistema(),
                Click.on(btnCancelar.of(codigo)),
                WaitUntil.the(modalCancelacion, isVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds(),
                Check.whether(confirmar)
                        .andIfSo(
                                Click.on(btnConfirmarCancelacion),
                                WaitUntil.the(capaCargando, isNotVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds(),
                                WaitUntil.the(mensaje, isVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds())
                        .otherwise(
                                Click.on(btnVolver),
                                WaitUntil.the(modalCancelacion, isNotVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds())
        );
    }

    public static CancelarReserva confirmando(String codigo) {
        return instrumented(CancelarReserva.class, codigo, true);
    }

    public static CancelarReserva desistiendo(String codigo) {
        return instrumented(CancelarReserva.class, codigo, false);
    }
}
