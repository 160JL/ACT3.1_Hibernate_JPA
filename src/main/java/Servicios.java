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

    public static void meh(String matricula, String marca, String modelo, Double precioBase, Concesionario concesionario) {
        try (EntityManager em = em()) {

            em.getTransaction().begin();

            List<Coche> coches = em.createQuery("SELECT c FROM Coche c WHERE c.matricula = :matricula").setParameter("matricula", matricula).getResultList();

            Coche newCoche = coches.getFirst();

            newCoche.setMarca(marca);

            newCoche.setModelo(modelo);

            newCoche.setPrecio_base(precioBase);

            newCoche.setConcesionario(concesionario);

            em.getTransaction().commit();
        }
    }

    public static void instalarExtra() {
        System.out.println("Extra instalado y precio actualizado");
    }

    public static void registrarReparacion() {
        System.out.println("Reparación registrada");
    }

    public static void venderCoche() {
        System.out.println("Venta realizada correctamente");
    }

    public static void stockConcesionario(int id) {
        System.out.println("Listado de stock");

        try (EntityManager em = em()) {

            em.getTransaction().begin();

            List<Coche> coches = em.createQuery("SELECT c FROM Coche c").getResultList();
            for (Coche coche : coches) {
                if (coche.getConcesionario().getId() == id) {
                    System.out.println(coche);
                }
            }
        }


    }

    public static void historialMecanico(int id) {
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

    public static void ventasPorConcesionario(int id) {
        System.out.println("Ventas del concesionario");

        try (EntityManager em = em()) {
            em.getTransaction().begin();

            List<Venta> ventas = em.createQuery("SELECT v FROM Venta v").getResultList();
            for (Venta venta : ventas) {
                if (venta.getConcesionario().getId() == id) {
                    System.out.println(venta);
                }
            }
        }
    }

    public static void costeActualCoche(Coche coche) {
        System.out.println("Coste total del coche");
        try (EntityManager em = em();) {
            em.getTransaction().begin();

            double costeActual = 0.0;

            List<Venta> ventas = em.createQuery("SELECT v FROM Venta v").getResultList();
            for (Venta venta : ventas) {
                if (Objects.equals(venta.getCoche().getMatricula(), coche.getMatricula())) {
                    costeActual = costeActual + venta.getPrecio_final();
                }
            }

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
            return em.createQuery("SELECT c FROM Coche c JOIN FETCH c.propietario LEFT JOIN FETCH c.equipamientos").getResultList();
        }
    }
}
