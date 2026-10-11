package Atm.PO;

import net.serenitybdd.screenplay.targets.Target;

/** Barra superior común a las páginas privadas (se pinta con JS en pintarEncabezado). */
public class EncabezadoPetStay {
    public static final Target saludo = Target.the("saludo al usuario").locatedBy("css:header.barra .saludo");
    public static final Target btnCerrarSesion = Target.the("botón Cerrar sesión").locatedBy("//button[@name='cerrar-sesion']");
    public static final Target capaCargando = Target.the("capa de carga").locatedBy("css:#capa-cargando");
    public static final Target tituloPagina = Target.the("título de la página").locatedBy("css:main h1");
}
