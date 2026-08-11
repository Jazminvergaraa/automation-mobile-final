package com.nttdata.stepsdefinitions;

import com.nttdata.steps.SauceLabsLoginSteps;
import com.nttdata.support.ScreenshotAttacher;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertTrue;

/**
 * Step definitions especificos del login de Sauce Labs.
 * <p>
 * NO contiene el step "Given ingreso al aplicativo...": ese step es
 * generico y vive en {@link AppConfigStepsDefs}, reutilizable para
 * cualquier app.
 */
public class SauceLabsStepDefs {

    private final SauceLabsLoginSteps sauceLabsLoginSteps = new SauceLabsLoginSteps();

    private Scenario scenario;

    @Before
    public void configurarScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    @When("ingreso el usuario {string}")
    public void ingreso_el_usuario(String usuario) {
        sauceLabsLoginSteps.ingresarUsuario(usuario);
    }

    @And("ingreso la clave {string}")
    public void ingreso_la_clave(String clave) {
        sauceLabsLoginSteps.ingresarClave(clave);
    }

    @And("hago clic en LOGIN")
    public void hago_clic_en_login() {
        sauceLabsLoginSteps.clicEnLogin();
        ScreenshotAttacher.attach(scenario, "Despues de hacer login");
    }

    @Then("valido el login OK")
    public void valido_el_login_ok() {
        assertTrue(
                "No se encontro la pantalla de productos: el login no fue exitoso",
                sauceLabsLoginSteps.loginFueExitoso()
        );
    }
}
