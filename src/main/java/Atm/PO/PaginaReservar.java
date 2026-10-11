package Atm.PO;

import net.serenitybdd.screenplay.targets.Target;

public class PaginaReservar {
    public static final Target opcionMascota = Target.the("opción de la mascota {0}").locatedBy("//select[@name='mascota']/option[starts-with(normalize-space(.),'{0} (')]");
    public static final Target fechaEntrada = Target.the("campo fecha de entrada").locatedBy("css:#fecha-entrada");
    public static final Target fechaSalida = Target.the("campo fecha de salida").locatedBy("css:[data-testid='fecha-salida']");
    public static final Target servicioBano = Target.the("check baño y peluquería").locatedBy("css:#srv-bano");
    public static final Target servicioPaseo = Target.the("check paseo diario").locatedBy("css:input[name='paseo']");
    public static final Target notaPaseo = Target.the("nota de paseo solo para perros").locatedBy("css:p.nota-paseo");
    public static final Target btnCalcular = Target.the("botón Calcular total").locatedBy("css:#btn-calcular");
    public static final Target panelResumen = Target.the("panel de resumen").locatedBy("css:aside.panel-resumen");
    public static final Target resNoches = Target.the("noches del resumen").locatedBy("css:[data-testid='res-noches']");
    public static final Target resDescuento = Target.the("descuento del resumen").locatedBy("css:[data-testid='res-descuento']");
    public static final Target resTotal = Target.the("total del resumen").locatedBy("css:[data-testid='res-total']");
    public static final Target btnConfirmar = Target.the("botón Confirmar reserva").locatedBy("//aside[contains(@class,'panel-resumen')]//button[normalize-space()='Confirmar reserva']");
    public static final Target mensaje = Target.the("mensaje de la reserva").locatedBy("css:[data-testid='mensaje-reserva']");
    public static final Target codigoReserva = Target.the("código de la reserva").locatedBy("css:[data-testid='codigo-reserva']");
    public static final Target errorCampo = Target.the("error del campo {0}").locatedBy("//form[@id='form-reserva']//span[@class='error-campo'][@data-campo='{0}']");
}
