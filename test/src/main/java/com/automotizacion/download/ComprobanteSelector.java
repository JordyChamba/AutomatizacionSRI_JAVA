package com.automotizacion.download;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.automotizacion.download.MonthSelector.ParAnioMesDia;

public class ComprobanteSelector {

    public static List<ConfigComprobante> seleccionarComprobantes() {
        List<ConfigComprobante> configuraciones = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("¿QUÉ TIPO DE COMPROBANTE QUIERES DESCARGAR AHORA?");
            System.out.println("1 → FACTURA");
            System.out.println("2 → COMPROBANTE DE RETENCIÓN");
            System.out.println("=".repeat(80));

            String opcion;
            while (true) {
                System.out.print("\nElige 1 o 2: ");
                opcion = sc.nextLine().trim();
                if (opcion.equals("1") || opcion.equals("2")) {
                    break;
                }
                System.out.println("Elige 1 o 2");
            }

            String carpetaFinal;
            String tipoComprobante;
            boolean esRetencion;

            if (opcion.equals("1")) {
                carpetaFinal = "C:\\Users\\Jordy\\Downloads\\Facturas";
                tipoComprobante = "Factura";
                esRetencion = false;
            } else {
                carpetaFinal = "C:\\Users\\Jordy\\Downloads\\Retenciones";
                tipoComprobante = "Comprobante de Retención";
                esRetencion = true;
            }

            // Crear carpeta si no existe
            try {
                Files.createDirectories(Paths.get(carpetaFinal));
                System.out.println("Carpeta asegurada: " + carpetaFinal);
            } catch (Exception e) {
                System.out.println("Error creando carpeta: " + e.getMessage());
            }

            System.out.println("\nSeleccionado: " + tipoComprobante.toUpperCase());
            System.out.println("Guardando en: " + carpetaFinal);

            // Pedir periodos (año + mes + día)
            List<ParAnioMesDia> meses = MonthSelector.seleccionarMesesConMensaje(tipoComprobante);

            configuraciones.add(new ConfigComprobante(carpetaFinal, tipoComprobante, esRetencion, meses));

            System.out.print("\n¿Descargar otro tipo de comprobante? (S/N): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("S")) {
                break;
            }
        }

        return configuraciones;
    }

    public record ConfigComprobante(
            String carpetaFinal,
            String tipoComprobante,
            boolean esRetencion,
            List<ParAnioMesDia> meses) {
    }
}