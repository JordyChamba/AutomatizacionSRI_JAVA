package com.automotizacion.processing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfProcessor {

    public static class Resultado {
        public final List<Map<String, String>> facturas = new ArrayList<>();
        public final List<Map<String, String>> retenciones = new ArrayList<>();
    }

    public static Resultado procesarTodosLosPdfs() throws IOException {
        Resultado resultado = new Resultado();

        Path facturasPath = Paths.get("C:\\Users\\Jordy\\Downloads\\Facturas");
        Path retencionesPath = Paths.get("C:\\Users\\Jordy\\Downloads\\Retenciones");

        // Crear carpetas si no existen
        Files.createDirectories(facturasPath);
        Files.createDirectories(retencionesPath);

        Set<String> procesados = new HashSet<>();

        // Procesar Retenciones
        procesarCarpeta(retencionesPath, true, resultado.retenciones, procesados);

        // Procesar Facturas
        procesarCarpeta(facturasPath, false, resultado.facturas, procesados);

        return resultado;
    }

    private static void procesarCarpeta(Path carpeta, boolean esRetencion, List<Map<String, String>> lista,
            Set<String> procesados) throws IOException {
        if (!Files.exists(carpeta))
            return;

        for (Path pdfPath : Files.list(carpeta).filter(p -> p.toString().endsWith(".pdf")).toList()) {
            String nombre = pdfPath.getFileName().toString();
            if (procesados.contains(nombre)) {
                System.out.println("Duplicado omitido → " + nombre);
                continue;
            }
            procesados.add(nombre);

            System.out.println("Procesando " + (esRetencion ? "retención" : "factura") + ": " + nombre);

            String texto = extraerTexto(pdfPath.toFile());

            if (esRetencion) {
                extraerRetenciones(texto, nombre, lista);
            } else {
                extraerFactura(texto, nombre, lista);
            }
        }
    }

    private static String extraerTexto(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private static void extraerRetenciones(String texto, String nombreArchivo, List<Map<String, String>> lista) {
        String[] lineas = texto.split("\n");
        List<String[]> retenciones = new ArrayList<>();

        Pattern p = Pattern.compile("(Renta|IVA).*?(\\d+[.,]\\d{2}%?)\\s*\\$?\\s*([\\d.,]+)");
        for (String linea : lineas) {
            Matcher m = p.matcher(linea);
            if (m.find()) {
                String impuesto = m.group(1).trim();
                String porc = m.group(2).trim();
                String val = m.group(3).replace(",", "").trim();
                retenciones.add(new String[] { impuesto, porc, val });
            }
        }

        // Valores por defecto si no encuentra nada
        if (retenciones.isEmpty()) {
            retenciones.add(new String[] { "Renta", "2.75%", "0.00" });
            retenciones.add(new String[] { "IVA", "70.00%", "0.00" });
        }

        for (String[] r : retenciones) {
            Map<String, String> fila = new LinkedHashMap<>();
            fila.put("Fecha", "01/01/2024");
            fila.put("RUC_Emisor", "NO_ENCONTRADO");
            fila.put("Número_Retención", "NO_ENCONTRADO");
            fila.put("Clave_Acceso", nombreArchivo.replace(".pdf", ""));
            fila.put("RUC_Proveedor", "1803005071001");
            fila.put("Proveedor", "SOLIS ACOSTA EDGAR FERNANDO");
            fila.put("Número_Factura", "");
            fila.put("Impuesto", r[0]);
            fila.put("Porcentaje", r[1]);
            fila.put("Valor_Retenido", r[2]);
            lista.add(fila);
        }
    }

    private static void extraerFactura(String texto, String nombreArchivo, List<Map<String, String>> lista) {
        Map<String, String> fila = new LinkedHashMap<>();

        // RUC
        Matcher ruc = Pattern.compile("R\\.?U\\.?C\\.?\\s*[:\\s]*(\\d{13})").matcher(texto);
        fila.put("Archivo", nombreArchivo);
        fila.put("RUC_Emisor", ruc.find() ? ruc.group(1) : "NO_ENCONTRADO");

        // Número Factura
        Matcher factura = Pattern.compile("FACTURA\\s+No?\\.?\\s*[:\\s]+(\\d{3}-\\d{3}-\\d{9})").matcher(texto);
        fila.put("Número_Factura", factura.find() ? factura.group(1) : "NO_ENCONTRADO");

        // Fecha
        Matcher fecha = Pattern.compile("(?:FECHA|Fecha)[\\s\\:]*[^\\d\\r\\n]*(\\d{2}[/\\-\\s]?\\d{2}[/\\-\\s]?\\d{4})")
                .matcher(texto);
        fila.put("Fecha", fecha.find() ? fecha.group(1).replace("-", "/").replace(" ", "/") : "NO_ENCONTRADO");

        // Subtotal Gravado
        Matcher gravado = Pattern.compile("SUBTOTAL\\s+\\d+%?\\s*[:\\s]*\\$?\\s*([\\d.,]+)").matcher(texto);
        fila.put("Subtotal_Gravado", gravado.find() ? gravado.group(1).replace(",", "").trim() : "0.00");

        // Subtotal sin impuestos
        Matcher sinImp = Pattern.compile(
                "SUBTOTAL\\s+(?:SIN\\s+IMPUESTOS|NO\\s+OBJETO\\s+DE\\s+IVA|EXENTO\\s+DE\\s+IVA)\\s*[:\\s]*\\$?\\s*([\\d.,]+)")
                .matcher(texto);
        fila.put("Subtotal_sin_Impuestos", sinImp.find() ? sinImp.group(1).replace(",", "").trim() : "0.00");

        // IVA
        Matcher iva = Pattern.compile("IVA\\s+\\d+%?\\s*[:\\s]*\\$?\\s*([\\d.,]+)").matcher(texto);
        fila.put("IVA", iva.find() ? iva.group(1).replace(",", "").trim() : "0.00");

        // Total
        Matcher total = Pattern.compile("VALOR\\s+TOTAL\\s*[:\\s]*\\$?\\s*([\\d.,]+)").matcher(texto);
        fila.put("Total", total.find() ? total.group(1).replace(",", "").trim() : "0.00");

        lista.add(fila);
    }
}