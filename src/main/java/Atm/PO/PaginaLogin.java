package Atm.PO;


import net.serenitybdd.screenplay.targets.Target;

public class PaginaLogin {
    public static final Target usuario = Target.the("campo usuario").locatedBy("//input[@name='usuario']");
    public static final Target Contrasena = Target.the("campo contraseña").locatedBy("css:#pass-01");
    public static final Target BtonLogin = Target.the("botón Ingresar").locatedBy("css:[data-testid='btn-ingresar']");
    public static final Target mensajeError = Target.the("mensaje de error del login").locatedBy("css:#error-login");
}
