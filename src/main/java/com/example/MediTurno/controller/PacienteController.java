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

import com.example.MediTurno.model.Paciente;
import com.example.MediTurno.service.PacienteService;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

	private final PacienteService pacienteService;

	public PacienteController(PacienteService pacienteService) {
		this.pacienteService = pacienteService;
	}

	@GetMapping
	public List<Paciente> listar() {
		return pacienteService.listar();
	}

	@GetMapping("/{id}")
	public Paciente obtener(@PathVariable Long id) {
		return pacienteService.obtener(id);
	}

	@PostMapping
	public ResponseEntity<Paciente> crear(@RequestBody Paciente paciente) {
		Paciente creado = pacienteService.crear(paciente);
		return ResponseEntity.created(URI.create("/api/pacientes/" + creado.getId())).body(creado);
	}

	@PutMapping("/{id}")
	public Paciente actualizar(@PathVariable Long id, @RequestBody Paciente paciente) {
		return pacienteService.actualizar(id, paciente);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> desactivar(@PathVariable Long id) {
		pacienteService.desactivar(id);
		return ResponseEntity.noContent().build();
	}
}
