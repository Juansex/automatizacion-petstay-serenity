package Atm.task;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;

import static Atm.PO.EncabezadoPetStay.btnCerrarSesion;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class CerrarSesion implements Task {

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Click.on(btnCerrarSesion));
    }

    public static CerrarSesion delSistema() {
        return instrumented(CerrarSesion.class);
    }
}
