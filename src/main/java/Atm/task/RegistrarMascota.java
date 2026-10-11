package Atm.task;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.conditions.Check;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static Atm.PO.EncabezadoPetStay.capaCargando;
import static Atm.PO.PaginaMascotas.*;
import static Atm.Utils.RutasYConstantes.tiempoEsperaSegundos;
import static Atm.Utils.RutasYConstantes.urlMascotas;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class RegistrarMascota implements Task {

    private final String nombreMascota;
    private final String especieMascota;
    private final String pesoMascota;
    private final String edadMascota;
    private final boolean vacunasAlDia;

    public RegistrarMascota(String nombreMascota, String especieMascota, String pesoMascota,
                            String edadMascota, Boolean vacunasAlDia) {
        this.nombreMascota = nombreMascota;
        this.especieMascota = especieMascota;
        this.pesoMascota = pesoMascota;
        this.edadMascota = edadMascota;
        this.vacunasAlDia = vacunasAlDia;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.url(urlMascotas),
                WaitUntil.the(nombre, isVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds(),
                Enter.theValue(nombreMascota).into(nombre),
                Click.on(especie.of(especieMascota)),
                Enter.theValue(pesoMascota).into(peso),
                Enter.theValue(edadMascota).into(edad),
                Check.whether(vacunasAlDia).andIfSo(Click.on(vacunas)),
                Click.on(btnRegistrar),
                WaitUntil.the(capaCargando, isNotVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds(),
                WaitUntil.the(mensaje, isVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds()
        );
    }

    public static RegistrarMascota conDatos(String nombre, String especie, String peso, String edad, boolean vacunas) {
        return instrumented(RegistrarMascota.class, nombre, especie, peso, edad, vacunas);
    }
}
