package com.nttdata.screens;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static com.nttdata.screens.AppConfigScreen.getDriver;

/**
 * Pantalla "My Cart" de My Demo App.
 * <p>
 * IDs verificados contra fragment_cart.xml e item_my_cart.xml:
 * - Titulo de la pantalla: id/productTV (texto estatico "My Cart";
 *   ojo: es el MISMO resource-id que en Products y en el detalle, pero
 *   cada pantalla se muestra en su propio fragmento, por lo que no hay
 *   ambiguedad en tiempo de ejecucion).
 * - Cada item del carrito NO tiene id de contenedor propio. Se lee por
 *   sus hijos: id/titleTV (nombre del producto) e id/noTV (cantidad),
 *   que aparecen en el mismo orden dentro del arbol para un mismo item.
 * - id/removeBt: boton "REMOVE" de cada item (usado para dejar el
 *   carrito limpio antes de cada escenario).
 */
public class CartScreen {

    private static final Duration TIMEOUT = Duration.ofSeconds(20);
    private static final String PKG = "com.saucelabs.mydemoapp.android";

    private final By cartTitle = AppiumBy.id(PKG + ":id/productTV");
    private final By emptyCartTitle = AppiumBy.id(PKG + ":id/noItemTitleTV");
    private final By itemTitles = AppiumBy.id(PKG + ":id/titleTV");
    private final By itemQuantities = AppiumBy.id(PKG + ":id/noTV");
    private final By removeButtons = AppiumBy.id(PKG + ":id/removeBt");

    public boolean isCartScreenDisplayed() {
        try {
            validarPantallaCargada();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Espera a que la pantalla de carrito termine de cargar, sea que
     * tenga productos o este vacia.
     * <p>
     * OJO: CartFragment.setData() alterna la visibilidad de dos
     * contenedores segun el estado (fragment_cart.xml):
     * - Carrito CON productos: id/cartCL visible (contiene productTV = "My Cart").
     * - Carrito VACIO: id/noItemCL visible (contiene noItemTitleTV = "No Items"),
     *   y en ese caso id/productTV NO existe en el arbol (esta en GONE).
     * Por eso esperar solo por "productTV" fallaba con TimeoutException
     * cuando el carrito estaba legitimamente vacio (ej: justo despues de
     * abrir la app por primera vez). Se espera por cualquiera de los dos.
     */
    public void validarPantallaCargada() {
        new WebDriverWait(getDriver(), TIMEOUT).until(driver ->
                !driver.findElements(cartTitle).isEmpty() || !driver.findElements(emptyCartTitle).isEmpty());
    }

    public boolean carritoEstaVacio() {
        return !getDriver().findElements(emptyCartTitle).isEmpty();
    }

    public int getCartItemCount() {
        return getDriver().findElements(itemTitles).size();
    }

    /**
     * Busca el producto por nombre entre los items del carrito y valida
     * que su cantidad mostrada coincida con la cantidad esperada. Esto
     * cubre no solo "esta en el carrito" sino "esta con la cantidad
     * correcta", que es justamente el tipo de bug sutil que un APK
     * inestable puede introducir (ej: boton + que no incrementa, o que
     * el carrito duplique/pierda unidades al agregar).
     */
    public boolean contieneProductoConCantidad(String nombreProducto, int unidadesEsperadas) {
        List<WebElement> titulos = getDriver().findElements(itemTitles);
        List<WebElement> cantidades = getDriver().findElements(itemQuantities);
        int total = Math.min(titulos.size(), cantidades.size());

        for (int i = 0; i < total; i++) {
            if (titulos.get(i).getText().trim().equalsIgnoreCase(nombreProducto)) {
                int cantidadEnCarrito;
                try {
                    cantidadEnCarrito = Integer.parseInt(leerTexto(cantidades.get(i)).trim());
                } catch (NumberFormatException e) {
                    System.out.println("[CartScreen] No se pudo leer la cantidad del item '" + nombreProducto + "'");
                    return false;
                }
                System.out.println("[CartScreen] '" + nombreProducto + "' encontrado en el carrito con cantidad = "
                        + cantidadEnCarrito + " (esperado = " + unidadesEsperadas + ")");
                return cantidadEnCarrito == unidadesEsperadas;
            }
        }

        System.out.println("[CartScreen] '" + nombreProducto + "' NO aparece en el carrito.");
        return false;
    }

    /**
     * Lee el texto de un elemento con fallback al atributo "text" del
     * accessibility tree. UiAutomator2 a veces devuelve getText() vacio
     * en el primer intento aunque el TextView ya tenga contenido.
     */
    private String leerTexto(WebElement el) {
        String texto = el.getText();
        if (texto == null || texto.trim().isEmpty()) {
            String atributo = el.getAttribute("text");
            if (atributo != null && !atributo.trim().isEmpty()) {
                texto = atributo;
            }
        }
        return texto == null ? "" : texto;
    }

    /**
     * Vacia el carrito quitando todos los items existentes. Se usa antes
     * de cada escenario para asegurar un estado inicial limpio (el
     * proyecto usa noReset=true para evitar reinstalar el APK entre
     * corridas, por lo que el carrito puede traer estado de una
     * ejecucion anterior si no se limpia explicitamente).
     */
    public void vaciarCarritoSiTieneProductos() {
        List<WebElement> botones = getDriver().findElements(removeButtons);
        int seguridad = 0;

        while (!botones.isEmpty() && seguridad < 20) {
            System.out.println("[CartScreen] Vaciando carrito, quedan " + botones.size() + " item(s)...");
            new WebDriverWait(getDriver(), TIMEOUT)
                    .until(ExpectedConditions.elementToBeClickable(botones.get(0)))
                    .click();
            botones = getDriver().findElements(removeButtons);
            seguridad++;
        }
    }

}