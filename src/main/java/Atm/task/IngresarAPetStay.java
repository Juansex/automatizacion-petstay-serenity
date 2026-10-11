package Atm.task;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static Atm.PO.EncabezadoPetStay.saludo;
import static Atm.Utils.RutasYConstantes.*;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/** Precondición de los escenarios privados: abrir la app e iniciar sesión con el usuario válido. */
public class IngresarAPetStay implements Task {

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                AbrirPetStay.sinDatosPrevios(),
                Logueo.go(usuarioValido, contrasenaValida),
                WaitUntil.the(saludo, isVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds()
        );
    }

    public static IngresarAPetStay comoDueno() {
        return instrumented(IngresarAPetStay.class);
    }
}
