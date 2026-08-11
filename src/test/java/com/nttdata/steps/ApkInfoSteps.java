package com.nttdata.steps;

import com.nttdata.driver.AppCapabilities;
import com.nttdata.driver.AppiumDriverManager;
import com.nttdata.screens.ApkInfoScreen;

/**
 * Orquesta el caso de uso completo de la app "APK Info":
 * iniciar la app, interactuar con su pantalla y cerrarla.
 * <p>
 * Esta clase es la que conoce QUE app se va a probar (su archivo de
 * configuracion y su appId). Las Screens no saben nada de esto.
 */
public class ApkInfoSteps {

    private static final String ARCHIVO_CONFIGURACION = "apkinfo.properties";
    private static final String APP_ID = "com.wt.apkinfo";

    private final ApkInfoScreen screen = new ApkInfoScreen();

    public void iniciarAplicacion() {
        AppCapabilities capabilities = AppCapabilities.from(ARCHIVO_CONFIGURACION);
        AppiumDriverManager.getInstance().startApp(capabilities);
    }

    public void validarPantallaInicial() {
        screen.validarPantallaInicial();
    }

    public void buscarTexto(String texto) {
        screen.buscarTexto(texto);
    }

    public void cerrarAplicacion() {
        AppiumDriverManager.getInstance().stopApp(APP_ID);
    }
}
