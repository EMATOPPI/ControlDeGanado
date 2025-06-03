package com.example.ControlDeGanado.service;


import com.example.ControlDeGanado.model.Animal;
import com.example.ControlDeGanado.model.Venta;
import com.example.ControlDeGanado.repository.AnimalRepository;
import com.example.ControlDeGanado.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final AnimalRepository animalRepository;

    public VentaService(VentaRepository ventaRepository, AnimalRepository animalRepository) {
        this.ventaRepository = ventaRepository;
        this.animalRepository = animalRepository;
    }

    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> obtenerPorId(Long id) {
        return ventaRepository.findById(id);
    }

    public Venta guardarVenta(Venta venta) {
        return ventaRepository.save(venta);
    }

    public void eliminarVenta(Long id) {
        ventaRepository.deleteById(id);
    }

    public Optional<Animal> obtenerAnimalPorId(Long id) {
        return animalRepository.findById(id);
    }
}