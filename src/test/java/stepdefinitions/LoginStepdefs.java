package stepdefinitions;

import Atm.Question.Mensajes;
import Atm.task.AbrirPetStay;
import Atm.task.CerrarSesion;
import Atm.task.IngresarAPetStay;
import Atm.task.Logueo;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static Atm.Question.ValidarDashboard.verificarCargaDashboard;
import static Atm.Question.ValidarPantallaLogin.verificarPantallaLogin;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.equalTo;

public class LoginStepdefs {

    @Given("acceso a la pagina de hotel para mascotas")
    public void acceso_a_la_pagina_de_hotel_para_mascotas() {
        theActorInTheSpotlight().attemptsTo(AbrirPetStay.sinDatosPrevios());
    }

    @Given("el dueño inició sesión en PetStay")
    public void el_dueno_inicio_sesion_en_pet_stay() {
        theActorInTheSpotlight().attemptsTo(IngresarAPetStay.comoDueno());
    }

    @When("ingreso nombre de {string} y {string} e ingreso al sistema")
    public void ingreso_nombre_de_usuario_contrasena_y_doy_click_en_ingresar(String usuario, String contrasena) {
        theActorInTheSpotlight().attemptsTo(Logueo.go(usuario, contrasena));
    }

    @When("cierra la sesión")
    public void cierra_la_sesion() {
        theActorInTheSpotlight().attemptsTo(CerrarSesion.delSistema());
    }

    @Then("valido que haya ingresado")
    public void valido_que_haya_ingresado() {
        theActorInTheSpotlight().should(seeThat(verificarCargaDashboard()));
    }

    @Then("el sistema saluda al usuario con {string}")
    public void el_sistema_saluda_al_usuario_con(String saludo) {
        theActorInTheSpotlight().should(seeThat(Mensajes.saludo(), equalTo(saludo)));
    }

    @Then("se muestra el mensaje de error de login {string}")
    public void se_muestra_el_mensaje_de_error_de_login(String mensaje) {
        theActorInTheSpotlight().should(seeThat(Mensajes.delLogin(), equalTo(mensaje)));
    }

    @Then("regresa a la pantalla de inicio de sesión")
    public void regresa_a_la_pantalla_de_inicio_de_sesion() {
        theActorInTheSpotlight().should(seeThat(verificarPantallaLogin()));
    }
}
