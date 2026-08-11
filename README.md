# QAInnovationLab — Appium Android Java (Cucumber)

Automation Team / Con fines educativos — automatización mobile Android con
**Appium + Java + Cucumber**.

---

## 1. Arquitectura del proyecto

```
src/main/java/com/nttdata/
├── screens/
│   ├── AppConfigScreen.java       → UNICA clase que levanta/cierra la app (cualquier app)
│   └── SauceLabsLoginScreen.java  → Solo interaccion con la pantalla de login de Sauce Labs
├── steps/
│   ├── AppConfigSteps.java        → Caso de uso generico: iniciar / cerrar / screenshot
│   └── SauceLabsLoginSteps.java   → Caso de uso: escribir usuario, clave, click login, validar
└── support/
    └── ScreenshotAttacher.java    → Utilidad para adjuntar evidencias a Cucumber

src/test/java/com/nttdata/
├── stepsdefinitions/
│   ├── AppConfigStepsDefs.java    → Step GENERICO: "ingreso al aplicativo" / "cierro el aplicativo"
│   └── SauceLabsStepDefs.java     → Steps especificos del login (usuario, clave, LOGIN, validar)
├── hooks/Hooks.java                → @Before / @After globales (logs + screenshot final)
└── runners/CucumberTestSuite.java → Runner JUnit + Cucumber

src/test/resources/
├── config/
│   ├── saucelabs.properties               → Capabilities de la app Sauce Labs
│   └── plantilla-nueva-app.properties.example → Plantilla para agregar una app nueva
└── features/
    └── SauceLabs.feature
```

### Regla de oro: `AppConfigScreen` SOLO levanta/cierra la app

`AppConfigScreen` es la única clase de todo el framework que:
- Crea la sesión de Appium (`iniciarAplicacion`)
- Cierra la app y la sesión (`cerrarAplicacion`)
- Expone el driver activo a las demás pantallas (`getDriver()`, estático)
- Toma screenshots (es una acción del dispositivo, no de una pantalla puntual)

**No conoce ningún botón, campo o texto de ninguna app.** Es completamente
genérica: sirve para levantar **cualquier** aplicación Android, siempre y
cuando exista su archivo `.properties` en `config/`.

Cualquier otra pantalla (como `SauceLabsLoginScreen`) obtiene el driver
así, sin volver a levantar la app:

```java
import static com.nttdata.screens.AppConfigScreen.getDriver;
...
        AndroidDriver driver = getDriver();
```

### Capa por capa

| Capa               | Responsabilidad                                                     |
|---------------------|----------------------------------------------------------------------|
| `AppConfigScreen`  | Levantar/cerrar CUALQUIER app + screenshot                          |
| `AppConfigSteps` / `AppConfigStepsDefs` | Caso de uso y step genérico reutilizable en cualquier feature |
| `XxxScreen`        | Interactuar con los elementos de una pantalla concreta de una app específica |
| `XxxSteps` / `XxxStepDefs` | Caso de uso y steps específicos de esa app/pantalla          |

---

## 2. Cómo agregar una app o APK nueva

**No se toca `AppConfigScreen` ni ninguna otra clase del framework
base.** Pasos:

1. Copia `config/plantilla-nueva-app.properties.example`, renómbralo
   (ej: `miApp.properties`) y completa sus valores (app, appPackage,
   appActivity, udid, etc).
2. En tu `.feature`, usa el step genérico ya existente para levantarla:
   ```gherkin
   Given ingreso al aplicativo "miApp"
   ```
   (Cucumber automáticamente agrega `.properties` y busca el archivo
   en `config/`).
3. Crea una `Screen` nueva (ej. `MiAppScreen.java`) SOLO con los
   locators e interacciones de esa pantalla, obteniendo el driver con
   `AppConfigScreen.getDriver()`.
4. Crea su `Steps` y `StepDefs` correspondientes (siguiendo el mismo
   patrón que `SauceLabsLoginSteps` / `SauceLabsStepDefs`).
5. Para cerrar la app al final, reutiliza el step genérico:
   ```gherkin
   Then cierro el aplicativo "com.mi.paquete"
   ```

Así, el mismo framework sirve para cualquier cantidad de apps sin
duplicar ni modificar la lógica de manejo del driver.

---

## 3. Logs en consola

Todas las capas imprimen logs con el prefijo `[NombreDeLaClase]` para
que el aprendiz pueda seguir el flujo de ejecución en la consola:

