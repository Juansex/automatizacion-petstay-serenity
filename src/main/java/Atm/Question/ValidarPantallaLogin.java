package Atm.Question;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static Atm.PO.PaginaLogin.BtonLogin;
import static Atm.Utils.RutasYConstantes.tiempoEsperaSegundos;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;

/** Verifica que el actor quedó en la pantalla de inicio de sesión (index.html). */
public class ValidarPantallaLogin implements Question<Boolean> {

    @Override
    public Boolean answeredBy(Actor actor) {
        actor.attemptsTo(WaitUntil.the(BtonLogin, isVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds());
        return the(BtonLogin).answeredBy(actor).isVisible();
    }

    public static Question<Boolean> verificarPantallaLogin() {
        return new ValidarPantallaLogin();
    }
}
