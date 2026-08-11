package com.nttdata.steps;

import com.nttdata.screens.SauceLabsLoginScreen;

/**
 * Caso de uso de login en la app movil de Sauce Labs.
 * Delega toda la interaccion de UI en {@link SauceLabsLoginScreen}.
 * No sabe nada de como se levanta o se cierra la app: eso lo maneja
 * {@link AppConfigSteps} / {@code AppConfigScreen}.
 */
public class SauceLabsLoginSteps {

    private final SauceLabsLoginScreen screen = new SauceLabsLoginScreen();

    public void ingresarUsuario(String usuario) {
        System.out.println("[SauceLabsLoginSteps] ingresarUsuario -> " + usuario);
        screen.escribirUsuario(usuario);
    }

    public void ingresarClave(String clave) {
        System.out.println("[SauceLabsLoginSteps] ingresarClave");
        screen.escribirClave(clave);
    }

    public void clicEnLogin() {
        System.out.println("[SauceLabsLoginSteps] clicEnLogin");
        screen.clicEnLogin();
    }

    public boolean loginFueExitoso() {
        System.out.println("[SauceLabsLoginSteps] loginFueExitoso: validando resultado...");
        return screen.estaEnPantallaDeProductos();
    }
}
