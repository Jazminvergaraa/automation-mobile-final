package com.nttdata.stepsdefinitions;

import com.nttdata.steps.ApkInfoSteps;
import com.nttdata.support.ScreenshotAttacher;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

/**
 * Step definitions del feature ApkInfo.feature.
 * Traduce el lenguaje Gherkin a llamadas sobre {@link ApkInfoSteps}.
 * No contiene logica de automatizacion: solo orquesta.
 */
public class ApkInfoStepDefs {

    private final ApkInfoSteps apkInfoSteps = new ApkInfoSteps();

    private Scenario scenario;

    @Before
    public void configurarScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("ingreso al aplicativo de APK Info")
    public void ingreso_al_aplicativo_de_apk_info() {
        apkInfoSteps.iniciarAplicacion();
        ScreenshotAttacher.attach(scenario, "Pantalla inicial");
    }

    @Then("busco el texto {string}")
    public void busco_el_texto(String texto) {
        apkInfoSteps.buscarTexto(texto);
        ScreenshotAttacher.attach(scenario, "Busqueda realizada - " + texto);
        apkInfoSteps.cerrarAplicacion();
    }
}
