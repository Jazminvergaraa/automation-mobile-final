package com.nttdata.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.nttdata.screens.AppConfigScreen.getDriver;

/**
 * SauceLabsLoginScreen
 * ---------------------
 * Pantalla de login de la app movil de Sauce Labs.
 * <p>
 * Su UNICA responsabilidad es interactuar con los elementos de esta
 * pantalla (escribir usuario/clave, hacer clic en LOGIN, verificar que
 * el login fue exitoso).
 * <p>
 * NO levanta ni cierra la aplicacion: eso es responsabilidad exclusiva
 * de {@link AppConfigScreen}. Esta clase solo pide el driver de la
 * sesion ya activa mediante {@code AppConfigScreen.getDriver()}.
 */
public class SauceLabsLoginScreen {

    private static final Duration TIMEOUT = Duration.ofSeconds(20);

    private final By txtUsuario =
            AppiumBy.xpath("//android.widget.EditText[@content-desc=\"test-Username\"]");

    private final By txtClave =
            AppiumBy.xpath("//android.widget.EditText[@content-desc=\"test-Password\"]");

    private final By btnLogin =
            AppiumBy.accessibilityId("test-LOGIN");

    // Elemento que solo existe en la pantalla de productos, tras un login exitoso
    private final By tituloProductos =
            AppiumBy.accessibilityId("test-PRODUCTS");

    public void escribirUsuario(String usuario) {
        System.out.println("[SauceLabsLoginScreen] Escribiendo usuario: " + usuario);

        AndroidDriver driver = getDriver();
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);

        WebElement campoUsuario = wait.until(ExpectedConditions.presenceOfElementLocated(txtUsuario));
        campoUsuario.sendKeys(usuario);
    }

    public void escribirClave(String clave) {
        System.out.println("[SauceLabsLoginScreen] Escribiendo clave...");

        AndroidDriver driver = getDriver();
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);

        WebElement campoClave = wait.until(ExpectedConditions.presenceOfElementLocated(txtClave));
        campoClave.sendKeys(clave);
    }

    public void clicEnLogin() {
        System.out.println("[SauceLabsLoginScreen] Haciendo clic en el boton LOGIN");

        AndroidDriver driver = getDriver();
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);

        WebElement botonLogin = wait.until(ExpectedConditions.presenceOfElementLocated(btnLogin));
        botonLogin.click();
    }

    public boolean estaEnPantallaDeProductos() {
        System.out.println("[SauceLabsLoginScreen] Validando que se llego a la pantalla de productos...");

        AndroidDriver driver = getDriver();
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);

        WebElement titulo = wait.until(ExpectedConditions.presenceOfElementLocated(tituloProductos));
        boolean visible = titulo.isDisplayed();

        System.out.println("[SauceLabsLoginScreen] Pantalla de productos visible: " + visible);
        return visible;
    }
}
