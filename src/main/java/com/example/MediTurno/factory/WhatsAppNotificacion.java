package com.example.MediTurno.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.MediTurno.model.Cita;

@Component
public class WhatsAppNotificacion implements Notificador {

	private static final Logger LOGGER = LoggerFactory.getLogger(WhatsAppNotificacion.class);

	@Override
	public CanalNotificacion canal() {
		return CanalNotificacion.WHATSAPP;
	}

	@Override
	public void enviar(Cita cita, String evento) {
		LOGGER.info("WhatsApp simulado: {} para cita {} enviado a {}", evento, cita.getId(),
				cita.getPaciente().getTelefono());
	}
}
