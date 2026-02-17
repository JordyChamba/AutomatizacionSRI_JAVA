package com.automotizacion.download;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import com.automotizacion.driver.ChromeDriverManager;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;

public class RetencionesDownloader {

    public static void descargarComprobantes(ChromeDriverManager driverManager,
            ComprobanteSelector.ConfigComprobante config)
            throws InterruptedException, IOException {

        String carpetaFinal = config.carpetaFinal();
        String tipoComprobante = config.tipoComprobante();
        List<MonthSelector.ParAnioMesDia> meses = config.meses();

        WebDriver driver = driverManager.getDriver();
        WebDriverWait wait = driverManager.getWait();

        // === APLICAR FILTRO DE TIPO DE COMPROBANTE ===
        System.out.println("\nAplicando filtro: " + tipoComprobante);
        boolean filtroOk = false;

        for (WebElement selectEl : driver.findElements(By.tagName("select"))) {
            Select sel = new Select(selectEl);
            for (WebElement opt : sel.getOptions()) {
                if (opt.getText().trim().equals(tipoComprobante)) {
                    sel.selectByVisibleText(tipoComprobante);
                    filtroOk = true;
                    System.out.println("✓ Filtro aplicado automáticamente: " + tipoComprobante);
                    Thread.sleep(3000);
                    break;
                }
            }
            if (filtroOk)
                break;
        }

        if (!filtroOk) {
            System.out.println("⚠ No se pudo aplicar el filtro automáticamente.");
            System.out.print("→ Selecciona manualmente '" + tipoComprobante + "' y presiona ENTER: ");
            new Scanner(System.in).nextLine();
        }

        // === DESCARGA POR CADA PERIODO ===
        for (int i = 0; i < meses.size(); i++) {
            MonthSelector.ParAnioMesDia par = meses.get(i);
            System.out.println("\n" + "=".repeat(80));
            System.out.println(" DESCARGANDO " + (i + 1) + "/" + meses.size() +
                    ": " + par.dia() + " de " + par.mes() + " " + par.anio() + " → " + tipoComprobante.toUpperCase());
            System.out.println("=".repeat(80));

            // === SELECCIÓN DE AÑO, MES Y DÍA CON FALLBACK A "TODOS" ===
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("select")));
            Thread.sleep(3000);

            for (WebElement selectEl : driver.findElements(By.tagName("select"))) {
                Select sel = new Select(selectEl);
                List<WebElement> options = sel.getOptions();
                boolean seleccionado = false;

                for (WebElement opt : options) {
                    String text = opt.getText().trim();
                    String value = opt.getAttribute("value");

                    // Año por value (más confiable)
                    if (value != null && value.equals(par.anio())) {
                        sel.selectByValue(par.anio());
                        System.out.println("✓ Año seleccionado: " + par.anio());
                        seleccionado = true;
                    }

                    // Mes por texto
                    if (text.equals(par.mes())) {
                        sel.selectByVisibleText(par.mes());
                        System.out.println("✓ Mes seleccionado: " + par.mes());
                        seleccionado = true;
                    }

                    // Día por texto (número o "Todos")
                    if (text.equals(par.dia())) {
                        sel.selectByVisibleText(par.dia());
                        System.out.println("✓ Día seleccionado: " + par.dia());
                        seleccionado = true;
                    }

                    // Fallback: "Todos" si existe
                    if (!seleccionado && (text.equals("Todos") || text.equals("Todo"))) {
                        sel.selectByVisibleText(text);
                        System.out.println("✓ Fallback aplicado: '" + text + "'");
                        seleccionado = true;
                    }
                }

                // Forzar "Todos" si no se seleccionó nada
                if (!seleccionado) {
                    try {
                        sel.selectByVisibleText("Todos");
                        System.out.println("✓ Forzado 'Todos' en select");
                    } catch (Exception ignored) {
                        try {
                            sel.selectByVisibleText("Todo");
                            System.out.println("✓ Forzado 'Todo' en select");
                        } catch (Exception ignored2) {
                        }
                    }
                }
            }

            // === CLICK EN BOTÓN DE CONSULTAR (COBERTURA TOTAL PARA SRI 2025) ===
            // === CLICK EN BOTÓN DE BÚSQUEDA (RÁPIDO Y CONFIABLE) ===
            boolean consultaOk = false;

