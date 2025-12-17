package model;

import jakarta.persistence.*;

@Entity
@Table(name = "Coche")
public class Coche {

    @Id
    private String matricula;
    private String marca;
    private String modelo;
    private double precio_base;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concesionario_id")
    private Concesionario concesionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id")
    private Propietario propietario;
}
