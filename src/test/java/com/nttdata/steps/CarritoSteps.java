package com.nttdata.steps;

import com.nttdata.screens.CartScreen;
import com.nttdata.screens.ProductDetailScreen;
import com.nttdata.screens.ProductsScreen;

import java.util.List;

public class CarritoSteps {

    private final ProductsScreen productsScreen = new ProductsScreen();
    private final ProductDetailScreen productDetailScreen = new ProductDetailScreen();
    private final CartScreen cartScreen = new CartScreen();

    /**
     * Deja el carrito en estado limpio antes de validar la galeria.
     * Necesario porque el proyecto usa noReset=true (no reinstala el
     * APK entre corridas), asi que el carrito podria traer productos
     * de una ejecucion anterior y contaminar la validacion de cantidad.
     */
    public void asegurarCarritoVacio() {
        System.out.println("[CarritoSteps] asegurarCarritoVacio");
        productsScreen.irAlCarrito();
        cartScreen.validarPantallaCargada();
        cartScreen.vaciarCarritoSiTieneProductos();
        productsScreen.regresarDesdeCarrito();
    }

    public List<String> validarGaleriaCargada() {
        System.out.println("[CarritoSteps] validarGaleriaCargada");
        return productsScreen.validarGaleriaCargada();
    }

    public void agregarProductoAlCarrito(String producto, int unidades) {
        System.out.println("[CarritoSteps] agregarProductoAlCarrito -> " + producto + " x" + unidades);

        productsScreen.abrirProducto(producto);

        String nombreEnDetalle = productDetailScreen.obtenerNombreProducto();
        if (!nombreEnDetalle.equalsIgnoreCase(producto)) {
            throw new AssertionError("El detalle abierto no corresponde al producto solicitado. Esperado: '"
                    + producto + "' | Encontrado: '" + nombreEnDetalle + "'");
        }

        productDetailScreen.ajustarCantidadYAgregarAlCarrito(unidades);
    }

    public boolean carritoSeActualizoCorrectamente(String ultimoProducto, int unidadesEsperadas) {
        int badgeInformativo = productsScreen.obtenerContadorCarrito();
        System.out.println("[CarritoSteps] (informativo) Contador del carrito = " + badgeInformativo);

        productsScreen.irAlCarrito();
        cartScreen.validarPantallaCargada();

        boolean actualizadoOk = cartScreen.contieneProductoConCantidad(ultimoProducto, unidadesEsperadas);
        System.out.println("[CarritoSteps] El carrito contiene '" + ultimoProducto + "' con la cantidad correcta? "
                + actualizadoOk);

        return actualizadoOk;
    }
}