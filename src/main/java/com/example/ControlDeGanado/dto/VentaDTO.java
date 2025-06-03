package com.example.ControlDeGanado.dto;

import java.time.LocalDate;
import com.example.ControlDeGanado.model.Venta.TipoVenta;

public class VentaDTO {
    private LocalDate fecha;
    private TipoVenta tipoVenta = TipoVenta.UNITARIA;
    private Double precioTotal;
    private Double precioUnitario;
    private Double pesoTotal;
    private Double precioPorKilo;
    private Double porcentajeDescuento = 0.0;
    private Double descuentoMateriaOrganica = 0.0;
    private Integer cantidad = 1;
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