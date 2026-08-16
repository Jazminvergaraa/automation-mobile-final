package com.nttdata.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nttdata.screens.AppConfigScreen.getDriver;

/**
 * Pantalla "Products" (galeria/catalogo) de My Demo App.
 * <p>
 * IDs verificados contra el codigo fuente oficial de la app
 * (fragment_product_catalog.xml, item_products.xml, menu_header_layout.xml):
 * - Titulo de la pantalla: id/productTV (texto estatico "Products")
 * - Grilla de productos: RecyclerView id/productRV (GridLayoutManager)
 * - Cada item NO tiene un id de contenedor propio; se identifica por sus
 *   hijos: id/titleTV (nombre) e id/productIV (imagen, es lo unico clickeable
 *   para abrir el detalle, segun ProductsAdapter.onBindViewHolder).
 * - Icono/zona clickeable del carrito: id/cartRL (contiene a cartIV y cartTV).
 * - Contador de items en el carrito: id/cartTV.
 */
public class ProductsScreen {

    private static final Duration TIMEOUT = Duration.ofSeconds(20);
    private static final int MIN_PRODUCTOS_ESPERADOS = 3;
    private static final int MAX_INTENTOS_SCROLL = 4;

    private static final String PKG = "com.saucelabs.mydemoapp.android";

    private final By screenTitle = AppiumBy.id(PKG + ":id/productTV");
    private final By productTitles = AppiumBy.id(PKG + ":id/titleTV");
    private final By productImages = AppiumBy.id(PKG + ":id/productIV");
    private final By cartButton = AppiumBy.id(PKG + ":id/cartRL");
    private final By cartBadgeCounter = AppiumBy.id(PKG + ":id/cartTV");

    public boolean isProductsScreenDisplayed() {
        try {
            esperarVisible(screenTitle);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Espera a que la galeria cargue al menos {@link #MIN_PRODUCTOS_ESPERADOS}
     * productos y devuelve sus nombres. Si el APK esta inestable y la
     * grilla no renderiza nada (o renderiza menos de lo esperado), esta
     * espera falla con TimeoutException, lo cual es la señal que buscamos.
     */
    public List<String> validarGaleriaCargada() {
        System.out.println("[ProductsScreen] Validando carga de la galeria de productos...");
        System.out.println("[ProductsScreen] Activity actual: " + getDriver().currentActivity());
        System.out.println("[ProductsScreen] Package actual: " + getDriver().getCurrentPackage());

        esperarVisible(screenTitle);

        WebDriverWait wait = new WebDriverWait(getDriver(), TIMEOUT);
        List<WebElement> nombres = wait.until(d -> {
            List<WebElement> encontrados = d.findElements(productTitles);
            return encontrados.size() >= MIN_PRODUCTOS_ESPERADOS ? encontrados : null;
        });

        List<String> textos = nombres.stream()
                .map(WebElement::getText)
                .filter(nombre -> nombre != null && !nombre.trim().isEmpty())
                .collect(Collectors.toList());

        System.out.println("[ProductsScreen] Productos detectados: " + textos);
        return textos;
    }

    /**
     * Ubica el producto por nombre (haciendo scroll si aun no esta
     * renderizado por el RecyclerView) y hace click sobre su imagen,
     * que es el unico elemento clickeable del item segun el codigo
     * fuente de la app.
     */
    public void abrirProducto(String nombreProducto) {
        System.out.println("[ProductsScreen] Abriendo producto: " + nombreProducto);

        WebElement imagenProducto = localizarImagenProducto(nombreProducto);
        new WebDriverWait(getDriver(), TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(imagenProducto))
                .click();
    }

    private WebElement localizarImagenProducto(String nombreProducto) {
        for (int intento = 0; intento <= MAX_INTENTOS_SCROLL; intento++) {
            List<WebElement> nombres = getDriver().findElements(productTitles);
            List<WebElement> imagenes = getDriver().findElements(productImages);
            int total = Math.min(nombres.size(), imagenes.size());

            for (int i = 0; i < total; i++) {
                if (nombres.get(i).getText().trim().equalsIgnoreCase(nombreProducto)) {
                    return imagenes.get(i);
                }
            }

            if (intento == MAX_INTENTOS_SCROLL) {
                break;
            }
            System.out.println("[ProductsScreen] '" + nombreProducto
                    + "' no visible aun, haciendo scroll (" + (intento + 1) + "/" + MAX_INTENTOS_SCROLL + ")...");
            scrollHaciaAbajo();
        }

        throw new RuntimeException(
                "[ProductsScreen] Producto no encontrado en la galeria (ni tras hacer scroll): " + nombreProducto);
    }

    private void scrollHaciaAbajo() {
        Dimension size = getDriver().manage().window().getSize();
        Map<String, Object> params = new HashMap<>();
        params.put("left", (int) (size.width * 0.1));
        params.put("top", (int) (size.height * 0.2));
        params.put("width", (int) (size.width * 0.8));
        params.put("height", (int) (size.height * 0.6));
        params.put("direction", "down");
        params.put("percent", 0.8);
        getDriver().executeScript("mobile: scrollGesture", params);
    }

    public void irAlCarrito() {
        System.out.println("[ProductsScreen] Abriendo el carrito...");
        WebElement boton = new WebDriverWait(getDriver(), TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(cartButton));
        boton.click();
    }

    /** Vuelve de la pantalla de carrito a la galeria usando el back del sistema. */
    public void regresarDesdeCarrito() {
        getDriver().navigate().back();
        esperarVisible(screenTitle);
    }

    public int obtenerContadorCarrito() {
        AndroidDriver driver = getDriver();
        List<WebElement> badges = driver.findElements(cartBadgeCounter);
        if (badges.isEmpty()) {
            return 0;
        }
        String texto = badges.get(0).getText().trim();
        int total = texto.isEmpty() ? 0 : Integer.parseInt(texto.replaceAll("[^0-9]", ""));
        System.out.println("[ProductsScreen] Contador del carrito = " + total);
        return total;
    }

    private WebElement esperarVisible(By locator) {
        return new WebDriverWait(getDriver(), TIMEOUT)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }
}