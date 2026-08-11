# language: es
Característica: Consulta de informacion de la APK instalada

  Como usuario de la app APK Info
  Quiero buscar texto dentro del listado de aplicaciones instaladas
  Para validar que el buscador funciona correctamente

  @ApkInfo
  Escenario: Buscar una aplicacion instalada por su nombre
    Dado ingreso al aplicativo de APK Info
    Cuando busco el texto "Chrome"
