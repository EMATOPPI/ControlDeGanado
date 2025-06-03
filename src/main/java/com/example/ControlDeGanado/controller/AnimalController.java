package com.example.ControlDeGanado.controller;

import com.example.ControlDeGanado.dto.AnimalDTO;
import com.example.ControlDeGanado.model.Animal;
import com.example.ControlDeGanado.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/animales")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping
    public List<Animal> listar() {
        return animalService.listarAnimales();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Animal> obtenerPorId(@PathVariable Long id) {
        return animalService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Animal> crear(@Valid @RequestBody AnimalDTO animalDTO) {
        Animal animal = new Animal();
        animal.setNombre(animalDTO.getNombre());
        animal.setTipo(animalDTO.getTipo());
        animal.setColor(animalDTO.getColor());
        animal.setDescripcion(animalDTO.getDescripcion());
        animal.setPeso(animalDTO.getPeso());

        Animal guardado = animalService.guardarAnimal(animal);
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        animalService.eliminarAnimal(id);
        return ResponseEntity.noContent().build();
    }
}