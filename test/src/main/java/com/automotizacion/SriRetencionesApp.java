package com.automotizacion;

import com.automotizacion.driver.ChromeDriverManager;
import com.automotizacion.navegation.LoginHandler;
import com.automotizacion.navegation.NavigationHandler;
import com.automotizacion.download.ComprobanteSelector;
import com.automotizacion.download.RetencionesDownloader;
import com.automotizacion.processing.PdfDataExtractor;
import com.automotizacion.excel.ExcelGenerator;

import java.util.List;

public class SriRetencionesApp {

    public static void main(String[] args) throws Exception {
        System.out.println("=== SRI RETENCIONES → EXCEL 100% FUNCIONAL (2025) - VERSIÓN JAVA MODULAR ===");

        ChromeDriverManager driverManager = new ChromeDriverManager();
        driverManager.iniciarChrome();

        try {
            LoginHandler.ejecutar(driverManager);
            NavigationHandler.irAComprobantesRecibidos(driverManager);

            // NUEVO: Selecciona tipo de comprobante (Factura o Retención) y meses
            List<ComprobanteSelector.ConfigComprobante> configuraciones = ComprobanteSelector.seleccionarComprobantes();

            // Descarga todos los tipos seleccionados
            for (ComprobanteSelector.ConfigComprobante config : configuraciones) {
                RetencionesDownloader.descargarComprobantes(driverManager, config);
            }

            // Después de descargar todo
            System.out.println("\n" + "=".repeat(80));
            System.out.println(" DESCARGAS COMPLETADAS - GENERANDO EXCEL...");
            System.out.println("=".repeat(80));

            ExcelGenerator.generarExcels();

        } finally {
            driverManager.cerrar();
        }

        System.out.println("\n¡PROCESO COMPLETADO CON ÉXITO!");
        System.out.println("Tus archivos están en:");
        System.out.println("   - C:\\Users\\Jordy\\Downloads\\Facturas");
        System.out.println("   - C:\\Users\\Jordy\\Downloads\\Retenciones");
    }
}