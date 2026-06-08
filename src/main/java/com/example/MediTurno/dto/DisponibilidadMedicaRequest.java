package com.example.MediTurno.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DisponibilidadMedicaRequest(
		Long medicoId,
		LocalDate fecha,
		LocalTime horaInicio,
		LocalTime horaFin) {
}
