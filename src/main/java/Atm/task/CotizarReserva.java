package Atm.task;

import Atm.Utils.Fechas;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.conditions.Check;

import static Atm.PO.PaginaReservar.*;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/** Diligencia el formulario de reserva y calcula el total (no confirma). */
public class CotizarReserva implements Task {

    private final String nombreMascota;
    private final int diasHastaEntrada;
    private final int diasHastaSalida;
    private final boolean conBano;
    private final boolean conPaseo;

    public CotizarReserva(String nombreMascota, Integer diasHastaEntrada, Integer diasHastaSalida,
                          Boolean conBano, Boolean conPaseo) {
        this.nombreMascota = nombreMascota;
        this.diasHastaEntrada = diasHastaEntrada;
        this.diasHastaSalida = diasHastaSalida;
        this.conBano = conBano;
        this.conPaseo = conPaseo;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                ElegirMascotaParaReservar.llamada(nombreMascota),
                Enter.theValue(Fechas.dentroDeDias(diasHastaEntrada)).into(fechaEntrada),
                Enter.theValue(Fechas.dentroDeDias(diasHastaSalida)).into(fechaSalida),
                Check.whether(conBano).andIfSo(Click.on(servicioBano)),
                Check.whether(conPaseo).andIfSo(Click.on(servicioPaseo)),
                Click.on(btnCalcular)
        );
    }

    public static CotizarReserva para(String mascota, int diasHastaEntrada, int diasHastaSalida) {
        return conServicios(mascota, diasHastaEntrada, diasHastaSalida, false, false);
    }

    public static CotizarReserva conServicios(String mascota, int diasHastaEntrada, int diasHastaSalida,
                                              boolean bano, boolean paseo) {
        return instrumented(CotizarReserva.class, mascota, diasHastaEntrada, diasHastaSalida, bano, paseo);
    }
}
