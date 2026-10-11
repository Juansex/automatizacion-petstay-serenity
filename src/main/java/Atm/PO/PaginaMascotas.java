package Atm.PO;

import net.serenitybdd.screenplay.targets.Target;

public class PaginaMascotas {
    public static final Target nombre = Target.the("campo nombre de la mascota").locatedBy("css:#nombreMascota");
    public static final Target especie = Target.the("especie {0}").locatedBy("//input[@name='especie'][@value='{0}']");
    public static final Target peso = Target.the("campo peso").locatedBy("css:input[name='peso']");
    public static final Target edad = Target.the("campo edad").locatedBy("css:[data-testid='edad']");
    public static final Target vacunas = Target.the("check vacunas al día").locatedBy("css:input[name='vacunas']");
    public static final Target btnRegistrar = Target.the("botón Registrar mascota").locatedBy("//form[@id='form-mascota']//button[@type='submit']");
    public static final Target mensaje = Target.the("mensaje de la página de mascotas").locatedBy("css:[data-testid='mensaje-mascotas']");
    public static final Target errorCampo = Target.the("error del campo {0}").locatedBy("//form[@id='form-mascota']//span[@class='error-campo'][@data-campo='{0}']");
    public static final Target tamanoMascota = Target.the("tamaño de la mascota {0}").locatedBy("//table[@id='tabla-mascotas']//tr[td[@class='nombre' and normalize-space()='{0}']]//span[@class='etiqueta-tamano']");
}
