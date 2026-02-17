# SRI Retenciones → Excel (Java 2025)

## Descripción

Este proyecto es una automatización completa en Java para descargar comprobantes (facturas y retenciones) del Servicio de Rentas Internas (SRI) de Ecuador y generar archivos Excel con los datos extraídos de los PDFs descargados. Utiliza Selenium para la navegación web, PDFBox para la extracción de datos de PDFs y Log4j para el logging.

## Características

- **Automatización de Login**: Inicio de sesión automático en el portal del SRI.
- **Navegación Inteligente**: Navegación a la sección de comprobantes recibidos.
- **Selección Flexible**: Permite seleccionar tipos de comprobantes (Factura o Retención) y meses específicos.
- **Descarga Automática**: Descarga de múltiples comprobantes en lotes.
- **Extracción de Datos**: Procesamiento de PDFs para extraer información relevante.
- **Generación de Excel**: Creación de archivos Excel organizados con los datos extraídos.
- **Gestión de Drivers**: Uso de WebDriverManager para manejar automáticamente los drivers de Chrome.

## Requisitos Previos

- **Java**: Versión 21 o superior.
- **Maven**: Para la gestión de dependencias y construcción del proyecto.
- **Chrome Browser**: Instalado en el sistema, ya que utiliza ChromeDriver.

## Instalación

1. Clona este repositorio:

   ```bash
   git clone https://github.com/tu-usuario/retenciones-sri.git
   cd retenciones-sri
   ```

2. Construye el proyecto con Maven:
   ```bash
   mvn clean install
   ```

## Uso

1. Asegúrate de tener configuradas las credenciales de acceso al SRI (ver sección de Configuración).

2. Ejecuta la aplicación principal:

   ```bash
   mvn exec:java -Dexec.mainClass="com.automotizacion.SriRetencionesApp"
   ```

3. Sigue las instrucciones en consola para seleccionar los tipos de comprobantes y meses a descargar.

4. Los archivos Excel generados se guardarán en las carpetas de descargas configuradas (por defecto: `C:\Users\Jordy\Downloads\Facturas` y `C:\Users\Jordy\Downloads\Retenciones`).

## Configuración

- **Credenciales**: Las credenciales del SRI deben estar configuradas en el código o en un archivo de configuración seguro. Actualmente, se manejan en la clase `LoginHandler`.
- **Rutas de Descarga**: Las rutas de salida se pueden modificar en la clase principal `SriRetencionesApp.java`.
- **Perfil de Chrome**: El proyecto utiliza un perfil de Chrome personalizado ubicado en `chrome_profile/` para mantener sesiones y configuraciones.

## Dependencias

- **Selenium Java**: 4.17.0 - Para la automatización web.
- **WebDriverManager**: 5.7.0 - Para la gestión automática de drivers.
- **PDFBox**: 2.0.30 - Para la manipulación y extracción de texto de PDFs.
- **Log4j Core**: 2.20.0 - Para el logging de la aplicación.

## Estructura del Proyecto

```
test/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── automotizacion/
│   │   │           ├── SriRetencionesApp.java
│   │   │           ├── driver/
│   │   │           │   └── ChromeDriverManager.java
│   │   │           ├── navegation/
│   │   │           │   ├── LoginHandler.java
│   │   │           │   └── NavigationHandler.java
│   │   │           ├── download/
│   │   │           │   ├── ComprobanteSelector.java
│   │   │           │   └── RetencionesDownloader.java
│   │   │           ├── processing/
│   │   │           │   └── PdfDataExtractor.java
│   │   │           └── excel/
│   │   │               └── ExcelGenerator.java
│   │   └── resources/
│   └── test/
└── target/
```

## Contribuyendo

Si deseas contribuir a este proyecto:

1. Haz un fork del repositorio.
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`).
3. Commit tus cambios (`git commit -am 'Agrega nueva funcionalidad'`).
4. Push a la rama (`git push origin feature/nueva-funcionalidad`).
5. Abre un Pull Request.

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## Contacto

Para preguntas o soporte, contacta al maintainer del repositorio.

---

**Nota**: Asegúrate de cumplir con las políticas del SRI y las leyes de Ecuador al usar esta automatización. Este proyecto es para fines educativos y de automatización personal.</content>
<parameter name="filePath">c:\Users\Jordy\Desktop\Empresa\AutomatizaciónJava\README.md
