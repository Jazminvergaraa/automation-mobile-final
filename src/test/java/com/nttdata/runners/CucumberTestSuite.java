package com.nttdata.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner principal de la suite.
 * <p>
 * Por defecto ejecuta todos los escenarios marcados con @Smoke.
 * Para correr solo un feature puntual, cambia el valor de "tags",
 * por ejemplo: tags = "@SauceLabsLogin".
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "com.nttdata.stepsdefinitions",
                "com.nttdata.hooks"
        },
        tags = "@Smoke",
        plugin = {
                "pretty",
                "html:target/cucumber-report.html",
                "json:target/cucumber-report.json"
        },
        monochrome = true
)
public class CucumberTestSuite {

}
