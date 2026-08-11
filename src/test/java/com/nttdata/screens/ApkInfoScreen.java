package com.nttdata.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page Object de la app "APK Info".
 * Su unica responsabilidad es interactuar con los elementos de esta
 * pantalla (buscar texto, leer resultados, etc). NO conoce nada sobre
 * como se levanta o se cierra la app.
 */
public class ApkInfoScreen extends BaseScreen {

    private final By toolbar = AppiumBy.id("com.wt.apkinfo:id/toolbar");
    private final By cajaBusqueda = AppiumBy.id("com.wt.apkinfo:id/searchEdit");

    public void validarPantallaInicial() {
        waitForVisible(toolbar);
    }

    public void buscarTexto(String texto) {
        WebElement elementoToolbar = waitForVisible(toolbar);
        elementoToolbar.click();

        WebElement inputBusqueda = waitForVisible(cajaBusqueda);
        inputBusqueda.click();
        inputBusqueda.sendKeys(texto);

        driver().pressKey(new KeyEvent(AndroidKey.ENTER));
    }
}
