package com.example.MediTurno.factory;

import com.example.MediTurno.model.Cita;

public interface Notificador {

	CanalNotificacion canal();

	void enviar(Cita cita, String evento);
}
