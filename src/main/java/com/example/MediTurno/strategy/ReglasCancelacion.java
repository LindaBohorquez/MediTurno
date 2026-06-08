package com.example.MediTurno.strategy;

import java.time.LocalDateTime;

import com.example.MediTurno.exception.ReglaNegocioException;
import com.example.MediTurno.model.Cita;
import com.example.MediTurno.model.EstadoCita;

final class ReglasCancelacion {

	private ReglasCancelacion() {
	}

	static void validarEstadoCancelable(Cita cita) {
		if (cita.getEstado() == EstadoCita.CANCELADA) {
			throw new ReglaNegocioException("La cita ya esta cancelada.");
		}
		if (cita.getEstado() == EstadoCita.ATENDIDA) {
			throw new ReglaNegocioException("Una cita atendida no se puede cancelar.");
		}
		LocalDateTime inicioCita = LocalDateTime.of(cita.getFecha(), cita.getHoraInicio());
		if (inicioCita.isBefore(LocalDateTime.now())) {
			throw new ReglaNegocioException("No se puede cancelar una cita que ya inicio o ya paso.");
		}
	}
}
