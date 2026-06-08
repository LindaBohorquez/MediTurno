package com.example.MediTurno.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.MediTurno.model.Cita;

@Component
public class EmailNotificacion implements Notificador {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotificacion.class);

	@Override
	public CanalNotificacion canal() {
		return CanalNotificacion.EMAIL;
	}

	@Override
	public void enviar(Cita cita, String evento) {
		LOGGER.info("Email simulado: {} para cita {} enviado a {}", evento, cita.getId(),
				cita.getPaciente().getCorreo());
	}
}
