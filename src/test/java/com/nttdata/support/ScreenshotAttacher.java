package com.nttdata.support;

import com.nttdata.steps.AppConfigSteps;
import io.cucumber.java.Scenario;

/**
 * Utilidad para adjuntar screenshots a un {@link Scenario} de Cucumber.
 * Se agrupa aqui para no repetir esta logica en cada clase de step
 * definitions.
 */
public final class ScreenshotAttacher {

    private static final AppConfigSteps appConfigSteps = new AppConfigSteps();

    private ScreenshotAttacher() {
        // Clase de utilidades: no se instancia
    }

    public static void attach(Scenario scenario, String nombre) {
        byte[] screenshot = appConfigSteps.tomarScreenshot();

        if (screenshot.length > 0) {
            System.out.println("[ScreenshotAttacher] Adjuntando evidencia: " + nombre);
            scenario.attach(screenshot, "image/png", nombre);
        } else {
            System.out.println("[ScreenshotAttacher] No se genero evidencia para: " + nombre);
        }
    }
}
