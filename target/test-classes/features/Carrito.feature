# language: es
Característica: Validar funcionalidad del carrito de compras

  Como QA encargado de la regresión de la app Android de la tienda en línea
  Quiero validar que el carrito de compras se actualice correctamente al agregar productos
  Para detectar a tiempo comportamientos no deseados en cada nuevo APK entregado por Desarrollo

  @Carrito @Smoke
  Esquema del escenario: Agregar productos al carrito y validar su actualización
    Dado estoy en la aplicación de SauceLabs
    Y valido que carguen correctamente los productos en la galeria
    Cuando agrego <UNIDADES> del siguiente producto "<PRODUCTO>"
    Entonces valido el carrito de compra actualice correctamente

    Ejemplos:
      | PRODUCTO                 | UNIDADES |
      | Sauce Labs Backpack      | 1        |
      | Sauce Labs Bolt T-Shirt  | 1        |
      | Sauce Labs Bike Light    | 2        |