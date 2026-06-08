package com.example.MediTurno.dto;

import java.math.BigDecimal;

public record ServicioMedicoRequest(
		String nombre,
		String descripcion,
		Integer duracionMinutos,
		BigDecimal tarifa,
		Long especialidadId,
		Boolean activo) {
}
