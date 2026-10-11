package Atm.task;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Evaluate;
import net.serenitybdd.screenplay.actions.Open;

import static Atm.Utils.RutasYConstantes.urlLogin;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * PetStay guarda sesión, mascotas y reservas en el localStorage del navegador.
 * Si el navegador se reutiliza entre escenarios, esos datos sobreviven; por eso
 * cada escenario arranca abriendo la app y limpiando el localStorage.
 */
public class AbrirPetStay implements Task {

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.url(urlLogin),
                Evaluate.javascript("window.localStorage.clear();"),
                Open.url(urlLogin)
        );
    }

    public static AbrirPetStay sinDatosPrevios() {
        return instrumented(AbrirPetStay.class);
    }
}
