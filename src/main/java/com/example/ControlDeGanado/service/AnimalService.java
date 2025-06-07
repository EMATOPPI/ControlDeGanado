package com.example.ControlDeGanado.service;

import com.example.ControlDeGanado.model.Animal;
import com.example.ControlDeGanado.repository.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnimalService {

    private final AnimalRepository animalRepository;

    @Autowired
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
        validateAnimal(animal);
        return animalRepository.save(animal);
    }

    public void eliminarAnimal(Long id) {
        if (!animalRepository.existsById(id)) {
            throw new RuntimeException("Animal no encontrado con ID: " + id);
        }
        animalRepository.deleteById(id);
    }

    public List<String> obtenerTiposAnimales() {
        return animalRepository.findAll().stream()
                .map(Animal::getTipo)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public Map<String, Object> obtenerEstadisticasAnimales() {
        List<Animal> animales = animalRepository.findAll();

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalAnimales", animales.size());

        if (!animales.isEmpty()) {
            // Peso total y promedio
            double pesoTotal = animales.stream()
                    .mapToDouble(animal -> animal.getPeso() != null ? animal.getPeso() : 0.0)
                    .sum();
            double pesoPromedio = pesoTotal / animales.size();

            estadisticas.put("pesoTotal", Math.round(pesoTotal * 100.0) / 100.0);
            estadisticas.put("pesoPromedio", Math.round(pesoPromedio * 100.0) / 100.0);

            // Distribución por tipo
            Map<String, Long> distribucionTipos = animales.stream()
                    .filter(animal -> animal.getTipo() != null)
                    .collect(Collectors.groupingBy(Animal::getTipo, Collectors.counting()));
            estadisticas.put("distribucionTipos", distribucionTipos);

            // Animal más pesado y más liviano
            OptionalDouble pesoMaximo = animales.stream()
                    .mapToDouble(animal -> animal.getPeso() != null ? animal.getPeso() : 0.0)
                    .max();
            OptionalDouble pesoMinimo = animales.stream()
                    .mapToDouble(animal -> animal.getPeso() != null ? animal.getPeso() : Double.MAX_VALUE)
                    .min();

            if (pesoMaximo.isPresent()) {
                estadisticas.put("pesoMaximo", pesoMaximo.getAsDouble());
            }
            if (pesoMinimo.isPresent() && pesoMinimo.getAsDouble() != Double.MAX_VALUE) {
                estadisticas.put("pesoMinimo", pesoMinimo.getAsDouble());
            }
        } else {
            estadisticas.put("pesoTotal", 0.0);
            estadisticas.put("pesoPromedio", 0.0);
            estadisticas.put("distribucionTipos", new HashMap<>());
        }

        return estadisticas;
    }

    public List<Animal> buscarPorTipo(String tipo) {
        return animalRepository.findAll().stream()
                .filter(animal -> animal.getTipo() != null &&
                        animal.getTipo().toLowerCase().contains(tipo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Animal> buscarPorRangoPeso(Double pesoMinimo, Double pesoMaximo) {
        return animalRepository.findAll().stream()
                .filter(animal -> animal.getPeso() != null &&
                        animal.getPeso() >= pesoMinimo &&
                        animal.getPeso() <= pesoMaximo)
                .collect(Collectors.toList());
    }

    public boolean existeAnimal(Long id) {
        return animalRepository.existsById(id);
    }

    private void validateAnimal(Animal animal) {
        if (animal.getNombre() == null || animal.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del animal es obligatorio");
        }

        if (animal.getTipo() == null || animal.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo del animal es obligatorio");
        }

        if (animal.getPeso() == null || animal.getPeso() <= 0) {
            throw new IllegalArgumentException("El peso del animal debe ser mayor que cero");
        }

        // Validar longitud de campos
        if (animal.getNombre().length() > 100) {
            throw new IllegalArgumentException("El nombre no puede exceder 100 caracteres");
        }

        if (animal.getTipo().length() > 50) {
            throw new IllegalArgumentException("El tipo no puede exceder 50 caracteres");
        }

        if (animal.getColor() != null && animal.getColor().length() > 50) {
            throw new IllegalArgumentException("El color no puede exceder 50 caracteres");
        }

        if (animal.getDescripcion() != null && animal.getDescripcion().length() > 500) {
            throw new IllegalArgumentException("La descripción no puede exceder 500 caracteres");
        }

        // Validar peso razonable (entre 0.1 kg y 2000 kg)
        if (animal.getPeso() < 0.1 || animal.getPeso() > 2000) {
            throw new IllegalArgumentException("El peso debe estar entre 0.1 kg y 2000 kg");
        }
    }
}