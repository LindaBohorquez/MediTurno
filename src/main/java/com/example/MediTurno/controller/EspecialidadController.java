package com.example.MediTurno.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.MediTurno.model.Especialidad;
import com.example.MediTurno.service.EspecialidadService;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

	private final EspecialidadService especialidadService;

	public EspecialidadController(EspecialidadService especialidadService) {
		this.especialidadService = especialidadService;
	}

	@GetMapping
	public List<Especialidad> listar() {
		return especialidadService.listar();
	}

	@GetMapping("/{id}")
	public Especialidad obtener(@PathVariable Long id) {
		return especialidadService.obtener(id);
	}

	@PostMapping
	public ResponseEntity<Especialidad> crear(@RequestBody Especialidad especialidad) {
		Especialidad creada = especialidadService.crear(especialidad);
		return ResponseEntity.created(URI.create("/api/especialidades/" + creada.getId())).body(creada);
	}

	@PutMapping("/{id}")
	public Especialidad actualizar(@PathVariable Long id, @RequestBody Especialidad especialidad) {
		return especialidadService.actualizar(id, especialidad);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> desactivar(@PathVariable Long id) {
		especialidadService.desactivar(id);
		return ResponseEntity.noContent().build();
	}
}
