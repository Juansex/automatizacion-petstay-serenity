package Atm.Question;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.WebElementQuestion;

import static Atm.PO.PaginaReservar.notaPaseo;
import static Atm.PO.PaginaReservar.servicioPaseo;

public class ServicioPaseo {

    private ServicioPaseo() {
    }

    public static Question<Boolean> estaHabilitado() {
        return Question.about("si el servicio de paseo está habilitado")
                .answeredBy(actor -> WebElementQuestion.the(servicioPaseo).answeredBy(actor).isEnabled());
    }

    public static Question<String> nota() {
        return Question.about("la nota del servicio de paseo")
                .answeredBy(actor -> TextoVisible.de(notaPaseo).answeredBy(actor));
    }
}
