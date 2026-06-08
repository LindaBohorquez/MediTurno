package com.example.MediTurno.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.MediTurno.dto.CancelarCitaRequest;
import com.example.MediTurno.dto.CitaRequest;
import com.example.MediTurno.dto.ReprogramarCitaRequest;
import com.example.MediTurno.facade.CitaFacade;
import com.example.MediTurno.model.Cita;
import com.example.MediTurno.service.CitaService;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

	private final CitaService citaService;
	private final CitaFacade citaFacade;

	public CitaController(CitaService citaService, CitaFacade citaFacade) {
		this.citaService = citaService;
		this.citaFacade = citaFacade;
	}

	@GetMapping
	public List<Cita> listar(@RequestParam(required = false) Long pacienteId,
			@RequestParam(required = false) Long medicoId) {
		return citaService.listar(pacienteId, medicoId);
	}

	@GetMapping("/{id}")
	public Cita obtener(@PathVariable Long id) {
		return citaService.obtener(id);
	}

	@PostMapping
	public ResponseEntity<Cita> agendar(@RequestBody CitaRequest request) {
		Cita creada = citaFacade.agendar(request);
		return ResponseEntity.created(URI.create("/api/citas/" + creada.getId())).body(creada);
	}

	@PatchMapping("/{id}/confirmar")
	public Cita confirmar(@PathVariable Long id) {
		return citaFacade.confirmar(id);
	}

	@PatchMapping("/{id}/cancelar")
	public Cita cancelar(@PathVariable Long id, @RequestBody(required = false) CancelarCitaRequest request) {
		return citaFacade.cancelar(id, request);
	}

	@PatchMapping("/{id}/reprogramar")
	public Cita reprogramar(@PathVariable Long id, @RequestBody ReprogramarCitaRequest request) {
		return citaFacade.reprogramar(id, request);
	}
}