            // Usamos un wait corto solo para este botón (5 segundos por intento)
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            String[] xpaths = {
                    "//button[contains(text(),'Consultar')]", // Primero el más común
                    "//button[text()='Consultar']",
                    "//button[normalize-space()='Consultar']",
                    "//button[contains(text(),'Buscar')]",
                    "//button[@type='submit']",
                    "//button[contains(@class,'btn-primary')]",
                    "//button[.//i[contains(@class,'fa-search')]]", // Lupa FontAwesome
                    "//button[.//i[contains(@class,'glyphicon-search')]]", // Lupa Glyphicon
                    "//button[.//i[@class='fa fa-search']]"

            };

            for (String xpath : xpaths) {
                try {
                    // Wait corto: máximo 5 segundos por XPath
                    WebElement btn = shortWait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                    System.out.println("✓ Consulta realizada automáticamente");
                    consultaOk = true;
                    Thread.sleep(8000); // Espera normal después del click
                    break;
                } catch (TimeoutException ignored) {
                    // No encontró este XPath, prueba el siguiente rápidamente
                } catch (Exception ignored) {
                }
            }

            if (!consultaOk) {
                System.out.println("\n⚠ No se encontró el botón automáticamente (probó todos los XPaths rápido).");
                System.out.print("→ Haz click manual en 'Consultar' o la lupa y presiona ENTER: ");
                new Scanner(System.in).nextLine();
            }

            // === CAPTCHA / ESPERA ===
            System.out.print("→ Resuelve CAPTCHA si aparece → Presiona ENTER cuando veas la lista: ");
            new Scanner(System.in).nextLine();

            // === DESCARGAR PDFs ===
            List<WebElement> links = driver.findElements(By.xpath("//a[contains(@id,'lnkPdf')]"));
            System.out.println(links.size() + " documentos encontrados");

            if (links.isEmpty()) {
                System.out.println("No hay documentos para este periodo. Continuando...");
                continue;
            }

            for (int j = 0; j < links.size(); j++) {
                links = driver.findElements(By.xpath("//a[contains(@id,'lnkPdf')]"));
                if (j >= links.size())
                    break;

                WebElement link = links.get(j);

                try {
                    String claveAcceso = link.findElement(By.xpath("./ancestor::tr//td[4]")).getText().trim();
                    String nombreArchivo = claveAcceso.replaceAll("[^\\w-]", "")
                            .substring(0, Math.min(60, claveAcceso.length())) + ".pdf";

                    System.out.printf(" [%02d/%d] Descargando: %s%n", j + 1, links.size(), nombreArchivo);

                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);
                    Thread.sleep(500);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);

                    esperarDescarga();
                    renombrarUltimoPdf(nombreArchivo, carpetaFinal);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("   ⚠ Error al descargar: " + e.getMessage());
                }
            }

            System.out.println(par.mes() + " " + par.anio() + " → COMPLETADO");
        }
    }

    private static void esperarDescarga() throws InterruptedException, IOException {
        Thread.sleep(3000);
        Path carpeta = Paths.get("C:\\Users\\Jordy\\Downloads");

        while (true) {
            boolean enDescarga = Files.list(carpeta)
                    .anyMatch(p -> p.getFileName().toString().endsWith(".crdownload"));
            if (!enDescarga)
                break;
            Thread.sleep(1000);
        }
    }

    private static void renombrarUltimoPdf(String nuevoNombre, String carpetaDestino) throws IOException {
        Path carpetaDownloads = Paths.get("C:\\Users\\Jordy\\Downloads");

        Optional<Path> ultimo = Files.list(carpetaDownloads)
                .filter(p -> p.toString().endsWith(".pdf"))
                .filter(p -> !p.getFileName().toString().contains(".crdownload"))
                .max(Comparator.comparingLong(p -> p.toFile().lastModified()));

        if (ultimo.isPresent()) {
            Path origen = ultimo.get();
            Path destino = Paths.get(carpetaDestino, nuevoNombre);
            Files.createDirectories(destino.getParent());
            Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("   Guardado → " + destino.getFileName());
        }
    }
}