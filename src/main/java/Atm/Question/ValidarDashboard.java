package Atm.Question;


import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static Atm.PO.EncabezadoPetStay.saludo;
import static Atm.Utils.RutasYConstantes.tiempoEsperaSegundos;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;

/** El "dashboard" de PetStay es la página Mis mascotas con el saludo "Hola, <usuario>" en la barra. */
public class ValidarDashboard implements Question<Boolean> {


    @Override
    public Boolean answeredBy(Actor actor) {
        actor.attemptsTo(WaitUntil.the(saludo, isVisible()).forNoMoreThan(tiempoEsperaSegundos).seconds());
        return the(saludo).answeredBy(actor).isVisible();
    }

    public static Question<Boolean> verificarCargaDashboard(){
        return new ValidarDashboard();
    }

}
