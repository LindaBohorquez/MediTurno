package com.example.MediTurno.model;

import java.time.LocalDate;
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
import jakarta.persistence.Table;

@Entity
@Table(name = "disponibilidades_medicas")
public class DisponibilidadMedica {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "medico_id", nullable = false)
	private Medico medico;

	@Column(nullable = false)
	private LocalDate fecha;

	@Column(nullable = false)
	private LocalTime horaInicio;

	@Column(nullable = false)
	private LocalTime horaFin;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoDisponibilidad estado = EstadoDisponibilidad.DISPONIBLE;

	public DisponibilidadMedica() {
	}

	public DisponibilidadMedica(Medico medico, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
		this.medico = medico;
		this.fecha = fecha;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
	}

	public void reservar() {
		if (estado != EstadoDisponibilidad.DISPONIBLE) {
			throw new IllegalStateException("La disponibilidad no esta libre para reservar.");
		}
		this.estado = EstadoDisponibilidad.OCUPADA;
	}

	public void liberar() {
		if (estado == EstadoDisponibilidad.CANCELADA) {
			throw new IllegalStateException("Una disponibilidad cancelada no se puede liberar.");
		}
		this.estado = EstadoDisponibilidad.DISPONIBLE;
	}

	public void cancelar() {
		this.estado = EstadoDisponibilidad.CANCELADA;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Medico getMedico() {
		return medico;
	}

	public void setMedico(Medico medico) {
		this.medico = medico;
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

	public EstadoDisponibilidad getEstado() {
		return estado;
	}

	public void setEstado(EstadoDisponibilidad estado) {
		this.estado = estado;
	}
}
