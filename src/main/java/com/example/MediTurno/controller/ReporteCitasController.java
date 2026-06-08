package com.example.MediTurno.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.MediTurno.dto.ReporteCitasResponse;
import com.example.MediTurno.service.ReporteCitasService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteCitasController {

	private final ReporteCitasService reporteCitasService;

	public ReporteCitasController(ReporteCitasService reporteCitasService) {
		this.reporteCitasService = reporteCitasService;
	}

	@GetMapping("/citas")
	public ReporteCitasResponse generar(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
			@RequestParam(required = false) Long medicoId) {
		return reporteCitasService.generar(desde, hasta, medicoId);
	}
}
