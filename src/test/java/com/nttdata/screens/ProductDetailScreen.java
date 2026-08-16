package com.nttdata.screens;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static com.nttdata.screens.AppConfigScreen.getDriver;

/**
 * Pantalla de detalle de producto de My Demo App.
 * <p>
 * IDs verificados contra fragment_product_detail.xml y
 * ProductDetailFragment.setData():
 * - id/productTV: en el XML arranca con el texto estatico "Products", pero
 *   ProductDetailFragment.setData() lo sobreescribe de forma ASINCRONA
 *   (via LiveData/Observer) con el nombre real del producto seleccionado.
 *   Por eso NO basta con esperar que el elemento este presente: hay que
 *   esperar a que su texto deje de ser el placeholder y quede en el
 *   nombre esperado. Esta es la espera explicita clave de esta pantalla.
 * - id/plusIV / id/minusIV: ImageView de + / - (NO son Button).
 * - id/noTV: cantidad actual seleccionada.
 * - id/cartBt: boton "Add To Cart".
 */
public class ProductDetailScreen {

    private static final Duration TIMEOUT = Duration.ofSeconds(20);
    private static final Duration TIMEOUT_CORTO = Duration.ofSeconds(5);
    private static final String PKG = "com.saucelabs.mydemoapp.android";
    private static final String PLACEHOLDER_INICIAL = "Products";
    private static final int MAX_INTENTOS_POR_CLICK = 3;

    private final By productNameLabel = AppiumBy.id(PKG + ":id/productTV");
    private final By plusButton = AppiumBy.id(PKG + ":id/plusIV");
    private final By quantityLabel = AppiumBy.id(PKG + ":id/noTV");
    private final By addToCartButton = AppiumBy.id(PKG + ":id/cartBt");

    public boolean isProductDetailDisplayed() {
        try {
            esperarVisible(productNameLabel);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Espera activamente a que el binding asincrono del detalle termine
     * (el nombre deja de ser el placeholder "Products") y devuelve el
     * nombre de producto realmente cargado en pantalla.
     * <p>
     * Si esta espera vence, NO es (necesariamente) lentitud: puede
     * significar que la app nunca navego a esta pantalla (crash o
     * falla de navegacion al abrir el detalle del producto). Por eso
     * se relanza con un mensaje explicito en vez de dejar pasar el
     * TimeoutException crudo de Selenium, y se registra el package
     * activo para diagnosticar si la app sigue viva.
     */
    public String obtenerNombreProducto() {
        try {
            new WebDriverWait(getDriver(), TIMEOUT).until(driver -> {
                List<WebElement> el = driver.findElements(productNameLabel);
                if (el.isEmpty()) {
                    return false;
                }
                String texto = el.get(0).getText().trim();
                return !texto.isEmpty() && !texto.equalsIgnoreCase(PLACEHOLDER_INICIAL);
            });
        } catch (TimeoutException e) {
            String paqueteActual;
            try {
                paqueteActual = getDriver().getCurrentPackage();
            } catch (Exception ignored) {
                paqueteActual = "desconocido (no se pudo consultar)";
            }
            throw new AssertionError(
                    "[ProductDetailScreen] La app no navego a la pantalla de detalle del producto "
                            + "(el titulo se quedo en '" + PLACEHOLDER_INICIAL + "' tras " + TIMEOUT.getSeconds()
                            + "s). Posible crash o falla de navegacion del APK al abrir el detalle. "
                            + "Package activo en ese momento: " + paqueteActual, e);
        }
        return esperarVisible(productNameLabel).getText();
    }

    /**
     * Incrementa la cantidad con el boton "+" hasta llegar a
     * {@code unidadesEsperadas} y valida (con espera explicita) que el
     * contador en pantalla realmente haya quedado en ese valor antes de
     * agregar al carrito.
     * <p>
     * El valor por defecto de id/noTV al abrir el detalle es "1" (texto
     * estatico en el XML), asi que solo se interactua con el boton "+"
     * cuando unidadesEsperadas > 1.
     * <p>
     * Cada click en "+" se hace con reintentos cortos: si tras un click
     * el contador no sube (posible tap perdido por un ImageView pequeno
     * en el emulador), se reintenta el click hasta {@link #MAX_INTENTOS_POR_CLICK}
     * veces antes de concluir que el boton esta realmente roto.
     */
    public void ajustarCantidadYAgregarAlCarrito(int unidadesEsperadas) {
        System.out.println("[ProductDetailScreen] Ajustando cantidad a " + unidadesEsperadas + " unidad(es)");

        if (unidadesEsperadas > 1) {
            int cantidadActual = leerCantidadActual();
            System.out.println("[ProductDetailScreen] Cantidad inicial en pantalla = " + cantidadActual);

            while (cantidadActual < unidadesEsperadas) {
                int cantidadObjetivoDeEsteClick = cantidadActual + 1;
                boolean incremento = false;

                for (int intento = 1; intento <= MAX_INTENTOS_POR_CLICK && !incremento; intento++) {
                    esperarClickeable(plusButton).click();
                    incremento = esperarIncremento(cantidadActual, TIMEOUT_CORTO);
                    if (!incremento) {
                        System.out.println("[ProductDetailScreen] El click en '+' no incremento el contador "
                                + "(intento " + intento + "/" + MAX_INTENTOS_POR_CLICK + "), reintentando...");
                    }
                }

                if (!incremento) {
                    throw new AssertionError(
                            "[ProductDetailScreen] El boton '+' no incremento el contador de " + cantidadActual
                                    + " a " + cantidadObjetivoDeEsteClick + " tras " + MAX_INTENTOS_POR_CLICK
                                    + " intentos. Posible falla real del boton '+' en el APK.");
                }

                cantidadActual = leerCantidadActual();
            }

            if (cantidadActual != unidadesEsperadas) {
                throw new AssertionError(
                        "[ProductDetailScreen] El contador de cantidad quedo en " + cantidadActual
                                + ", se esperaba " + unidadesEsperadas + ".");
            }
        }

        System.out.println("[ProductDetailScreen] Agregando al carrito...");
        esperarClickeable(addToCartButton).click();
    }

    private int leerCantidadActual() {
        List<WebElement> el = getDriver().findElements(quantityLabel);
        if (el.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(leerTexto(el.get(0)).trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean esperarIncremento(int valorAnterior, Duration timeout) {
        try {
            new WebDriverWait(getDriver(), timeout).until(driver -> leerCantidadActual() > valorAnterior);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Lee el texto de un elemento con fallback al atributo "text" del
     * accessibility tree. UiAutomator2 a veces devuelve getText() vacio
     * en el primer intento aunque el TextView ya tenga contenido; el
     * atributo "text" es mas confiable para TextViews simples como noTV.
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

    private WebElement esperarVisible(By locator) {
        return new WebDriverWait(getDriver(), TIMEOUT).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private WebElement esperarClickeable(By locator) {
        return new WebDriverWait(getDriver(), TIMEOUT).until(ExpectedConditions.elementToBeClickable(locator));
    }
}