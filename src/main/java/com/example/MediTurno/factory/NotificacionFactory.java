package com.example.MediTurno.factory;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.MediTurno.exception.ReglaNegocioException;

@Component
public class NotificacionFactory {

	private final Map<CanalNotificacion, Notificador> notificadores = new EnumMap<>(CanalNotificacion.class);

	public NotificacionFactory(List<Notificador> notificadoresDisponibles) {
		notificadoresDisponibles.forEach(notificador -> notificadores.put(notificador.canal(), notificador));
	}

	public Notificador crear(CanalNotificacion canal) {
		CanalNotificacion canalSeleccionado = canal == null ? CanalNotificacion.EMAIL : canal;
		Notificador notificador = notificadores.get(canalSeleccionado);
		if (notificador == null) {
			throw new ReglaNegocioException("No existe notificador para el canal " + canalSeleccionado + ".");
		}
		return notificador;
	}
}
