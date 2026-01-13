import jakarta.persistence.*;
import model.*;
import org.hibernate.exception.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Servicios {
    private static EntityManager em() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");
            return emf.createEntityManager();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void iniciarEntityManager() {
        try {
            EntityManager em = em();

            System.out.println("Conexión establecida");

            try {

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

    public static void altaConcesionario(String nombre, String direccion) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Concesionario concesionario = new Concesionario(nombre,direccion);

        em.persist(concesionario);
        em.getTransaction().commit();
        em.getEntityManagerFactory().close();
        System.out.println("Concesionario Creado con Exito");
    }

    public static boolean altaCoche(String matricula) {
        try (EntityManager em = em()) {

            Coche newCoche = null;
            while (true) {
                try {
                    em.getTransaction().begin();
                    assert matricula != null;
                    if (!matricula.matches("^[0-9]{4}[A-Za-z]{3}$")) {
                        throw new NumberFormatException();
                    }
                    newCoche = new Coche(matricula, null, null, 0, null);
                    em.persist(newCoche);
                    em.getTransaction().commit();
                    return true;
                } catch (EntityExistsException | ConstraintViolationException | RollbackException e) {
                    System.out.println("Ya existe esta matricula");
                    return false;
                } catch (NumberFormatException e) {
                    System.out.println("Formato de matricula incorrecto (####***)");
                    return false;
                } finally {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                }
            }
        }
    }

    public static void altaCoche(String matricula, String marca, String modelo, Double precioBase, Concesionario concesionario) {
        try (EntityManager em = em()) {

            em.getTransaction().begin();

            Coche newCoche = em.find(Coche.class, matricula);

            newCoche.setMarca(marca);

            newCoche.setModelo(modelo);

            newCoche.setPrecio_base(precioBase);

            newCoche.setConcesionario(concesionario);

            em.getTransaction().commit();
        }
    }

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

    public static void registrarReparacion(String matricula, long id, LocalDateTime fecha, double coste, String descripcion) {

        try (EntityManager em = em()) {

            em.getTransaction().begin();

            Coche coche = em.find(Coche.class, matricula);

            Mecanico mecanico = em.find(Mecanico.class, id);

            Reparacion reparacion = new Reparacion(fecha,coste,descripcion,coche,mecanico);

            em.persist(reparacion);

            em.getTransaction().commit();

            System.out.println("Reparación registrada");
        }

    }

    public static void venderCoche(Long idPropietario,Long idConcesionario, String matricula, double precio) {

        try (EntityManager em = em()) {
            em.getTransaction().begin();

            Coche coche = em.find(Coche.class, matricula);

            Concesionario concesionario = em.find(Concesionario.class, idConcesionario);

            Propietario propietario = em.find(Propietario.class, idPropietario);

            Venta venta = new Venta(LocalDateTime.now(),precio,concesionario,propietario,coche);

            em.persist(venta);
            em.getTransaction().commit();

            System.out.println("Venta realizada correctamente");
        }
    }

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

    public static void historialMecanico(long id) {
        System.out.println("Historial del mecánico");


        try (EntityManager em = em()) {
            em.getTransaction().begin();

            List<Reparacion> reparacions = em.createQuery("SELECT r FROM Reparacion r").getResultList();
            for (Reparacion reparacion : reparacions) {
                if (reparacion.getMecanico().getId() == id) {
                    System.out.println(reparacion);
                }
            }
        }


    }

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

    public static void costeActualCoche(String matricula) {
        System.out.println("Coste total del coche");
        try (EntityManager em = em();) {
            em.getTransaction().begin();

            double costeActual = 0.0;
            Coche coche = em.find(Coche.class, matricula);

            if (coche.getPropietario()!=null) {
                List<Venta> ventas = em.createQuery("SELECT v FROM Venta v").getResultList();
                for (Venta venta : ventas) {
                    if (Objects.equals(venta.getCoche().getMatricula(), coche.getMatricula())) {
                        costeActual = costeActual + venta.getPrecio_final();
                        break;
                    }
                }
            } else {costeActual = costeActual + coche.getPrecio_base();}

            List<Reparacion> reparacions = em.createQuery("SELECT r FROM Reparacion r").getResultList();
            for (Reparacion reparacion : reparacions) {
                if (Objects.equals(reparacion.getCoche().getMatricula(), coche.getMatricula())) {
                    costeActual = costeActual + reparacion.getCoste();
                }
            }

            if (coche.getEquipamientos() != null) {
                for (Equipamiento equipamiento : coche.getEquipamientos()) {
                    costeActual = costeActual + equipamiento.getCoste();
                }
            }

            System.out.println("Precio actual del coche: " + costeActual);
        }
    }

    public static List<Concesionario> listadoConcesionarios() {
        try (EntityManager em = em()) {
            return em.createQuery("SELECT c FROM Concesionario c").getResultList();
        }

    }

    public static List<Mecanico> listadoMecanicos() {
        try (EntityManager em = em()) {
            return em.createQuery("SELECT m FROM Mecanico m").getResultList();
        }
    }

    public static List<Coche> listadoCochesPropietarios() {
        try (EntityManager em = em()) {
             List<Coche> listant = em.createQuery("SELECT c FROM Coche c LEFT JOIN FETCH c.equipamientos").getResultList();
             List<Coche> lista = new ArrayList();
             for (Coche coche : listant) {
                 if (coche.getPropietario() != null) {lista.add(coche);}
             }
             return lista;
        }
    }

    public static List<Coche> listadoCoches() {
        try (EntityManager em = em()) {
            return em.createQuery("SELECT c FROM Coche c").getResultList();
        }
    }

    public static List<Equipamiento> listadoEquipamientos() {
        try (EntityManager em = em()) {
            return em.createQuery("SELECT e FROM Equipamiento e").getResultList();
        }
    }

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

    public static List<Propietario> listadoPropietarios() {
        try (EntityManager em = em()) {
            return em.createQuery("SELECT p FROM Propietario p").getResultList();
        }
    }

    public static boolean existeCoche(String matricula) {
        try (EntityManager em = em()) {
            em.getTransaction().begin();
            return em.find(Coche.class, matricula) != null;
        }
    }

    public static boolean existeEquipamiento(Long id) {
        try (EntityManager em = em()) {
            em.getTransaction().begin();
            return em.find(Equipamiento.class, id) != null;
        }
    }

    public static boolean existePropietario(Long id) {
        try (EntityManager em = em()) {
            em.getTransaction().begin();
            return em.find(Propietario.class, id) != null;
        }
    }

    public static boolean existeMecanico(Long id) {
        try (EntityManager em = em()) {
            em.getTransaction().begin();
            return em.find(Mecanico.class, id) != null;
        }
    }

    public static boolean existeConcesionario(Long id) {
        try (EntityManager em = em()) {
            em.getTransaction().begin();
            return em.find(Concesionario.class, id) != null;
        }
    }

    public static double ganancias(List<Venta> ventas) {
        double ganancias = 0;
        for (var venta : ventas) {
            ganancias = ganancias + venta.getPrecio_final();
        }
        return ganancias;
    }

    public static boolean cochePropietario(String matricula) {
        try (EntityManager em = em()) {
            em.getTransaction().begin();
            Coche coche = em.find(Coche.class, matricula);
            return coche.getPropietario() != null;
        }
    }
}
