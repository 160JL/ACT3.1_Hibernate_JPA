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
}
