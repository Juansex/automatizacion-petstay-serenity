package Atm.Question;

import net.serenitybdd.screenplay.Question;

import static Atm.PO.PaginaMascotas.tamanoMascota;

public class MascotaRegistrada {

    private MascotaRegistrada() {
    }

    public static Question<String> tamanoDe(String nombre) {
        return Question.about("el tamaño de " + nombre + " en la lista de mascotas")
                .answeredBy(actor -> TextoVisible.de(tamanoMascota.of(nombre)).answeredBy(actor));
    }
}
