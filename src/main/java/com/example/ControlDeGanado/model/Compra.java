package com.example.ControlDeGanado.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "compras")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    // Tipo de compra: UNITARIA, LOTE
    @Enumerated(EnumType.STRING)
    private TipoCompra tipoCompra = TipoCompra.UNITARIA;

    // Precio total
    private Double precioTotal;

    // Precio unitario (calculado o ingresado)
    private Double precioUnitario;

    // Cantidad de animales
    private Integer cantidad = 1;

    // Peso total (opcional)
    private Double pesoTotal;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    // Para compra por lote (varios animales del mismo tipo)
    private String tipoAnimalLote;
    private String razaAnimalLote;

    private String descripcion;

    // Moneda: PYG, USD
    private String moneda = "PYG";

    // Constructor vacío
    public Compra() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public TipoCompra getTipoCompra() {
        return tipoCompra;
    }

    public void setTipoCompra(TipoCompra tipoCompra) {
        this.tipoCompra = tipoCompra;
    }

    public Double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(Double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPesoTotal() {
        return pesoTotal;
    }

    public void setPesoTotal(Double pesoTotal) {
        this.pesoTotal = pesoTotal;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public String getTipoAnimalLote() {
        return tipoAnimalLote;
    }

    public void setTipoAnimalLote(String tipoAnimalLote) {
        this.tipoAnimalLote = tipoAnimalLote;
    }

    public String getRazaAnimalLote() {
        return razaAnimalLote;
    }

    public void setRazaAnimalLote(String razaAnimalLote) {
        this.razaAnimalLote = razaAnimalLote;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }


    // Enum para tipo de compra
    public enum TipoCompra {
        UNITARIA, // Compra de un solo animal
        LOTE      // Compra de varios animales del mismo tipo
    }
}