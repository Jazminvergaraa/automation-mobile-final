package com.nttdata.driver;

import io.appium.java_client.android.options.UiAutomator2Options;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Objects;
import java.util.Properties;

/**
 * Representa la configuracion (capabilities) de UNA app movil especifica.
 * <p>
 * Toda la informacion que cambia de una app a otra (paquete, activity,
 * ruta del APK, dispositivo, etc.) vive en un archivo .properties dentro
 * de {@code src/test/resources/config}.
 * <p>
 * Gracias a esto, para automatizar una nueva app o APK NO se toca
 * codigo Java: solo se agrega un nuevo archivo de configuracion, por
 * ejemplo {@code miNuevaApp.properties}, y se referencia desde la clase
 * de steps correspondiente.
 */
public final class AppCapabilities {

    private final Properties properties = new Properties();
    private final String sourceFile;

    private AppCapabilities(String propertiesFileName) {
        this.sourceFile = propertiesFileName;
        String resourcePath = "config/" + propertiesFileName;

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath)) {

            if (input == null) {
                throw new IllegalStateException(
                        "No se encontro el archivo de configuracion '" + resourcePath +
                                "' en src/test/resources/config"
                );
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Error leyendo el archivo de configuracion: " + resourcePath, e);
        }
    }

    /**
     * Factory method: crea la configuracion de capabilities a partir
     * del nombre de un archivo .properties ubicado en config/.
     *
     * @param propertiesFileName por ejemplo "saucelabs.properties"
     */
    public static AppCapabilities from(String propertiesFileName) {
        return new AppCapabilities(propertiesFileName);
    }

    public String getAppiumUrl() {
        return getRequired("appium.url");
    }

    public String getAppPackage() {
        return getRequired("appPackage");
    }

    /**
     * Construye las UiAutomator2Options a partir de las propiedades
     * cargadas. Si en el futuro se necesitan capabilities adicionales
     * para una app puntual, se agregan aqui como properties nuevas.
     */
    public UiAutomator2Options toUiAutomator2Options() {
        UiAutomator2Options options = new UiAutomator2Options();

        options.setPlatformName(properties.getProperty("platformName", "Android"));
        options.setAutomationName(properties.getProperty("automationName", "UiAutomator2"));
        options.setUdid(getRequired("udid"));
        options.setDeviceName(getRequired("deviceName"));
        options.setPlatformVersion(getRequired("platformVersion"));
        options.setApp(getRequired("app"));
        options.setAppPackage(getRequired("appPackage"));
        options.setAppActivity(getRequired("appActivity"));
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

    private String getRequired(String key) {
        String value = properties.getProperty(key);
        return Objects.requireNonNull(
                value,
                "Falta la propiedad obligatoria '" + key + "' en " + sourceFile
        );
    }
}
