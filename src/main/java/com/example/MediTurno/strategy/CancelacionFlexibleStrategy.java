package com.example.MediTurno.strategy;

import org.springframework.stereotype.Component;

import com.example.MediTurno.model.Cita;

@Component
public class CancelacionFlexibleStrategy implements PoliticaCancelacion {

	@Override
	public void validar(Cita cita) {
		ReglasCancelacion.validarEstadoCancelable(cita);
	}
}
