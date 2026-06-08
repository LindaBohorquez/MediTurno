package com.example.MediTurno.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "citas")
public class Cita {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "paciente_id", nullable = false)
	private Paciente paciente;

	@ManyToOne(optional = false)
	@JoinColumn(name = "medico_id", nullable = false)
	private Medico medico;

	@ManyToOne(optional = false)
	@JoinColumn(name = "servicio_medico_id", nullable = false)
	private ServicioMedico servicioMedico;

	@ManyToOne(optional = false)
	@JoinColumn(name = "disponibilidad_medica_id", nullable = false)
	private DisponibilidadMedica disponibilidadMedica;

	@Column(nullable = false)
	private LocalDate fecha;

	@Column(nullable = false)
	private LocalTime horaInicio;

	@Column(nullable = false)
	private LocalTime horaFin;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoCita estado = EstadoCita.AGENDADA;

	@Column(length = 300)
	private String motivo;

	@Column(length = 500)
	private String observaciones;

	@Column(nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	@Column(nullable = false)
	private LocalDateTime fechaActualizacion;

	public Cita() {
	}

	public Cita(Paciente paciente, Medico medico, ServicioMedico servicioMedico,
			DisponibilidadMedica disponibilidadMedica, String motivo, String observaciones) {
		this.paciente = paciente;
		this.medico = medico;
		this.servicioMedico = servicioMedico;
		this.disponibilidadMedica = disponibilidadMedica;
		this.fecha = disponibilidadMedica.getFecha();
		this.horaInicio = disponibilidadMedica.getHoraInicio();
		this.horaFin = disponibilidadMedica.getHoraFin();
		this.motivo = motivo;
		this.observaciones = observaciones;
	}

	public void confirmar() {
		if (estado != EstadoCita.AGENDADA && estado != EstadoCita.REPROGRAMADA) {
			throw new IllegalStateException("Solo se pueden confirmar citas agendadas o reprogramadas.");
		}
		this.estado = EstadoCita.CONFIRMADA;
	}

	public void cancelar(String motivoCancelacion) {
		if (estado == EstadoCita.CANCELADA) {
			throw new IllegalStateException("La cita ya esta cancelada.");
		}
		if (estado == EstadoCita.ATENDIDA) {
			throw new IllegalStateException("Una cita atendida no se puede cancelar.");
		}
		this.estado = EstadoCita.CANCELADA;
		if (motivoCancelacion != null && !motivoCancelacion.isBlank()) {
			this.observaciones = motivoCancelacion;
		}
	}

	public void reprogramar(DisponibilidadMedica nuevaDisponibilidad, String nuevasObservaciones) {
		if (estado == EstadoCita.ATENDIDA) {
			throw new IllegalStateException("La cita no se puede reprogramar en su estado actual.");
		}
		this.medico = nuevaDisponibilidad.getMedico();
		this.disponibilidadMedica = nuevaDisponibilidad;
		this.fecha = nuevaDisponibilidad.getFecha();
		this.horaInicio = nuevaDisponibilidad.getHoraInicio();
		this.horaFin = nuevaDisponibilidad.getHoraFin();
		this.estado = EstadoCita.REPROGRAMADA;
		if (nuevasObservaciones != null && !nuevasObservaciones.isBlank()) {
			this.observaciones = nuevasObservaciones;
		}
	}

	@PrePersist
	void prePersist() {
		LocalDateTime ahora = LocalDateTime.now();
		this.fechaCreacion = ahora;
		this.fechaActualizacion = ahora;
	}

	@PreUpdate
	void preUpdate() {
		this.fechaActualizacion = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	public Medico getMedico() {
		return medico;
	}

	public void setMedico(Medico medico) {
		this.medico = medico;
	}

	public ServicioMedico getServicioMedico() {
		return servicioMedico;
	}

	public void setServicioMedico(ServicioMedico servicioMedico) {
		this.servicioMedico = servicioMedico;
	}

	public DisponibilidadMedica getDisponibilidadMedica() {
		return disponibilidadMedica;
	}

	public void setDisponibilidadMedica(DisponibilidadMedica disponibilidadMedica) {
		this.disponibilidadMedica = disponibilidadMedica;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(LocalTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	public LocalTime getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(LocalTime horaFin) {
		this.horaFin = horaFin;
	}

	public EstadoCita getEstado() {
		return estado;
	}

	public void setEstado(EstadoCita estado) {
		this.estado = estado;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public LocalDateTime getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}
}
