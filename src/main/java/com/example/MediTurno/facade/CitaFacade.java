package com.example.MediTurno.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MediTurno.dto.CancelarCitaRequest;
import com.example.MediTurno.dto.CitaRequest;
import com.example.MediTurno.dto.ReprogramarCitaRequest;
import com.example.MediTurno.model.Cita;
import com.example.MediTurno.service.CitaService;
import com.example.MediTurno.service.NotificacionService;

@Service
@Transactional
public class CitaFacade {

	private final CitaService citaService;
	private final NotificacionService notificacionService;

	public CitaFacade(CitaService citaService, NotificacionService notificacionService) {
		this.citaService = citaService;
		this.notificacionService = notificacionService;
	}

	public Cita agendar(CitaRequest request) {
		Cita cita = citaService.agendar(request);
		notificacionService.notificarCambioCita(cita, "Cita agendada");
		return cita;
	}

	public Cita confirmar(Long id) {
		Cita cita = citaService.confirmar(id);
		notificacionService.notificarCambioCita(cita, "Cita confirmada");
		return cita;
	}

	public Cita cancelar(Long id, CancelarCitaRequest request) {
		Cita cita = citaService.cancelar(id, request);
		notificacionService.notificarCambioCita(cita, "Cita cancelada");
		return cita;
	}

	public Cita reprogramar(Long id, ReprogramarCitaRequest request) {
		Cita cita = citaService.reprogramar(id, request);
		notificacionService.notificarCambioCita(cita, "Cita reprogramada");
		return cita;
	}
}
