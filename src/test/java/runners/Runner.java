package runners;
import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;


/**
 * Ejecuta todos los .feature. Para filtrar por tag (requisito, issue o smoketest):
 * ./gradlew clean test -Dcucumber.filter.tags="@Issue-5"
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        plugin =  "pretty",
        features = "src/test/resources/features",
        glue = "stepdefinitions",
        snippets = CucumberOptions.SnippetType.CAMELCASE
)

public class Runner {

}
