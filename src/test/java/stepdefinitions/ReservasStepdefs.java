package stepdefinitions;

import Atm.Question.Mensajes;
import Atm.Question.ResumenReserva;
import Atm.Question.ServicioPaseo;
import Atm.task.CancelarReserva;
import Atm.task.ConfirmarReserva;
import Atm.task.CotizarReserva;
import Atm.task.ElegirMascotaParaReservar;
import Atm.task.VerMisReservas;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.*;

public class ReservasStepdefs {

    @Given("tiene una reserva confirmada para {string} con entrada en {int} días y salida en {int} días")
    public void tiene_una_reserva_confirmada(String mascota, int entrada, int salida) {
        theActorInTheSpotlight().attemptsTo(
                CotizarReserva.para(mascota, entrada, salida),
                ConfirmarReserva.cotizada());
    }

    @When("cotiza una estadía para {string} con entrada en {int} días, salida en {int} días, baño {string} y paseo {string}")
    public void cotiza_una_estadia(String mascota, int entrada, int salida, String bano, String paseo) {
        theActorInTheSpotlight().attemptsTo(
                CotizarReserva.conServicios(mascota, entrada, salida,
                        "si".equalsIgnoreCase(bano), "si".equalsIgnoreCase(paseo)));
    }

    @When("confirma la reserva")
    public void confirma_la_reserva() {
        theActorInTheSpotlight().attemptsTo(ConfirmarReserva.cotizada());
    }

    @When("elige a {string} en el formulario de reserva")
    public void elige_a_en_el_formulario_de_reserva(String mascota) {
        theActorInTheSpotlight().attemptsTo(ElegirMascotaParaReservar.llamada(mascota));
    }

    @When("cancela la reserva confirmando la acción")
    public void cancela_la_reserva_confirmando() {
        String codigo = theActorInTheSpotlight().asksFor(ResumenReserva.codigoGenerado());
        theActorInTheSpotlight().attemptsTo(CancelarReserva.confirmando(codigo));
    }

    @When("inicia la cancelación de la reserva pero decide no cancelar")
    public void inicia_la_cancelacion_pero_desiste() {
        String codigo = theActorInTheSpotlight().asksFor(ResumenReserva.codigoGenerado());
        theActorInTheSpotlight().attemptsTo(CancelarReserva.desistiendo(codigo));
    }

    @Then("las noches calculadas son {int}")
    public void las_noches_calculadas_son(int noches) {
        theActorInTheSpotlight().should(seeThat(ResumenReserva.noches(), equalTo(noches)));
    }

    @Then("el total de la reserva es {string}")
    public void el_total_de_la_reserva_es(String total) {
        theActorInTheSpotlight().should(seeThat(ResumenReserva.total(), equalTo(total)));
    }

    @Then("el descuento por estadía larga es {string}")
    public void el_descuento_por_estadia_larga_es(String descuento) {
        theActorInTheSpotlight().should(seeThat(ResumenReserva.descuento(), equalTo(descuento)));
    }

    @Then("se genera un código de reserva con formato {string}")
    public void se_genera_un_codigo_de_reserva_con_formato(String formato) {
        String patron = formato.replace("#", "\\d");
        theActorInTheSpotlight().should(seeThat(ResumenReserva.codigoGenerado(), matchesPattern(patron)));
    }

    @Then("la reserva aparece en Mis reservas con estado {string}")
    public void la_reserva_aparece_en_mis_reservas_con_estado(String estado) {
        theActorInTheSpotlight().attemptsTo(VerMisReservas.enElSistema());
        theActorInTheSpotlight().should(seeThat(ResumenReserva.estadoEnMisReservas(), equalTo(estado)));
    }

    @Then("se muestra el mensaje de reserva {string}")
    public void se_muestra_el_mensaje_de_reserva(String mensaje) {
        theActorInTheSpotlight().should(seeThat(Mensajes.deReserva(), equalTo(mensaje)));
    }

    @Then("el campo {string} de la reserva muestra el error {string}")
    public void el_campo_de_la_reserva_muestra_el_error(String campo, String error) {
        theActorInTheSpotlight().should(seeThat(Mensajes.errorEnCampoDeReserva(campo), equalTo(error)));
    }

    @Then("el servicio de paseo no está habilitado")
    public void el_servicio_de_paseo_no_esta_habilitado() {
        theActorInTheSpotlight().should(seeThat(ServicioPaseo.estaHabilitado(), is(false)));
    }

    @Then("se informa que {string}")
    public void se_informa_que(String nota) {
        theActorInTheSpotlight().should(seeThat(ServicioPaseo.nota(), equalTo(nota)));
    }

    @Then("se muestra el mensaje de cancelación con {string}")
    public void se_muestra_el_mensaje_de_cancelacion_con(String texto) {
        theActorInTheSpotlight().should(seeThat(Mensajes.deMisReservas(), containsString(texto)));
    }
}
