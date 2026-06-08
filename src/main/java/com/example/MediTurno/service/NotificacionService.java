package com.example.MediTurno.service;

import org.springframework.stereotype.Service;

import com.example.MediTurno.factory.CanalNotificacion;
import com.example.MediTurno.factory.NotificacionFactory;
import com.example.MediTurno.model.Cita;

@Service
public class NotificacionService {

	private final NotificacionFactory notificacionFactory;

	public NotificacionService(NotificacionFactory notificacionFactory) {
		this.notificacionFactory = notificacionFactory;
	}

	public void notificarCambioCita(Cita cita, String evento) {
		notificarCambioCita(cita, evento, CanalNotificacion.EMAIL);
	}

	public void notificarCambioCita(Cita cita, String evento, CanalNotificacion canal) {
		notificacionFactory.crear(canal).enviar(cita, evento);
	}
}
