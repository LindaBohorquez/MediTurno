package com.example.MediTurno.strategy;

import com.example.MediTurno.model.Cita;

public interface PoliticaCancelacion {

	void validar(Cita cita);
}
