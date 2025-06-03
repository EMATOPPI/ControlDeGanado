package com.example.ControlDeGanado.service;


import com.example.ControlDeGanado.model.Animal;
import com.example.ControlDeGanado.model.Compra;
import com.example.ControlDeGanado.repository.AnimalRepository;
import com.example.ControlDeGanado.repository.CompraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final AnimalRepository animalRepository;

    public CompraService(CompraRepository compraRepository, AnimalRepository animalRepository) {
        this.compraRepository = compraRepository;
        this.animalRepository = animalRepository;
    }

    public List<Compra> listarCompras() {
        return compraRepository.findAll();
    }

    public Optional<Compra> obtenerPorId(Long id) {
        return compraRepository.findById(id);
    }

    public Compra guardarCompra(Compra compra) {
        return compraRepository.save(compra);
    }

    public void eliminarCompra(Long id) {
        compraRepository.deleteById(id);
    }

    public Optional<Animal> obtenerAnimalPorId(Long id) {
        return animalRepository.findById(id);
    }
}