package com.example.MediTurno.strategy;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class PoliticaCancelacionSelector {

	private final Map<TipoPoliticaCancelacion, PoliticaCancelacion> politicas;

	public PoliticaCancelacionSelector(CancelacionFlexibleStrategy flexible,
			CancelacionRestrictivaStrategy restrictiva) {
		this.politicas = Map.of(
				TipoPoliticaCancelacion.FLEXIBLE, flexible,
				TipoPoliticaCancelacion.RESTRICTIVA, restrictiva);
	}

	public PoliticaCancelacion seleccionar(TipoPoliticaCancelacion tipo) {
		TipoPoliticaCancelacion tipoSeleccionado = tipo == null ? TipoPoliticaCancelacion.FLEXIBLE : tipo;
		return politicas.get(tipoSeleccionado);
	}
}
