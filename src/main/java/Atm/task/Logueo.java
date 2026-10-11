package Atm.task;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.conditions.Check;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static Atm.PO.PaginaLogin.*;
import static Atm.Utils.RutasYConstantes.tiempoEsperaSegundos;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class Logueo implements Task {

    // Campos de instancia (no static) para que cada actor/escenario tenga sus propios datos
    private final String usu;
    private final String contras;

    public Logueo(String usu, String contras) {
        this.usu = usu;
        this.contras = contras;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                WaitUntil.the(usuario, isVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds(),
                Check.whether(!usu.isEmpty()).andIfSo(Enter.theValue(usu).into(usuario)),
                Check.whether(!contras.isEmpty()).andIfSo(Enter.theValue(contras).into(Contrasena)),
                Click.on(BtonLogin)
        );
    }

    public static Logueo go(String usuario, String pass) {
        return instrumented(Logueo.class, usuario, pass);
    }
}
