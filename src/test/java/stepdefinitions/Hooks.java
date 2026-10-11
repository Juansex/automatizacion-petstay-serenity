package stepdefinitions;

import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;

public class Hooks {

    /**
     * Antes de cada escenario: OnlineCast le da al actor la habilidad BrowseTheWeb
     * con un navegador nuevo (localStorage limpio = datos de la app limpios).
     */
    @Before
    public void Inicio() {
        OnStage.setTheStage(new OnlineCast());
        theActorCalled("Dueño");
    }
}
