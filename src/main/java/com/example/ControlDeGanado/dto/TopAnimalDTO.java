package com.example.ControlDeGanado.dto;

public class TopAnimalDTO {
    private Long animalId;
    private String nombreAnimal;
    private String tipoAnimal;
    private String razaAnimal;
    private Double montoTotal;
    private Integer cantidadOperaciones;

    public TopAnimalDTO() {}

    public TopAnimalDTO(Long animalId, String nombreAnimal, String tipoAnimal, String razaAnimal, Double montoTotal, Integer cantidadOperaciones) {
        this.animalId = animalId;
        this.nombreAnimal = nombreAnimal;
        this.tipoAnimal = tipoAnimal;
        this.razaAnimal = razaAnimal;
        this.montoTotal = montoTotal;
        this.cantidadOperaciones = cantidadOperaciones;
    }

    // Getters y Setters
    public Long getAnimalId() { return animalId; }
    public void setAnimalId(Long animalId) { this.animalId = animalId; }

    public String getNombreAnimal() { return nombreAnimal; }
    public void setNombreAnimal(String nombreAnimal) { this.nombreAnimal = nombreAnimal; }

    public String getTipoAnimal() { return tipoAnimal; }
    public void setTipoAnimal(String tipoAnimal) { this.tipoAnimal = tipoAnimal; }

    public String getRazaAnimal() { return razaAnimal; }
    public void setRazaAnimal(String razaAnimal) { this.razaAnimal = razaAnimal; }

    public Double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(Double montoTotal) { this.montoTotal = montoTotal; }

    public Integer getCantidadOperaciones() { return cantidadOperaciones; }
    public void setCantidadOperaciones(Integer cantidadOperaciones) { this.cantidadOperaciones = cantidadOperaciones; }
}