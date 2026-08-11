package com.nttdata.screens;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.OutputType;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * AppConfigScreen
 * ---------------
 * UNICA responsabilidad de esta clase: levantar y cerrar la aplicacion
 * (es decir, crear y finalizar la sesion de Appium).
 * <p>
 * Esta clase NO conoce ningun elemento de UI (botones, campos, etc).
 * Eso es responsabilidad de cada Screen especifica de cada app
 * (por ejemplo {@code SauceLabsLoginScreen}).
 * <p>
 * Es GENERICA: sirve para levantar cualquier aplicacion Android.
 * Para automatizar una app nueva NO se modifica esta clase: solo se
 * crea un nuevo archivo {@code .properties} dentro de
 * {@code src/test/resources/config} con sus capabilities
 * (app, appPackage, appActivity, udid, etc.) y se indica su nombre
 * al llamar a {@link #iniciarAplicacion(String)}.
 */
public class AppConfigScreen {

    // El driver es estatico: solo existe UNA sesion de Appium activa
    // a la vez, sin importar cuantas Screens se usen despues.
    private static AndroidDriver driver;

    /**
     * Levanta la aplicacion cuyas capabilities estan definidas en el
     * archivo de configuracion indicado.
     *
     * @param archivoConfiguracion nombre del archivo .properties dentro de
     *                              src/test/resources/config, ej: "saucelabs.properties"
     */
    public void iniciarAplicacion(String archivoConfiguracion) {

        if (driver != null) {
            System.out.println("[AppConfigScreen] Ya existe una sesion Appium activa, se reutiliza.");
            return;
        }

        System.out.println("[AppConfigScreen] Leyendo configuracion desde: " + archivoConfiguracion);
        Properties properties = cargarPropiedades(archivoConfiguracion);

        try {
            System.out.println("[AppConfigScreen] Construyendo capabilities...");
            UiAutomator2Options options = construirOptions(properties);
            String appiumUrl = obtenerObligatorio(properties, "appium.url");

            System.out.println("[AppConfigScreen] Conectando a Appium Server en: " + appiumUrl);
            driver = new AndroidDriver(new URL(appiumUrl), options);

            System.out.println("[AppConfigScreen] Sesion Appium creada correctamente. SessionId: " + driver.getSessionId());

        } catch (MalformedURLException e) {
            throw new RuntimeException("[AppConfigScreen] URL de Appium invalida", e);
        }
    }

    /**
     * Entrega el driver de la sesion activa a cualquier Screen que
     * necesite interactuar con la app (ej: SauceLabsLoginScreen).
     */
    public static AndroidDriver getDriver() {
        if (driver == null) {
            throw new IllegalStateException(
                    "[AppConfigScreen] El driver no ha sido inicializado. " +
                            "Debes llamar a iniciarAplicacion(...) antes de interactuar con la pantalla."
            );
        }
        return driver;
    }

    /**
     * Cierra la aplicacion indicada por su appId (package) y finaliza
     * la sesion de Appium.
     *
     * @param appId package de la app a cerrar, ej: "com.swaglabsmobileapp"
     */
    public void cerrarAplicacion(String appId) {

        if (driver == null) {
            System.out.println("[AppConfigScreen] No hay sesion activa para cerrar.");
            return;
        }

        try {
            System.out.println("[AppConfigScreen] Cerrando aplicacion: " + appId);
            driver.executeScript("mobile: terminateApp", Map.of("appId", appId));

        } finally {
            System.out.println("[AppConfigScreen] Cerrando sesion Appium...");
            driver.quit();
            driver = null;
            System.out.println("[AppConfigScreen] Sesion Appium cerrada correctamente.");
        }
    }

    public byte[] tomarScreenshot() {

        if (driver == null) {
            System.out.println("[AppConfigScreen] No hay sesion activa, no se puede tomar screenshot.");
            return new byte[0];
        }

        System.out.println("[AppConfigScreen] Tomando screenshot...");
        return driver.getScreenshotAs(OutputType.BYTES);
    }

    // =========================================================
    // Lectura de configuracion y construccion de capabilities
    // =========================================================

    private Properties cargarPropiedades(String archivoConfiguracion) {
        Properties properties = new Properties();
        String resourcePath = "config/" + archivoConfiguracion;

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath)) {

            if (input == null) {
                throw new IllegalStateException(
                        "[AppConfigScreen] No se encontro el archivo de configuracion '" + resourcePath +
                                "' en src/test/resources/config"
                );
            }

            properties.load(input);
            return properties;

        } catch (IOException e) {
            throw new RuntimeException("[AppConfigScreen] Error leyendo el archivo de configuracion: " + resourcePath, e);
        }
    }

    private UiAutomator2Options construirOptions(Properties properties) {
        UiAutomator2Options options = new UiAutomator2Options();

        options.setPlatformName(properties.getProperty("platformName", "Android"));
        options.setAutomationName(properties.getProperty("automationName", "UiAutomator2"));
        options.setUdid(obtenerObligatorio(properties, "udid"));
        options.setDeviceName(obtenerObligatorio(properties, "deviceName"));
        options.setPlatformVersion(obtenerObligatorio(properties, "platformVersion"));
        options.setApp(obtenerObligatorio(properties, "app"));
        options.setAppPackage(obtenerObligatorio(properties, "appPackage"));
        options.setAppActivity(obtenerObligatorio(properties, "appActivity"));
        options.setNoReset(Boolean.parseBoolean(properties.getProperty("noReset", "true")));
        options.setAutoGrantPermissions(Boolean.parseBoolean(properties.getProperty("autoGrantPermissions", "true")));

        options.setCapability("appium:forceAppLaunch", true);
        options.setCapability("appium:settings[waitForIdleTimeout]", 1000);
        options.setCapability("appium:disableWindowAnimation", true);
        options.setCapability("appium:skipLogcatCapture", true);

        long timeoutSeconds = Long.parseLong(properties.getProperty("newCommandTimeoutSeconds", "300"));
        options.setNewCommandTimeout(Duration.ofSeconds(timeoutSeconds));

        return options;
    }

    private String obtenerObligatorio(Properties properties, String key) {
        String value = properties.getProperty(key);
        return Objects.requireNonNull(value, "[AppConfigScreen] Falta la propiedad obligatoria '" + key + "'");
    }
}
