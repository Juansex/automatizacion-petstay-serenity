package Atm.task;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static Atm.PO.PaginaReservar.fechaEntrada;
import static Atm.PO.PaginaReservar.opcionMascota;
import static Atm.Utils.RutasYConstantes.tiempoEsperaSegundos;
import static Atm.Utils.RutasYConstantes.urlReservar;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/** Abre el formulario de reserva y selecciona la mascota (dispara el evento change del select). */
public class ElegirMascotaParaReservar implements Task {

    private final String nombreMascota;

    public ElegirMascotaParaReservar(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.url(urlReservar),
                WaitUntil.the(fechaEntrada, isVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds(),
                Click.on(opcionMascota.of(nombreMascota))
        );
    }

    public static ElegirMascotaParaReservar llamada(String nombreMascota) {
        return instrumented(ElegirMascotaParaReservar.class, nombreMascota);
    }
}