```
========================================
[Hooks] Iniciando escenario: Login exitoso con credenciales validas
========================================
[AppConfigStepsDefs] Iniciando aplicativo: saucelabs
[AppConfigScreen] Leyendo configuracion desde: saucelabs.properties
[AppConfigScreen] Construyendo capabilities...
[AppConfigScreen] Conectando a Appium Server en: http://127.0.0.1:4723/
[AppConfigScreen] Sesion Appium creada correctamente. SessionId: ...
[ScreenshotAttacher] Adjuntando evidencia: Pantalla inicial
[SauceLabsLoginSteps] ingresarUsuario -> standard_user
[SauceLabsLoginScreen] Escribiendo usuario: standard_user
...
[SauceLabsLoginScreen] Pantalla de productos visible: true
[AppConfigStepsDefs] Cerrando aplicativo: com.swaglabsmobileapp
[AppConfigScreen] Cerrando sesion Appium...
[Hooks] Escenario 'Login exitoso con credenciales validas' finalizo: EXITOSO
```

Esto ayuda a diagnosticar rápido en qué paso falló algo, sin depender
únicamente del reporte HTML de Cucumber.

---

## 4. Feature disponible

### `SauceLabs.feature` (`@SauceLabsLogin`, `@Smoke`)
Login completo en la app de Sauce Labs (`standard_user` / `secret_sauce`),
validación de que se llegó a la pantalla de productos, y cierre de la
app — usando el step genérico de `AppConfigStepsDefs` para levantarla y
cerrarla.

---

## 5. Requisitos

- Java 21
- Maven
- Appium Server corriendo en `http://127.0.0.1:4723/`
- Un emulador/dispositivo Android disponible (revisa `udid` en
  `saucelabs.properties`)
- Actualizar la ruta del `app` (APK) en `saucelabs.properties` según tu
  equipo local

---

## 6. Cómo ejecutar

```bash
mvn test
```

Para correr solo el feature de Sauce Labs (ya es el único disponible,
pero sirve como ejemplo si agregas más features luego):

```bash
mvn test -Dcucumber.filter.tags="@SauceLabsLogin"
```

Los reportes quedan en:
- `target/cucumber-report.html`
- `target/cucumber-report.json`

---

## 7. Comandos útiles del emulador (ADB)

Comandos de referencia rápida para depurar y administrar el
emulador/dispositivo Android durante el desarrollo de las pruebas.

### Dispositivos conectados

```bash
adb devices
```

### Iniciar una app manualmente

```bash
adb shell am start -n com.airbnb.android/com.airbnb.android.feat.splashscreen.SplashScreenActivity
```

### Obtener la versión de Android del dispositivo conectado

```bash
adb shell getprop ro.build.version.release
```

### Reiniciar el servicio de ADB

Útil cuando el emulador queda "colgado" o ADB no detecta el
dispositivo:

```bash
adb kill-server & taskkill /f /im adb.exe & taskkill /f /im qemu-system-x86_64.exe & adb start-server
```

### Logs del dispositivo (logcat)

```bash
adb logcat
```

Filtrando por un emulador específico:

```bash
adb -s emulator-5560 logcat
```

### Apagar un emulador específico

```bash
adb -s emulator-5560 emu kill
```

### Listar y arrancar AVDs (Windows)

```bash
%localappdata%\Android\Sdk\emulator\emulator -list-avds
%localappdata%\Android\Sdk\emulator\emulator -avd Pixel_4a_API_30
%localappdata%\Android\Sdk\emulator\emulator -avd Pixel_4_XL_API_35 -gpu swiftshader_indirect
```

### Limpiar snapshots de un AVD (Windows)

Borra todos los snapshots guardados de un AVD puntual (soluciona
arranques corruptos o muy lentos):

```bash
del /Q /S "%USERPROFILE%\.android\avd\Pixel_4_XL_API_35-v2.avd\snapshots\*"
```

Borra los snapshots de **todos** los AVDs de una sola pasada:

```bash
for /D %G in ("%USERPROFILE%\.android\avd\*") do for /D %H in ("%G\snapshots\*") do del /Q /S "%H\*"
```

> **Nota:** estos comandos de Windows (`del`, `taskkill`, `%localappdata%`,
> `%USERPROFILE%`) están pensados para ejecutarse en `cmd.exe`. Si usas
> Git Bash, WSL o PowerShell, la sintaxis de rutas y variables cambia.