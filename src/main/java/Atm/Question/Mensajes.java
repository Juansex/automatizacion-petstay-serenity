package Atm.Question;

import Atm.PO.EncabezadoPetStay;
import Atm.PO.PaginaLogin;
import Atm.PO.PaginaMascotas;
import Atm.PO.PaginaMisReservas;
import Atm.PO.PaginaReservar;
import net.serenitybdd.screenplay.Question;

/** Mensajes informativos y de error que muestra cada pantalla. */
public class Mensajes {

    private Mensajes() {
    }

    public static Question<String> saludo() {
        return Question.about("el saludo de la barra superior")
                .answeredBy(actor -> TextoVisible.de(EncabezadoPetStay.saludo).answeredBy(actor));
    }

    public static Question<String> delLogin() {
        return Question.about("el mensaje de error del login")
                .answeredBy(actor -> TextoVisible.de(PaginaLogin.mensajeError).answeredBy(actor));
    }

    public static Question<String> deMascotas() {
        return Question.about("el mensaje de la página de mascotas")
                .answeredBy(actor -> TextoVisible.de(PaginaMascotas.mensaje).answeredBy(actor));
    }

    public static Question<String> errorEnCampoDeMascota(String campo) {
        return Question.about("el error del campo " + campo + " de la mascota")
                .answeredBy(actor -> TextoVisible.de(PaginaMascotas.errorCampo.of(campo)).answeredBy(actor));
    }

    public static Question<String> deReserva() {
        return Question.about("el mensaje de la reserva")
                .answeredBy(actor -> TextoVisible.de(PaginaReservar.mensaje).answeredBy(actor));
    }

    public static Question<String> errorEnCampoDeReserva(String campo) {
        return Question.about("el error del campo " + campo + " de la reserva")
                .answeredBy(actor -> TextoVisible.de(PaginaReservar.errorCampo.of(campo)).answeredBy(actor));
    }

    public static Question<String> deMisReservas() {
        return Question.about("el mensaje de mis reservas")
                .answeredBy(actor -> TextoVisible.de(PaginaMisReservas.mensaje).answeredBy(actor));
    }
}
