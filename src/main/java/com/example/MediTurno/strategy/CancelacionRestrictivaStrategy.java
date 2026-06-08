package com.example.MediTurno.strategy;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.MediTurno.exception.ReglaNegocioException;
import com.example.MediTurno.model.Cita;

@Component
public class CancelacionRestrictivaStrategy implements PoliticaCancelacion {

	private static final long HORAS_MINIMAS = 24;

	@Override
	public void validar(Cita cita) {
		ReglasCancelacion.validarEstadoCancelable(cita);
		LocalDateTime inicioCita = LocalDateTime.of(cita.getFecha(), cita.getHoraInicio());
		long horasRestantes = Duration.between(LocalDateTime.now(), inicioCita).toHours();
		if (horasRestantes < HORAS_MINIMAS) {
			throw new ReglaNegocioException("La cita solo se puede cancelar con minimo 24 horas de anticipacion.");
		}
	}
}
