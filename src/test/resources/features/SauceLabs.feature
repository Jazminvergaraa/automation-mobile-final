# language: es
Característica: Inicio de sesion en Sauce Labs Sample App

  Como usuario de la app movil de Sauce Labs
  Quiero iniciar sesion con credenciales validas
  Para acceder al catalogo de productos

  @SauceLabsLogin @Smoke
  Escenario: Login exitoso con credenciales validas
    Dado ingreso al aplicativo "saucelabs"
    Cuando ingreso el usuario "standard_user"
    Y ingreso la clave "secret_sauce"
    Y hago clic en LOGIN
    Entonces valido el login OK
    Y cierro el aplicativo "com.swaglabsmobileapp"