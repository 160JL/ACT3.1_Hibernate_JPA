import model.Concesionario;
import model.Mecanico;

import java.util.List;
import java.util.Scanner;

public class Menu {

    private static final Scanner sc = new Scanner(System.in);

    public static void mostrarMenuPrincipal() {
        int opcion;

        do {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Configuración y Carga de Datos");
            System.out.println("2. Gestión de Stock (Altas)");
            System.out.println("3. Taller y Equipamiento");
            System.out.println("4. Ventas");
            System.out.println("5. Consultas e Informes");
            System.out.println("0. Salir");
            System.out.print("Seleccione opción: \n");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> Servicios.iniciarEntityManager();
                case 2 -> menuStock();
                case 3 -> menuTaller();
                case 4 -> menuVentas();
                case 5 -> menuConsultas();
                case 0 -> System.out.println("Saliendo del sistema...");
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private static void menuStock() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Stock ---");
            System.out.println("1. Alta de Concesionario");
            System.out.println("2. Alta de Coche");
            System.out.println("0. Volver");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> Servicios.altaConcesionario();
                case 2 -> Servicios.altaCoche();
                case 0 -> {}
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private static void menuTaller() {
        int opcion;
        do {
            System.out.println("\n--- Taller y Equipamiento ---");
            System.out.println("1. Instalar Extra");
            System.out.println("2. Registrar Reparación");
            System.out.println("0. Volver");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> Servicios.instalarExtra();
                case 2 -> Servicios.registrarReparacion();
                case 0 -> {}
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private static void menuVentas() {
        int opcion;
        do {
            System.out.println("\n--- Ventas ---");
            System.out.println("1. Vender Coche");
            System.out.println("0. Volver");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> Servicios.venderCoche();
                case 0 -> {}
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private static void menuConsultas() {
        int opcion;
        do {
            System.out.println("\n--- Consultas e Informes ---");
            System.out.println("1. Stock de Concesionario");
            System.out.println("2. Historial de Mecánico");
            System.out.println("3. Ventas por Concesionario");
            System.out.println("4. Coste actual de Coche");
            System.out.println("0. Volver");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> menuStockConcesionario();
                case 2 -> menuHistorialMecanico();
                case 3 -> Servicios.ventasPorConcesionario();
                case 4 -> Servicios.costeActualCoche();
                case 0 -> {}
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private static void menuHistorialMecanico() {
        boolean menu = true;
        int id = 0;
        List<Mecanico> mecanicos = Servicios.listadoMecanicos();
        System.out.println(mecanicos);
        while (menu) {
            System.out.println("Inserta la ID del Mecanico");
            id = Integer.parseInt(sc.nextLine());
            int finalId = id;
            if (mecanicos.stream().anyMatch(mecanico-> mecanico.getId()== finalId)) menu = false;
        }
        Servicios.historialMecanico(id);
    }

    private static void menuStockConcesionario() {
        boolean menu = true;
        int id = 0;
        List<Concesionario> concesionarios = Servicios.listadoConcesionarios();
        System.out.println(concesionarios);
        while (menu) {
            System.out.println("Inserta la ID del Concesionario");
            id = Integer.parseInt(sc.nextLine());
            int finalId = id;
            if (concesionarios.stream().anyMatch(concesionario-> concesionario.getId()== finalId)) menu = false;
        }
        Servicios.stockConcesionario(id);
    }
}