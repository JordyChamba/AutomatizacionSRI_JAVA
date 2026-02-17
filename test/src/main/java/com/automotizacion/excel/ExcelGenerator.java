package com.automotizacion.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.automotizacion.processing.PdfProcessor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ExcelGenerator {

    public static void generarExcels() throws IOException {
        PdfProcessor.Resultado resultado = PdfProcessor.procesarTodosLosPdfs();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));

        // === EXCEL FACTURAS ===
        String pathFac = "C:\\Users\\Jordy\\Downloads\\Facturas\\FACTURAS_" + timestamp + ".xlsx";
        generarExcel(pathFac, resultado.facturas, new String[] {
                "Archivo", "RUC_Emisor", "Número_Factura", "Fecha",
                "Subtotal_Gravado", "Subtotal_sin_Impuestos", "IVA", "Total"
        }, "Facturas");

        // === EXCEL RETENCIONES ===
        String pathRet = "C:\\Users\\Jordy\\Downloads\\Retenciones\\RETENCIONES_" + timestamp + ".xlsx";
        generarExcel(pathRet, resultado.retenciones, new String[] {
                "Fecha", "RUC_Emisor", "Número_Retención", "Clave_Acceso",
                "RUC_Proveedor", "Proveedor", "Número_Factura", "Impuesto", "Porcentaje", "Valor_Retenido"
        }, "Retenciones");

        System.out.println("\n¡TODO TERMINADO PERFECTO!");
        System.out.println("Excel Facturas: " + pathFac);
        System.out.println("Excel Retenciones: " + pathRet);
    }

    private static void generarExcel(String path, List<Map<String, String>> datos, String[] headers, String tipo)
            throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet(tipo);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Map<String, String> fila : datos) {
                Row row = sheet.createRow(rowNum++);
                int col = 0;
                for (String h : headers) {
                    row.createCell(col++).setCellValue(fila.getOrDefault(h, ""));
                }
            }

            for (int i = 0; i < headers.length; i++)
                sheet.autoSizeColumn(i);

            try (FileOutputStream fos = new FileOutputStream(path)) {
                wb.write(fos);
            }
        }

        System.out.println(tipo + " → " + path + " (" + datos.size() + " líneas)");
    }
}