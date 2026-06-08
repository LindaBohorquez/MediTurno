package com.example.MediTurno.dto;

public record CitaRequest(
		Long pacienteId,
		Long medicoId,
		Long servicioMedicoId,
		Long disponibilidadMedicaId,
		String motivo,
		String observaciones) {
}
