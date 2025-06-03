package com.example.ControlDeGanado.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    // Tipo de venta: UNITARIA, LOTE, POR_KILO
    @Enumerated(EnumType.STRING)
    private TipoVenta tipoVenta = TipoVenta.UNITARIA;

    private Double precioTotal;

    // Precio unitario (calculado o ingresado)
    private Double precioUnitario;

    private Double pesoTotal;

    // Precio por kilo (para venta por kilo)
    private Double precioPorKilo;

    private Double porcentajeDescuento = 0.0;

    // Descuento por materia orgánica (%)
    private Double descuentoMateriaOrganica = 0.0;

    private Integer cantidad = 1;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    // Para venta por lote (varios animales del mismo tipo)
    private String tipoAnimalLote;
    private String razaAnimalLote;

    private String descripcion;

    // Moneda: PYG, USD
    private String moneda = "PYG";

    // Constructor vacío
    public Venta() {}


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

    public TipoVenta getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(TipoVenta tipoVenta) {
        this.tipoVenta = tipoVenta;
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

    public Double getPesoTotal() {
        return pesoTotal;
    }

    public void setPesoTotal(Double pesoTotal) {
        this.pesoTotal = pesoTotal;
    }

    public Double getPrecioPorKilo() {
        return precioPorKilo;
    }

    public void setPrecioPorKilo(Double precioPorKilo) {
        this.precioPorKilo = precioPorKilo;
    }

    public Double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(Double porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public Double getDescuentoMateriaOrganica() {
        return descuentoMateriaOrganica;
    }

    public void setDescuentoMateriaOrganica(Double descuentoMateriaOrganica) {
        this.descuentoMateriaOrganica = descuentoMateriaOrganica;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
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

    // Enum para tipo de venta
    public enum TipoVenta {
        UNITARIA,  // Venta de un solo animal
        LOTE,      // Venta de varios animales del mismo tipo
        POR_KILO   // Venta basada en el peso
    }
}