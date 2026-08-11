package com.nttdata.stepsdefinitions;

import com.nttdata.steps.AppConfigSteps;
import com.nttdata.support.ScreenshotAttacher;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

/**
 * Step definitions GENERICOS para iniciar y cerrar cualquier aplicacion.
 * <p>
 * Se reutilizan desde CUALQUIER feature, indicando por parametro el
 * archivo de configuracion (.properties) y el appId de la app a
 * levantar o cerrar. No es necesario crear un step nuevo por cada app.
 * <p>
 * Ejemplo de uso en un .feature:
 * <pre>
 *   Given ingreso al aplicativo "saucelabs"
 *   ...
 *   Then cierro el aplicativo "com.swaglabsmobileapp"
 * </pre>
 */
public class AppConfigStepsDefs {

    private final AppConfigSteps appConfigSteps = new AppConfigSteps();

    private Scenario scenario;

    @Before
    public void configurarScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("ingreso al aplicativo {string}")
    public void ingreso_al_aplicativo(String nombreConfiguracion) {
        System.out.println("[AppConfigStepsDefs] Iniciando aplicativo: " + nombreConfiguracion);
        appConfigSteps.iniciarAplicacion(nombreConfiguracion + ".properties");
        ScreenshotAttacher.attach(scenario, "Pantalla inicial");
    }

    @Then("cierro el aplicativo {string}")
    public void cierro_el_aplicativo(String appId) {
        System.out.println("[AppConfigStepsDefs] Cerrando aplicativo: " + appId);
        appConfigSteps.cerrarAplicacion(appId);
    }
}
