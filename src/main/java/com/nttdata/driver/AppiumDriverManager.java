package com.nttdata.driver;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * UNICA clase del framework responsable de:
 * <ul>
 *     <li>Levantar la aplicacion (crear la sesion de Appium)</li>
 *     <li>Exponer el driver activo a las pantallas (Screens)</li>
 *     <li>Cerrar la aplicacion y la sesion de Appium</li>
 * </ul>
 * <p>
 * Implementa el patron <b>Singleton</b>: durante la ejecucion de un
 * escenario solo existe una instancia del manager y, por lo tanto,
 * una unica sesion de driver activa a la vez. Esto evita crear
 * sesiones duplicadas y centraliza el ciclo de vida del driver en
 * un solo lugar, en vez de repetirlo en cada Screen o Step.
 * <p>
 * Ninguna otra clase del framework deberia crear un {@link AndroidDriver}
 * directamente: todas obtienen el driver a traves de {@link #getDriver()}.
 */
public final class AppiumDriverManager {

    private static final AppiumDriverManager INSTANCE = new AppiumDriverManager();

    private AndroidDriver driver;

    private AppiumDriverManager() {
        // Constructor privado: evita que se cree la clase desde fuera (Singleton)
    }

    public static AppiumDriverManager getInstance() {
        return INSTANCE;
    }

    /**
     * Levanta la aplicacion descrita por {@code appCapabilities}.
     * Si ya existe una sesion activa, se reutiliza en vez de crear una nueva.
     */
    public AndroidDriver startApp(AppCapabilities appCapabilities) {

        if (driver != null) {
            System.out.println("Ya existe una sesion Appium activa, se reutiliza.");
            return driver;
        }

        try {
            System.out.println("Iniciando sesion Appium...");

            driver = new AndroidDriver(
                    new URL(appCapabilities.getAppiumUrl()),
                    appCapabilities.toUiAutomator2Options()
            );

            System.out.println("Sesion Appium creada: " + driver.getSessionId());

        } catch (MalformedURLException e) {
            throw new RuntimeException("URL de Appium invalida: " + appCapabilities.getAppiumUrl(), e);
        }

        return driver;
    }

    /**
     * Entrega el driver de la sesion activa.
     * Debe llamarse SIEMPRE despues de {@link #startApp(AppCapabilities)}.
     */
    public AndroidDriver getDriver() {
        if (driver == null) {
            throw new IllegalStateException(
                    "El driver de Appium no ha sido inicializado. " +
                            "Debes iniciar la app antes de interactuar con la pantalla."
            );
        }
        return driver;
    }

    /**
     * Cierra la app indicada por su appId (package) y finaliza la sesion de Appium.
     *
     * @param appId package de la aplicacion a terminar, ej: "com.wt.apkinfo"
     */
    public void stopApp(String appId) {

        if (driver == null) {
            return;
        }

        try {
            System.out.println("Cerrando aplicacion: " + appId);
            driver.executeScript("mobile: terminateApp", Map.of("appId", appId));

        } finally {
            System.out.println("Cerrando sesion Appium...");
            driver.quit();
            driver = null;
            System.out.println("Sesion Appium cerrada correctamente.");
        }
    }

    /**
     * Toma un screenshot de la sesion activa.
     * Devuelve un arreglo vacio si no hay sesion (nunca lanza excepcion),
     * para poder usarse de forma segura en hooks de Cucumber.
     */
    public byte[] takeScreenshot() {

        if (driver == null) {
            return new byte[0];
        }

        try {
            return driver.getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            System.out.println("No se pudo tomar el screenshot: " + e.getMessage());
            return new byte[0];
        }
    }
}
