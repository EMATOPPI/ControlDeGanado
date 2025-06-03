package com.example.ControlDeGanado.dto;

import java.time.LocalDate;
import com.example.ControlDeGanado.model.Compra.TipoCompra;

public class CompraDTO {
    private LocalDate fecha;
    private TipoCompra tipoCompra = TipoCompra.UNITARIA;
    private Double precioTotal;
    private Double precioUnitario;
    private Integer cantidad = 1;
    private Double pesoTotal;
    private Long animalId;
    private String tipoAnimalLote;
    private String razaAnimalLote;
    private String descripcion;
    private String moneda = "PYG";

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

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
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
}