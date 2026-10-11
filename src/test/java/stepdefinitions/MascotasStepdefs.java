package stepdefinitions;

import Atm.Question.MascotaRegistrada;
import Atm.Question.Mensajes;
import Atm.task.RegistrarMascota;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.equalTo;

public class MascotasStepdefs {

    private static final String edadPorDefecto = "3";

    @Given("tiene registrada la mascota {string} de especie {string} con peso {string} kg")
    public void tiene_registrada_la_mascota(String nombre, String especie, String peso) {
        theActorInTheSpotlight().attemptsTo(RegistrarMascota.conDatos(nombre, especie, peso, edadPorDefecto, true));
    }

    @When("registra la mascota {string} de especie {string} con peso {string} kg, edad {string} años y vacunas al día {string}")
    public void registra_la_mascota(String nombre, String especie, String peso, String edad, String vacunas) {
        theActorInTheSpotlight().attemptsTo(
                RegistrarMascota.conDatos(nombre, especie, peso, edad, "si".equalsIgnoreCase(vacunas)));
    }

    @Then("se muestra el mensaje en mascotas {string}")
    public void se_muestra_el_mensaje_en_mascotas(String mensaje) {
        theActorInTheSpotlight().should(seeThat(Mensajes.deMascotas(), equalTo(mensaje)));
    }

    @Then("el campo {string} de la mascota muestra el error {string}")
    public void el_campo_de_la_mascota_muestra_el_error(String campo, String error) {
        theActorInTheSpotlight().should(seeThat(Mensajes.errorEnCampoDeMascota(campo), equalTo(error)));
    }

    @Then("la mascota {string} aparece en la lista con tamaño {string}")
    public void la_mascota_aparece_en_la_lista_con_tamano(String nombre, String tamano) {
        theActorInTheSpotlight().should(seeThat(MascotaRegistrada.tamanoDe(nombre), equalTo(tamano)));
    }
}
