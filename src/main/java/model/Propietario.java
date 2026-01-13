package model;

import jakarta.persistence.*;

@Entity
@Table(name = "Propietario")
public class Propietario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String dni;
    private String nombre;

    public Propietario(String dni, String nombre) {
        this.dni = dni;
        this.nombre = nombre;
    }

    public Propietario() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "\nPropietario:" +
                "\n   id= " + id +
                "\n   dni= " + dni +
                "\n   nombre= " + nombre;
    }
}
