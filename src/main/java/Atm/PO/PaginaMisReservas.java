package Atm.PO;

import net.serenitybdd.screenplay.targets.Target;

public class PaginaMisReservas {
    public static final Target estadoReserva = Target.the("estado de la reserva {0}").locatedBy("//tr[td[@class='codigo' and normalize-space()='{0}']]//span[contains(@class,'estado')]");
    public static final Target btnCancelar = Target.the("botón Cancelar de la reserva {0}").locatedBy("//button[@data-codigo='{0}']");
    public static final Target modalCancelacion = Target.the("modal de cancelación").locatedBy("css:div.fondo-modal");
    public static final Target textoModal = Target.the("texto del modal").locatedBy("css:div.fondo-modal .texto-modal");
    public static final Target btnConfirmarCancelacion = Target.the("botón Sí, cancelar").locatedBy("css:#confirmar-cancelacion");
    public static final Target btnVolver = Target.the("botón No, volver").locatedBy("//button[@data-accion='volver']");
    public static final Target mensaje = Target.the("mensaje de mis reservas").locatedBy("css:#mensaje-reservas");
}
