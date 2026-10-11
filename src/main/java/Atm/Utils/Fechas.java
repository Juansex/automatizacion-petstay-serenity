package Atm.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * La app valida las fechas contra el día actual, por eso los escenarios
 * no usan fechas fijas sino desplazamientos en días respecto a hoy.
 */
public class Fechas {

    private static final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Fechas() {
    }

    public static String dentroDeDias(int dias) {
        return LocalDate.now().plusDays(dias).format(formato);
    }
}
