package com.automotizacion.download;

import java.util.*;

public class MonthSelector {

    private static final Map<String, String> MES_NOMBRES = Map.ofEntries(
            Map.entry("1", "Enero"), Map.entry("01", "Enero"),
            Map.entry("2", "Febrero"), Map.entry("02", "Febrero"),
            Map.entry("3", "Marzo"), Map.entry("03", "Marzo"),
            Map.entry("4", "Abril"), Map.entry("04", "Abril"),
            Map.entry("5", "Mayo"), Map.entry("05", "Mayo"),
            Map.entry("6", "Junio"), Map.entry("06", "Junio"),
            Map.entry("7", "Julio"), Map.entry("07", "Julio"),
            Map.entry("8", "Agosto"), Map.entry("08", "Agosto"),
            Map.entry("9", "Septiembre"), Map.entry("09", "Septiembre"),
            Map.entry("10", "Octubre"),
            Map.entry("11", "Noviembre"),
            Map.entry("12", "Diciembre"));

    /**
     * Pide al usuario año, mes y día (día opcional: vacío o "todos" → "Todos")
     */
    public static List<ParAnioMesDia> seleccionarMesesConMensaje(String tipoComprobante) {
        List<ParAnioMesDia> periodos = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        System.out.println("\n" + "=".repeat(80));
        System.out.println(" INGRESO DE PERIODOS PARA: " + tipoComprobante.toUpperCase());
        System.out.println("=".repeat(80));
        System.out.println("   • Para el día: deja vacío o escribe 'todos' para seleccionar 'Todos' en el portal");
        System.out.println("   • O ingresa un número (1-31) para un día específico");

        while (true) {
            // === AÑO ===
            String anio;
            while (true) {
                System.out.print("\nAño (ej: 2025): ");
                anio = sc.nextLine().trim();
                if (anio.matches("\\d{4}")) {
                    break;
                }
                System.out.println("Año inválido, debe tener 4 dígitos");
            }

            // === MES ===
            String mesNum;
            while (true) {
                System.out.print("Mes (1-12): ");
                mesNum = sc.nextLine().trim();
                if (MES_NOMBRES.containsKey(mesNum)) {
                    break;
                }
                System.out.println("Mes inválido, usa 1-12 o 01-12");
            }
            String nombreMes = MES_NOMBRES.get(mesNum);

            // === DÍA ===
            System.out.print("Día (1-31 o deja vacío para 'Todos'): ");
            String diaInput = sc.nextLine().trim().toLowerCase();
            String nombreDia;

            if (diaInput.isEmpty() || diaInput.equals("todos") || diaInput.equals("todo")) {
                nombreDia = "Todos";
            } else if (diaInput.matches("\\d{1,2}")) {
                int diaNum = Integer.parseInt(diaInput);
                if (diaNum >= 1 && diaNum <= 31) {
                    nombreDia = diaInput;
                } else {
                    System.out.println("Día fuera de rango (1-31), se usará 'Todos'");
                    nombreDia = "Todos";
                }
            } else {
                System.out.println("Entrada inválida para día, se usará 'Todos'");
                nombreDia = "Todos";
            }

            periodos.add(new ParAnioMesDia(anio, nombreMes, nombreDia));
            System.out.println("✓ Agregado: " + nombreDia + " de " + nombreMes + " " + anio);

            // === ¿OTRO PERIODO? ===
            System.out.print("\n¿Otro periodo para este tipo? (S/N): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("S")) {
                break;
            }
        }

        return periodos;
    }

    public record ParAnioMesDia(String anio, String mes, String dia) {
    }
}