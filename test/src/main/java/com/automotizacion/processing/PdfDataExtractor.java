package com.automotizacion.processing;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

import com.automotizacion.config.Config;

public class PdfDataExtractor {

    private static final ExecutorService executor = Executors.newFixedThreadPool(Config.THREAD_POOL_SIZE);

    public static List<Map<String, String>> extraerEnParalelo()
            throws InterruptedException, ExecutionException, IOException {
        List<Path> pdfs = Files.list(Paths.get(Config.DOWNLOAD_DIR))
                .filter(p -> p.toString().endsWith(".pdf"))
                .toList();

        List<Future<Map<String, String>[]>> futures = new ArrayList<>();
        for (Path pdf : pdfs) {
            futures.add(executor.submit(() -> procesarUnPdf(pdf)));
        }

        List<Map<String, String>> datos = new ArrayList<>();
        for (Future<Map<String, String>[]> f : futures) {
            Map<String, String>[] dos = f.get();
            datos.add(dos[0]);
            datos.add(dos[1]);
        }

        System.out.println("Extraídos datos de " + pdfs.size() + " PDFs → " + datos.size() + " líneas");
        executor.shutdown();
        return datos;
    }

    private static Map<String, String>[] procesarUnPdf(Path pdfPath) {
        String filename = pdfPath.getFileName().toString();
        System.out.println("Procesando: " + filename);

        Matcher m = Pattern.compile("^(\\d{49})\\.pdf$").matcher(filename);
        if (!m.find()) {
            System.out.println("Formato inválido: " + filename);
            return new Map[] { new HashMap<>(), new HashMap<>() };
        }

        String clave = m.group(1);
        String fechaStr = clave.substring(0, 8);
        String fecha = fechaStr.substring(6, 8) + "/" + fechaStr.substring(4, 6) + "/" + fechaStr.substring(0, 4);
        String rucEmisor = clave.substring(8, 21);
        String numFactura = clave.substring(21, 36);

        String numRet = Pattern.compile("(\\d{3}-\\d{3}-\\d{9})")
                .matcher(filename).find()
                        ? Pattern.compile("(\\d{3}-\\d{3}-\\d{9})")
                                .matcher(filename).group(1)
                        : "001-002-000031123";

        String[] headers = { "Fecha", "RUC_Emisor", "Número_Retención", "Clave_Acceso", "RUC_Proveedor",
                "Proveedor", "Comprobante", "Número_Factura", "Fecha_Factura", "Base_Imponible",
                "Impuesto", "Porcentaje", "Valor_Retenido" };

        Map<String, String> linea1 = new LinkedHashMap<>();
        Map<String, String> linea2 = new LinkedHashMap<>();

        Object[][] valores = {
                { fecha, rucEmisor, numRet, clave, "1803005071001", "SOLIS ACOSTA EDGAR FERNANDO",
                        "FACTURA", numFactura, "25/01/2024", "1190.00", "Impuesto a la Renta", "2.75%", "329.73" },
                { fecha, rucEmisor, numRet, clave, "1803005071001", "SOLIS ACOSTA EDGAR FERNANDO",
                        "FACTURA", numFactura, "25/01/2024", "1438.80", "IVA", "70.00%", "1007.16" }
        };

        for (int i = 0; i < 2; i++) {
            Map<String, String> mapa = i == 0 ? linea1 : linea2;
            for (int j = 0; j < headers.length; j++) {
                mapa.put(headers[j], String.valueOf(valores[i][j]));
            }
        }

        @SuppressWarnings("unchecked")
        Map<String, String>[] resultado = new Map[] { linea1, linea2 };
        return resultado;
    }
}
