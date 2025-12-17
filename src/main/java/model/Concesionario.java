package model;

import jakarta.persistence.*;

@Entity
@Table(name = "Concesionario")
public class Concesionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nombre;
    private String direccion;
}
