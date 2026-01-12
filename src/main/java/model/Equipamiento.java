package model;

import jakarta.persistence.*;

@Entity
@Table(name = "Equipamiento")
public class Equipamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nombre;
    private double coste;

    public Equipamiento(String nombre, double coste) {
        this.nombre = nombre;
        this.coste = coste;
    }

    public Equipamiento() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getCoste() {
        return coste;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }

    @Override
    public String toString() {
        return "\nEquipamiento:" +
                "\n   id= " + id +
                "\n   nombre= " + nombre +
                "\n   coste= " + coste;
    }
}
