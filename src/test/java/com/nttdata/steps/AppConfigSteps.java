package com.nttdata.steps;

import com.nttdata.screens.AppConfigScreen;

/**
 * Caso de uso GENERICO: iniciar, cerrar y tomar screenshot de
 * cualquier aplicacion movil.
 * <p>
 * No conoce elementos de UI de ninguna app en particular; para eso
 * existen las Screens especificas (ej: SauceLabsLoginScreen), cada
 * una con su propia clase de Steps (ej: SauceLabsLoginSteps).
 */
public class AppConfigSteps {

    private final AppConfigScreen appConfigScreen = new AppConfigScreen();

    public void iniciarAplicacion(String archivoConfiguracion) {
        System.out.println("[AppConfigSteps] Solicitando inicio de aplicacion (" + archivoConfiguracion + ")");
        appConfigScreen.iniciarAplicacion(archivoConfiguracion);
    }

    public void cerrarAplicacion(String appId) {
        System.out.println("[AppConfigSteps] Solicitando cierre de aplicacion (" + appId + ")");
        appConfigScreen.cerrarAplicacion(appId);
    }

    public byte[] tomarScreenshot() {
        return appConfigScreen.tomarScreenshot();
    }
}
