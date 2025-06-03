package com.example.ControlDeGanado.service;



import com.example.ControlDeGanado.model.Animal;
import com.example.ControlDeGanado.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    public List<Animal> listarAnimales() {
        return animalRepository.findAll();
    }

    public Optional<Animal> obtenerPorId(Long id) {
        return animalRepository.findById(id);
    }

    public Animal guardarAnimal(Animal animal) {
        return animalRepository.save(animal);
    }

    public void eliminarAnimal(Long id) {
        animalRepository.deleteById(id);
    }
}