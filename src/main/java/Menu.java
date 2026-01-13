import model.*;

import java.time.LocalDateTime;
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

            try {
                // Leemos la línea completa y la convertimos
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = -1; // Fuerza el default en el switch
            }

            switch (opcion) {
                case 1 -> menuAltaConcesionario();
                case 2 -> menuAltaCoche();
                case 0 -> {
                }
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private static void menuAltaCoche() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Alta de coche\n");

        String matricula = null;
        boolean exito = false;
        do {
            System.out.println("Introduce una matricula: (Deja en blanco para cancelar)");
            matricula = sc.nextLine();
            if (matricula != null && matricula.trim().isBlank()) return;
            assert matricula != null;
            exito = Servicios.altaCoche(matricula);
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
            if (concesionarios.stream().anyMatch(concesionario -> concesionario.getId() == finalId)) break;
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
            if (precioBase >= 0) break;
        }
        int finalId = id;
        Concesionario concesionariont = concesionarios.stream().filter(concesionario -> concesionario.getId() == finalId).findFirst().orElse(null);
        Servicios.altaCoche(matricula, marca, modelo, precioBase, concesionariont);

    }

    private static void menuAltaConcesionario() {
        System.out.println("Alta de concesionario");
        System.out.println("Nombre del Concesionario: (Deja en blanco para cancelar)");
        String nombre = sc.nextLine();
        if (nombre.trim().isBlank()) return;
        System.out.println("Dirección del Concesionario: (Deja en blanco para cancelar)");
        String direccion = sc.nextLine();
        if (direccion.trim().isBlank()) return;
        Servicios.altaConcesionario(nombre, direccion);
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
                case 1 -> menuInstalarExtra();
                case 2 -> menuRegistrarReparacion();
                case 0 -> {
                }
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private static void menuRegistrarReparacion() {
        boolean menu = true;
        String matricula = null;
        List<Coche> coches = Servicios.listadoCoches();
        System.out.println(coches);
        while (menu) {
            System.out.println("Inserta la matrícula del Coche. Inserte 0 para cancelar:");
            matricula = sc.nextLine();
            String finalId = matricula;
            Coche cochent = coches.stream().filter(coche -> Objects.equals(coche.getMatricula(), finalId.trim())).findFirst().orElse(null);
            if (cochent != null || finalId.trim().equals("0")) menu = false;
        }
        if (matricula.trim().equals("0")) return;
        menu = true;
        int id = 0;
        List<Mecanico> mecanicos = Servicios.listadoMecanicos();
        System.out.println(mecanicos);
        while (menu) {
            System.out.println("Inserta la ID del Mecánico. Inserte 0 para cancelar:");
            try {
                id = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                id = -1;
            }
            int finalId = id;
            if (mecanicos.stream().anyMatch(mecanico -> mecanico.getId() == finalId) || finalId == 0) menu = false;
        }
        if (id == 0) return;
        System.out.println("¿Quieres insertar una fecha?");
        System.out.println("1. Si");
        System.out.println("2. No");
        int opcion = 0;
        LocalDateTime fecha = null;
        try {
            opcion = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            opcion = -1;
        }
        switch (opcion) {
            case 1 -> {
                System.out.println("Introducida fecha actual.");
                fecha = LocalDateTime.now();
            }
            default -> {
            }
        }
        System.out.println("¿Quieres insertar un coste?");
        System.out.println("1. Si");
        System.out.println("2. No");
        double coste = 0;
        try {
            opcion = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            opcion = -1;
        }
        switch (opcion) {
            case 1 -> {
                while (true) {
                    System.out.println("Introduce el coste:");
                    try {
                        coste = Double.parseDouble(sc.nextLine().trim());
                    } catch (NumberFormatException e) {
                        coste = -1;
                        System.out.println("--- ERROR: Numero no válido ---");
                    }
                    if (coste > 0) break;
                }
            }
            default -> {
            }
        }
        System.out.println("¿Quieres insertar una descripción?");
        System.out.println("1. Si");
        System.out.println("2. No");
        String descripcion = null;
        try {
            opcion = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            opcion = -1;
        }
        switch (opcion) {
            case 1 -> {
                System.out.println("Inserta Descripción:");
                ;
                descripcion = sc.nextLine();
            }
            default -> {
            }
        }

        Servicios.registrarReparacion(matricula, id, fecha, coste, descripcion);
    }

    private static void menuInstalarExtra() {
        boolean menu = true;
        String matricula = null;
        List<Coche> coches = Servicios.listadoCoches();
        System.out.println(coches);
        while (menu) {
            System.out.println("Inserta la matrícula del Coche. Inserte 0 para cancelar:");
            matricula = sc.nextLine();
            if (matricula.trim().equals("0") || Servicios.existeCoche(matricula)) menu = false;
        }
        if (matricula.trim().equals("0")) return;
        menu = true;
        long id = 0;
        List<Equipamiento> equipamientos = Servicios.listadoEquipamientos();
        System.out.println(equipamientos);
        while (menu) {
            System.out.println("Inserta la ID del Equipamiento. Inserte 0 para cancelar:");
            try {
                id = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                id = -1;
            }
            if (id == 0 || Servicios.existeEquipamiento(id)) menu = false;
        }
        if (id == 0) return;
        Servicios.instalarExtra(matricula, id);
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
                case 1 -> menuVenderCoche();
                case 0 -> {
                }
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private static void menuVenderCoche() {
        int opcion;
        do {
            System.out.println("\n--- ¿Quieres crear un nuevo propietario? ---");
            System.out.println("1. Si");
            System.out.println("2. No");
            System.out.println("0. Volver");

            try {
                // Leemos la línea completa y la convertimos
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = -1; // Fuerza el default en el switch
            }

            switch (opcion) {
                case 1 -> {
                    menuVentaCoche(menuNuevoPropietario());
                }
                case 2 -> {
                    menuVentaCoche(0);
                    return;
                }
                case 0 -> {
                }
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private static void menuVentaCoche(long id) {
        if (id == -1) return;
        if (id == 0) id = menuSelectPropietario();

        long idConcesionario = menuStockVentasConcesionario(0);
        if (idConcesionario == 0) return;

        List<Coche> coches = Servicios.stockConcesionario(idConcesionario);
        System.out.println(coches);
        String matricula = null;
        matricula = getIdCoche();
        if (matricula == null) return;

        double precio = 0;
        do {
            System.out.println("Indica el precio de venta:");
            try {
                precio = Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                precio = -1;
                System.out.println("--- ERROR: Numero no válido ---");
            }
        } while (!(precio > 0));

        Servicios.venderCoche(id, idConcesionario, matricula, precio);

    }

    private static String getIdCoche() {
        String matricula = null;
        boolean menu = true;
        while (menu) {
            System.out.println("Inserta la matrícula del Coche. Inserte 0 para cancelar:");
            matricula = sc.nextLine();
            boolean gucci = false;
            if (!matricula.trim().equals("0") && Servicios.existeCoche(matricula)) {
                if (Servicios.cochePropietario(matricula)) System.out.println("--- El coche ya tiene propietario ---");
                else gucci = true;
            } else if (matricula.trim().equals("0")) gucci = true;
            else System.out.println("--- El coche no existe ---");

            if (gucci) menu = false;
        }
        if (matricula.trim().equals("0")) return null;
        return matricula;
    }

    private static long menuSelectPropietario() {
        List<Propietario> propietarios = Servicios.listadoPropietarios();
        boolean menu = true;
        long id = 0;
        System.out.println(propietarios);
        while (menu) {
            System.out.println("Inserta la ID del Propietario:");
            try {
                // Leemos la línea completa y la convertimos
                id = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                id = -1; // Fuerza el default en el switch
            }
            if (Servicios.existePropietario(id)) menu = false;
        }
        return id;
    }

    private static long menuNuevoPropietario() {
        System.out.println("Introduce el DNI del Propietario:");
        String dni = sc.nextLine();
        System.out.println("Introduce el nombre del propietario:");
        String nombre = sc.nextLine();
        return Servicios.nuevoPropietario(dni, nombre);
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
                case 1 -> menuStockVentasConcesionario(1);
                case 2 -> menuHistorialMecanico();
                case 3 -> menuStockVentasConcesionario(2);
                case 4 -> menuCosteActualCoche();
                case 0 -> {
                }
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private static void menuCosteActualCoche() {
        String id = null;
        List<Coche> coches = Servicios.listadoCochesPropietarios();
        System.out.println(coches);
        id = getIdCoche();
        Servicios.costeActualCoche(id);
    }

    private static void menuHistorialMecanico() {
        boolean menu = true;
        long id = 0;
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
            if (id == 0 || Servicios.existeMecanico(id)) menu = false;
        }
        if (id == 0) return;
        Servicios.historialMecanico(id);
    }

    private static long menuStockVentasConcesionario(int stock) {
        boolean menu = true;
        long id = 0;
        List<Concesionario> concesionarios = Servicios.listadoConcesionarios();
        System.out.println(concesionarios);
        while (menu) {
            System.out.println("Inserta la ID del Concesionario. Inserte 0 para cancelar:");
            try {
                id = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                id = -1;
            }
            long finalId = id;
            if (id == 0 || Servicios.existeConcesionario(id)) menu = false;
        }
        if (id == 0) return 0;
        if (stock == 1) {
            System.out.println(Servicios.stockConcesionario(id));
            return 0;
        } else if (stock == 2) {
            List<Venta> ventas = Servicios.ventasPorConcesionario(id);
            System.out.println(ventas);
            System.out.println("Ganancias totales: " + Servicios.ganancias(ventas));
            return 0;
        } else return id;
    }
}