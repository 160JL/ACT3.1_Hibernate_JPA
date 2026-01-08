package model;

import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Table(name = "Reparacion")
public class Reparacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDateTime fecha;
    private double coste;
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coche_matricula")
    private Coche coche;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mecanico_id")
    private Mecanico mecanico;

    public Reparacion(LocalDateTime fecha, double coste, String descripcion, Coche coche, Mecanico mecanico) {
        this.fecha = fecha;
        this.coste = coste;
        this.descripcion = descripcion;
        this.coche = coche;
        this.mecanico = mecanico;
    }

    public Reparacion() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getCoste() {
        return coste;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Coche getCoche() {
        return coche;
    }

    public void setCoche(Coche coche) {
        this.coche = coche;
    }

    public Mecanico getMecanico() {
        return mecanico;
    }

    public void setMecanico(Mecanico mecanico) {
        this.mecanico = mecanico;
    }

    @Override
    public String toString() {
        return "\nReparacion:" +
                "\n   id= " + id +
                "\n   fecha= " + fecha +
                "\n   coste= " + coste +
                "\n   descripcion= " + descripcion +
                "\n   coche= " + coche.getMatricula() +
                "\n   mecanico= " + mecanico.getNombre();
    }
}
