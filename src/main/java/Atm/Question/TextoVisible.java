package Atm.Question;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static Atm.Utils.RutasYConstantes.tiempoEsperaSegundos;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * Lee el texto de un elemento esperando a que sea visible.
 * La app muestra los mensajes después de una espera aleatoria (300-1500 ms),
 * por eso no basta con leer el texto apenas se hace clic.
 */
public class TextoVisible implements Question<String> {

    private final Target target;

    public TextoVisible(Target target) {
        this.target = target;
    }

    @Override
    public String answeredBy(Actor actor) {
        actor.attemptsTo(WaitUntil.the(target, isVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds());
        return Text.of(target).answeredBy(actor).trim();
    }

    public static Question<String> de(Target target) {
        return new TextoVisible(target);
    }
}
