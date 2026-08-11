package com.nttdata.screens;

import com.nttdata.driver.AppiumDriverManager;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Clase base para todas las pantallas (Screens / Page Objects).
 * <p>
 * Una "Screen" NUNCA levanta ni cierra la aplicacion: solo sabe
 * interactuar con los elementos que ve en pantalla. El ciclo de
 * vida del driver es responsabilidad exclusiva de
 * {@link AppiumDriverManager}.
 */
public abstract class BaseScreen {

    protected static final Duration TIMEOUT = Duration.ofSeconds(20);

    /**
     * Obtiene el driver de la sesion activa. Siempre se pide "al vuelo"
     * (nunca se guarda como campo) para evitar quedarse con una
     * referencia a una sesion ya cerrada.
     */
    protected AndroidDriver driver() {
        return AppiumDriverManager.getInstance().getDriver();
    }

    protected WebElement waitForVisible(By locator) {
        return new WebDriverWait(driver(), TIMEOUT)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }
}
