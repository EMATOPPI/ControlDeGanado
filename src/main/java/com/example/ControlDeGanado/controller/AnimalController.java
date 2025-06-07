package com.example.ControlDeGanado.controller;

import com.example.ControlDeGanado.dto.AnimalDTO;
import com.example.ControlDeGanado.model.Animal;
import com.example.ControlDeGanado.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/animales")
@CrossOrigin(origins = "*") // Para desarrollo. En producción, especificar dominios
public class AnimalController {

    private final AnimalService animalService;

    @Autowired
    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping
    public ResponseEntity<List<Animal>> listar() {
        try {
            List<Animal> animales = animalService.listarAnimales();
            return ResponseEntity.ok(animales);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Animal> obtenerPorId(@PathVariable Long id) {
        try {
            return animalService.obtenerPorId(id)
                    .map(animal -> ResponseEntity.ok(animal))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody AnimalDTO animalDTO, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Animal animal = convertirDTOaEntidad(animalDTO);
            Animal animalGuardado = animalService.guardarAnimal(animal);
            return new ResponseEntity<>(animalGuardado, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el animal: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody AnimalDTO animalDTO,
                                        BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            return animalService.obtenerPorId(id)
                    .map(animalExistente -> {
                        animalExistente.setNombre(animalDTO.getNombre());
                        animalExistente.setTipo(animalDTO.getTipo());
                        animalExistente.setColor(animalDTO.getColor());
                        animalExistente.setDescripcion(animalDTO.getDescripcion());
                        animalExistente.setPeso(animalDTO.getPeso());

                        Animal animalActualizado = animalService.guardarAnimal(animalExistente);
                        return ResponseEntity.ok(animalActualizado);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el animal: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            if (animalService.obtenerPorId(id).isPresent()) {
                animalService.eliminarAnimal(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el animal: " + e.getMessage());
        }
    }

    @GetMapping("/tipos")
    public ResponseEntity<List<String>> obtenerTipos() {
        try {
            List<String> tipos = animalService.obtenerTiposAnimales();
            return ResponseEntity.ok(tipos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            var estadisticas = animalService.obtenerEstadisticasAnimales();
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    // Método auxiliar para convertir DTO a entidad
    private Animal convertirDTOaEntidad(AnimalDTO dto) {
        Animal animal = new Animal();
        animal.setNombre(dto.getNombre());
        animal.setTipo(dto.getTipo());
        animal.setColor(dto.getColor());
        animal.setDescripcion(dto.getDescripcion());
        animal.setPeso(dto.getPeso());
        return animal;
    }
}