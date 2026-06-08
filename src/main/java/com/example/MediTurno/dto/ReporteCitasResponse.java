package com.example.MediTurno.dto;

import java.util.List;

import com.example.MediTurno.model.Cita;

public record ReporteCitasResponse(
		long total,
		long agendadas,
		long confirmadas,
		long canceladas,
		long reprogramadas,
		long atendidas,
		List<Cita> citas) {
}
