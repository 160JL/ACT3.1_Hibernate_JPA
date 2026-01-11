import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Coche;
import model.Concesionario;
import model.Mecanico;

import java.util.List;
import java.util.Objects;
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

            try {
                // Leemos la línea completa y la convertimos
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = -1; // Fuerza el default en el switch
            }

            switch (opcion) {
                case 1 -> Servicios.iniciarEntityManager();
                case 2 -> menuStock();
                //TODO
                case 3 -> menuTaller();
                //TODO
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

            try {
                // Leemos la línea completa y la convertimos
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = -1; // Fuerza el default en el switch
            }

            switch (opcion) {
                case 1 -> menuAltaConcesionario();
                case 2 -> menuAltaCoche();
                case 0 -> {}
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private static void menuAltaCoche() {Scanner sc = new Scanner(System.in);
        System.out.println("Alta de coche\n");

        String matricula = null;
        boolean exito = false;
        do {
            System.out.println("Introduce una matricula: (Deja en blanco para cancelar)");
            matricula = sc.nextLine();
            if(matricula != null&&matricula.trim().isBlank()) return;
            assert matricula != null;
            exito=Servicios.altaCoche(matricula);
        } while (!exito);

        int id = 0;
        List<Concesionario> concesionarios = Servicios.listadoConcesionarios();
        System.out.println(concesionarios);
        while (true) {
            System.out.println("Inserta la ID del Concesionario.");
            try {
                // Leemos la línea completa y la convertimos
                id = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                id = -1; // Fuerza el default en el switch
            }
            int finalId = id;
            if (concesionarios.stream().anyMatch(concesionario-> concesionario.getId()== finalId)) break;
        }
        System.out.println("Introduce la marca:");
        String marca = sc.nextLine();
        System.out.println("Introduce el modelo:");
        String modelo = sc.nextLine();
        double precioBase = 0.0;
        while (true) {
            System.out.println("Introduce el precio base:");
            try {
                precioBase = Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                precioBase = -1;
                System.out.println("--- ERROR: Numero no válido ---");
            }
            if (precioBase>=0) break;
        }
        int finalId = id;
        Concesionario concesionariont = concesionarios.stream().filter(concesionario-> concesionario.getId()== finalId).findFirst().orElse(null);
        Servicios.meh(matricula,marca,modelo,precioBase,concesionariont);

    }

    private static void menuAltaConcesionario() {
        System.out.println("Alta de concesionario");
        System.out.println("Nombre del Concesionario: (Deja en blanco para cancelar)");
        String nombre = sc.nextLine();
        if (nombre.trim().isBlank()) return;
        System.out.println("Dirección del Concesionario: (Deja en blanco para cancelar)");
        String direccion = sc.nextLine();
        if (direccion.trim().isBlank()) return;
        Servicios.altaConcesionario(nombre,direccion);
    }

    private static void menuTaller() {
        int opcion;
        do {
            System.out.println("\n--- Taller y Equipamiento ---");
            System.out.println("1. Instalar Extra");
            System.out.println("2. Registrar Reparación");
            System.out.println("0. Volver");

            try {
                // Leemos la línea completa y la convertimos
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = -1; // Fuerza el default en el switch
            }

            switch (opcion) {
                //TODO
                case 1 -> Servicios.instalarExtra();
                //TODO
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

            try {
                // Leemos la línea completa y la convertimos
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = -1; // Fuerza el default en el switch
            }

            switch (opcion) {
                //TODO
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

            try {
                // Leemos la línea completa y la convertimos
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = -1; // Fuerza el default en el switch
            }

            switch (opcion) {
                case 1 -> menuStockVentasConcesionario(true);
                case 2 -> menuHistorialMecanico();
                case 3 -> menuStockVentasConcesionario(false);
                case 4 -> menuCosteActualCoche();
                case 0 -> {}
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private static void menuCosteActualCoche() {
        boolean menu = true;
        String id = null;
        Coche cochent = null;
        List<Coche> coches = Servicios.listadoCochesPropietarios();
        System.out.println(coches);
        while (menu) {
            System.out.println("Inserta la matrícula del Coche. Inserte 0 para cancelar:");
            id = sc.nextLine();
            String finalId = id;
            cochent = coches.stream().filter(coche-> Objects.equals(coche.getMatricula(), finalId.trim())).findFirst().orElse(null);
            if (cochent!=null||finalId.trim().equals("0")) menu = false;
        }
        if (id.trim().equals("0")) return;
        Servicios.costeActualCoche(cochent);
    }

    private static void menuHistorialMecanico() {
        boolean menu = true;
        int id = 0;
        List<Mecanico> mecanicos = Servicios.listadoMecanicos();
        System.out.println(mecanicos);
        while (menu) {
            System.out.println("Inserta la ID del Mecanico. Inserte 0 para cancelar:");
            try {
                // Leemos la línea completa y la convertimos
                id = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                id = -1; // Fuerza el default en el switch
            }
            int finalId = id;
            if (mecanicos.stream().anyMatch(mecanico-> mecanico.getId()== finalId)||finalId==0) menu = false;
        }
        if (id == 0) return;
        Servicios.historialMecanico(id);
    }

    private static void menuStockVentasConcesionario(boolean stock) {
        boolean menu = true;
        int id = 0;
        List<Concesionario> concesionarios = Servicios.listadoConcesionarios();
        System.out.println(concesionarios);
        while (menu) {
            System.out.println("Inserta la ID del Concesionario. Inserte 0 para cancelar:");
            try {
                // Leemos la línea completa y la convertimos
                id = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                id = -1; // Fuerza el default en el switch
            }
            int finalId = id;
            if (concesionarios.stream().anyMatch(concesionario-> concesionario.getId()== finalId)||finalId==0) menu = false;
        }
        if (id == 0) return;
        if (stock) Servicios.stockConcesionario(id); else Servicios.ventasPorConcesionario(id);
    }
}