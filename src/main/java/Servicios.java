import jakarta.persistence.*;
import model.*;
import org.hibernate.exception.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Servicios {
    /**
     * Crea un entity manager para evitar repetición de codigo
     * @return EntityManager
     */
    private static EntityManager em() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");
            return emf.createEntityManager();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Comprueba que la conexión funciona, borra los datos de la base de datos y carga los datos de ejemplo
     */
    public static void iniciarEntityManager() {
        try {
            EntityManager em = em();

            System.out.println("Conexión establecida");

            try {

                //llamada al borrado de los datos
                borrarDatos();

                em.getTransaction().begin();

                // -------- CONCESIONARIOS --------
                Concesionario c1 = new Concesionario("AutoCenter Madrid", "Calle Gran Vía 123");
                Concesionario c2 = new Concesionario("MotorSur Sevilla", "Avenida Andalucía 45");

                em.persist(c1);
                em.persist(c2);

                // -------- PROPIETARIOS --------
                Propietario p1 = new Propietario("12345678A", "Juan Pérez");
                Propietario p2 = new Propietario("87654321B", "María López");

                em.persist(p1);
                em.persist(p2);

                // -------- MECÁNICOS --------
                Mecanico m1 = new Mecanico("Carlos Gómez", "Motor");
                Mecanico m2 = new Mecanico("Laura Fernández", "Electricidad");

                em.persist(m1);
                em.persist(m2);

                // -------- EQUIPAMIENTO --------
                Equipamiento e1 = new Equipamiento("Aire Acondicionado", 500);
                Equipamiento e2 = new Equipamiento("Navegador GPS", 750);
                Equipamiento e3 = new Equipamiento("Asientos de Cuero", 1200);

                em.persist(e1);
                em.persist(e2);
                em.persist(e3);

                // -------- COCHES --------
                Coche coche1 = new Coche("1234ABC", "Toyota", "Corolla", 18000, c1);
                Coche coche2 = new Coche("5678DEF", "BMW", "Serie 3", 32000, c1);
                Coche coche3 = new Coche("9012GHI", "Seat", "Ibiza", 15000, c2);

                coche2.setPropietario(p1); // vendido

                em.persist(coche1);
                em.persist(coche2);
                em.persist(coche3);

                // -------- RELACIÓN N:M COCHE - EQUIPAMIENTO --------
                coche1.getEquipamientos().add(e1);
                coche1.getEquipamientos().add(e2);

                coche2.getEquipamientos().add(e1);
                coche2.getEquipamientos().add(e3);

                // -------- REPARACIONES --------
                Reparacion r1 = new Reparacion(
                        LocalDateTime.of(2025, 1, 10, 10, 30),
                        150,
                        "Cambio de aceite",
                        coche2,
                        m1
                );

                Reparacion r2 = new Reparacion(
                        LocalDateTime.of(2025, 2, 5, 9, 0),
                        300,
                        "Sustitución batería",
                        coche2,
                        m2
                );

                em.persist(r1);
                em.persist(r2);

                // -------- VENTA --------
                Venta v1 = new Venta(
                        LocalDateTime.of(2025, 1, 15, 12, 0),
                        34500,
                        c1,
                        p1,
                        coche2
                );

                em.persist(v1);
                em.getTransaction().commit();
                System.out.println("--- Datos de prueba cargados correctamente ---");
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                em.getEntityManagerFactory().close();
            }
        } catch (Exception e) {
            System.out.println("--- ERROR: La base de datos no existe ---");
            System.out.println(e.getMessage());
        }
    }

    /**
     * borra todos los datos de la base de datos
     */
    private static void borrarDatos() {
        EntityManager em = em();

        em.getTransaction().begin();


        em.createQuery("DELETE FROM Venta").executeUpdate();
        em.createQuery("DELETE FROM Reparacion").executeUpdate();
        em.createNativeQuery("DELETE FROM coche_equipamiento").executeUpdate();
        em.createQuery("DELETE FROM Coche").executeUpdate();
        em.createQuery("DELETE FROM Propietario").executeUpdate();
        em.createQuery("DELETE FROM Mecanico").executeUpdate();
        em.createQuery("DELETE FROM Equipamiento").executeUpdate();
        em.createQuery("DELETE FROM Concesionario").executeUpdate();

        em.getTransaction().commit();
        em.getEntityManagerFactory().close();
    }

    /**
     * Crea un nuevo concesionario y lo guarde an la base de datos
     * @param nombre nombre del concesionario
     * @param direccion direccion del concesionario
     */
    public static void altaConcesionario(String nombre, String direccion) {
        try (EntityManager em = em()) {
            em.getTransaction().begin();

            Concesionario concesionario = new Concesionario(nombre, direccion);

            em.persist(concesionario);
            em.getTransaction().commit();
            System.out.println("Concesionario Creado con Exito");
        }
    }

    /**
     * Crea un nuevo coche y lo registra en la base de datos
     * @param matricula matricula
     * @param marca marca
     * @param modelo modelo
     * @param precioBase precio base
     * @param idConcesionario ID del concesionario
     */
    public static void altaCoche(String matricula, String marca, String modelo, Double precioBase, Long idConcesionario) {
        try (EntityManager em = em()) {

            em.getTransaction().begin();

            Concesionario concesionario = em.find(Concesionario.class, idConcesionario);

            Coche coche = new Coche(matricula, marca, modelo, precioBase, concesionario);

            em.persist(coche);

            em.getTransaction().commit();

            System.out.println("Coche creado con exito");
        }
    }

    /**
     * Instala un extra en un coche
     * @param matricula matricula del coche
     * @param id ID del equipamento
     */
    public static void instalarExtra(String matricula, long id) {
        try (EntityManager em = em()) {

            em.getTransaction().begin();

            Coche coche = em.find(Coche.class, matricula);

            Equipamiento equipamiento = em.find(Equipamiento.class, id);

            if (coche.getEquipamientos().contains(equipamiento)) {
                System.out.println("El coche ya tiene ese equipamiento");
            } else {
                coche.getEquipamientos().add(equipamiento);
                System.out.println("Equipamiento instalado");
                costeActualCoche(matricula);
            }

            em.getTransaction().commit();
        }
    }

    /**
     * Registra una nueva reparacion
     * @param matricula matricula del coche
     * @param id id del mecanico
     * @param fecha fecha de la reparacion
     * @param coste coste de la reparacion
     * @param descripcion descripcion de la reparacion
     */
    public static void registrarReparacion(String matricula, long id, LocalDateTime fecha, double coste, String descripcion) {

        try (EntityManager em = em()) {

            em.getTransaction().begin();

            Coche coche = em.find(Coche.class, matricula);

            Mecanico mecanico = em.find(Mecanico.class, id);

            Reparacion reparacion = new Reparacion(fecha, coste, descripcion, coche, mecanico);

            em.persist(reparacion);

            em.getTransaction().commit();

            System.out.println("Reparación registrada");
        }

    }

    /**
     * procesa la venta de un coche
     * @param idPropietario ID del propietario
     * @param idConcesionario ID del concesionario
     * @param matricula Matricula del coche
     * @param precio precio final de la venta
     */
    public static void venderCoche(Long idPropietario, Long idConcesionario, String matricula, double precio) {

        try (EntityManager em = em()) {
            em.getTransaction().begin();

            Coche coche = em.find(Coche.class, matricula);

            Concesionario concesionario = em.find(Concesionario.class, idConcesionario);

            Propietario propietario = em.find(Propietario.class, idPropietario);

            Venta venta = new Venta(LocalDateTime.now(), precio, concesionario, propietario, coche);

            em.persist(venta);
            em.getTransaction().commit();

            System.out.println("Venta realizada correctamente");
        }
    }

    /**
     * recoge la lista de los coches en un concesionario
     * @param id ID del concesionario
     * @return Lista de los coches en el concesionario
     */
    public static List<Coche> stockConcesionario(long id) {
        System.out.println("Listado de stock");

        try (EntityManager em = em()) {

            em.getTransaction().begin();

            List<Coche> coches = em.createQuery("SELECT c FROM Coche c").getResultList();
            List<Coche> cochent = new ArrayList<>();
            for (Coche coche : coches) {
                if (coche.getConcesionario().getId() == id) {
                    cochent.add(coche);
                }
            }
            return cochent;
        }


    }

    /**
     * recoge la lista de las reparaciones de un mecanico
     * @param id ID del mecanico
     * @return Lista de las reparaciones del mecanico
     */
    public static List<Reparacion> historialMecanico(long id) {
        System.out.println("Historial del mecánico");


        try (EntityManager em = em()) {
            em.getTransaction().begin();

            List<Reparacion> reparacions = em.createQuery("SELECT r FROM Reparacion r").getResultList();
            List<Reparacion> reparacionList = new ArrayList<>();
            for (Reparacion reparacion : reparacions) {
                if (reparacion.getMecanico().getId() == id) {
                    reparacionList.add(reparacion);
                }
            }
            return reparacionList;
        }


    }

    /**
     * recoge la lista de las ventas en un concesionario
     * @param id ID del concesionario
     * @return Lista de las ventas en el concesionario
     */
    public static List<Venta> ventasPorConcesionario(long id) {
        System.out.println("Ventas del concesionario");

        try (EntityManager em = em()) {
            em.getTransaction().begin();

            List<Venta> ventas = em.createQuery("SELECT v FROM Venta v").getResultList();
            List<Venta> ventant = new ArrayList();
            for (Venta venta : ventas) {
                if (venta.getConcesionario().getId() == id) {
                    ventant.add(venta);
                }
            }
            return ventant;
        }
    }

    /**
     * Calcula el coste total de un coche y lo imprime por pantalla
     * @param matricula Matrícula del coche
     */
    public static void costeActualCoche(String matricula) {
        System.out.println("Coste total del coche");
        try (EntityManager em = em();) {
            em.getTransaction().begin();

            double costeActual = 0.0;
            Coche coche = em.find(Coche.class, matricula);

            //suma el precio de la venta en caso de que el coche tenga propietario
            if (coche.getPropietario() != null) {
                List<Venta> ventas = em.createQuery("SELECT v FROM Venta v").getResultList();
                for (Venta venta : ventas) {
                    if (Objects.equals(venta.getCoche().getMatricula(), coche.getMatricula())) {
                        costeActual = costeActual + venta.getPrecio_final();
                        break;
                    }
                }
            }
            // si el coche no tiene propietario usa el precio base
            else {
                costeActual = costeActual + coche.getPrecio_base();
            }

            //suma el coste de todas las reparaciones del coche
            List<Reparacion> reparacions = em.createQuery("SELECT r FROM Reparacion r").getResultList();
            for (Reparacion reparacion : reparacions) {
                if (Objects.equals(reparacion.getCoche().getMatricula(), coche.getMatricula())) {
                    costeActual = costeActual + reparacion.getCoste();
                }
            }

            //cuma el precio de todos los extras del coche
            if (coche.getEquipamientos() != null) {
                for (Equipamiento equipamiento : coche.getEquipamientos()) {
                    costeActual = costeActual + equipamiento.getCoste();
                }
            }

            System.out.println("Precio actual del coche: " + costeActual);
        }
    }

    /**
     * Devuelve un listado con todos los concesionarios en la base de datos
     * @return Lista de los concesionarios
     */
    public static List<Concesionario> listadoConcesionarios() {
        try (EntityManager em = em()) {
            return em.createQuery("SELECT c FROM Concesionario c").getResultList();
        }

    }

    /**
     * Devuelve un listado con todos los mecanicos en la base de datos
     * @return Lista de los mecanicos
     */
    public static List<Mecanico> listadoMecanicos() {
        try (EntityManager em = em()) {
            return em.createQuery("SELECT m FROM Mecanico m").getResultList();
        }
    }

    /**
     * Devuelve un listado con todos los coches con propietario en la base de datos
     * @return Lista de los coches
     */
    public static List<Coche> listadoCochesPropietarios() {
        try (EntityManager em = em()) {
            List<Coche> listant = em.createQuery("SELECT c FROM Coche c LEFT JOIN FETCH c.equipamientos").getResultList();
            List<Coche> lista = new ArrayList();
            for (Coche coche : listant) {
                if (coche.getPropietario() != null) {
                    lista.add(coche);
                }
            }
            return lista;
        }
    }

    /**
     * Devuelve un listado con todos los coches en la base de datos
     * @return Lista de los coches
     */
    public static List<Coche> listadoCoches() {
        try (EntityManager em = em()) {
            return em.createQuery("SELECT c FROM Coche c").getResultList();
        }
    }

    /**
     * Devuelve un listado con todos los equipamientos en la base de datos
     * @return Lista de los equipamientos
     */
    public static List<Equipamiento> listadoEquipamientos() {
        try (EntityManager em = em()) {
            return em.createQuery("SELECT e FROM Equipamiento e").getResultList();
        }
    }

    /**
     * registra un nuevo propietario en la base de datos
     * @param dni DNI del propietario
     * @param nombre Nombre del propietario
     * @return ID del nuevo propietario o -1 en caso de error
     */
    public static long nuevoPropietario(String dni, String nombre) {
        try (EntityManager em = em()) {
            em.getTransaction().begin();
            Propietario propietario = new Propietario(dni, nombre);
            em.persist(propietario);
            em.getTransaction().commit();
            return propietario.getId();
        } catch (EntityExistsException | ConstraintViolationException | RollbackException e) {
            System.out.println("--- ERROR: Ya existe un propietario con ese DNI ---");
            return -1;
        }

    }

    /**
     * Devuelve un listado con todos los propietarios en la base de datos
     * @return Lista de los propietarios
     */
    public static List<Propietario> listadoPropietarios() {
        try (EntityManager em = em()) {
            return em.createQuery("SELECT p FROM Propietario p").getResultList();
        }
    }

    /**
     * comprueba si existe un coche
     * @param matricula Matricula del coche
     * @return boolean indicando si existe
     */
    public static boolean existeCoche(String matricula) {
        try (EntityManager em = em()) {
            em.getTransaction().begin();
            return em.find(Coche.class, matricula) != null;
        }
    }

    /**
     * comprueba si existe un equipamiento
     * @param id ID del equipaminento
     * @return boolean indicando si existe
     */
    public static boolean existeEquipamiento(Long id) {
        try (EntityManager em = em()) {
            em.getTransaction().begin();
            return em.find(Equipamiento.class, id) != null;
        }
    }

    /**
     * comprueba si existe un propietario
     * @param id ID del propietario
     * @return boolean indicando si existe
     */
    public static boolean existePropietario(Long id) {
        try (EntityManager em = em()) {
            em.getTransaction().begin();
            return em.find(Propietario.class, id) != null;
        }
    }

    /**
     * comprueba si existe un mecanico
     * @param id ID del mecanico
     * @return boolean indicando si existe
     */
    public static boolean existeMecanico(Long id) {
        try (EntityManager em = em()) {
            em.getTransaction().begin();
            return em.find(Mecanico.class, id) != null;
        }
    }

    /**
     * comprueba si existe un concesionario
     * @param id ID del concesionario
     * @return boolean indicando si existe
     */
    public static boolean existeConcesionario(Long id) {
        try (EntityManager em = em()) {
            em.getTransaction().begin();
            return em.find(Concesionario.class, id) != null;
        }
    }

    /**
     * Calcula todas las ganancias de un concesionario
     * @param ventas Lista de ventas
     * @return Precio total de todas las ventas
     */
    public static double ganancias(List<Venta> ventas) {
        double ganancias = 0;
        for (var venta : ventas) {
            ganancias = ganancias + venta.getPrecio_final();
        }
        return ganancias;
    }

    /**
     * Comprueba si un coche tiene propietario
     * @param matricula Matrícula del coche
     * @return Boolean indicando si el coche tiene propietario
     */
    public static boolean cochePropietario(String matricula) {
        try (EntityManager em = em()) {
            em.getTransaction().begin();
            Coche coche = em.find(Coche.class, matricula);
            return coche.getPropietario() != null;
        }
    }
}
