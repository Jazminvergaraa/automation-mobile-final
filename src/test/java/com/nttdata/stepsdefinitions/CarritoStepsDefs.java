package com.nttdata.stepsdefinitions;

import com.nttdata.steps.AppConfigSteps;
import com.nttdata.steps.CarritoSteps;
import com.nttdata.support.ScreenshotAttacher;
import io.cucumber.java.*;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.*;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class CarritoStepsDefs {

    private static final String ARCHIVO_CONFIGURACION = "mydemoapp.properties";
    private static final String APP_ID = "com.saucelabs.mydemoapp.android";

    private final AppConfigSteps appConfigSteps = new AppConfigSteps();
    private final CarritoSteps carritoSteps = new CarritoSteps();

    private Scenario scenario;
    private String ultimoProductoAgregado;
    private int ultimasUnidadesAgregadas;

    @Before
    public void configurarScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("estoy en la aplicación de SauceLabs")
    public void estoy_en_la_aplicacion_de_sauceLabs() {
        appConfigSteps.iniciarAplicacion(ARCHIVO_CONFIGURACION);
        ScreenshotAttacher.attach(scenario, "Pantalla inicial - My Demo App");

        carritoSteps.asegurarCarritoVacio();
        ScreenshotAttacher.attach(scenario, "Carrito verificado/limpio antes de iniciar");
    }

    @And("valido que carguen correctamente los productos en la galeria")
    public void valido_que_carguen_correctamente_los_productos_en_la_galeria() {
        List<String> productos = carritoSteps.validarGaleriaCargada();
        assertTrue(
                "La galeria de productos no cargo ningun producto. Posible APK inestable.",
                !productos.isEmpty()
        );
        ScreenshotAttacher.attach(scenario, "Galeria de productos cargada");
    }

    @When("agrego {int} del siguiente producto {string}")
    public void agrego_del_siguiente_producto(Integer unidades, String producto) {
        carritoSteps.agregarProductoAlCarrito(producto, unidades);
        ultimoProductoAgregado = producto;
        ultimasUnidadesAgregadas = unidades;
        ScreenshotAttacher.attach(scenario, "Agregado " + producto + " x" + unidades);
    }

    @Then("valido el carrito de compra actualice correctamente")
    public void valido_el_carrito_de_compra_actualice_correctamente() {
        boolean actualizadoOk = carritoSteps.carritoSeActualizoCorrectamente(ultimoProductoAgregado, ultimasUnidadesAgregadas);
        ScreenshotAttacher.attach(scenario, "Carrito actualizado");
        assertTrue(
                "El producto '" + ultimoProductoAgregado + "' no aparece en el carrito con la cantidad esperada ("
                        + ultimasUnidadesAgregadas + ").",
                actualizadoOk
        );
    }

    @After
    public void cerrarAplicacionCarrito() {
        appConfigSteps.cerrarAplicacion(APP_ID);
    }
}