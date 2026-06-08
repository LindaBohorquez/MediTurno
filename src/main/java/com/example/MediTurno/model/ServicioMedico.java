package com.example.MediTurno.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "servicios_medicos")
public class ServicioMedico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String nombre;

	@Column(length = 300)
	private String descripcion;

	@Column(nullable = false)
	private Integer duracionMinutos;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal tarifa = BigDecimal.ZERO;

	@ManyToOne(optional = false)
	@JoinColumn(name = "especialidad_id", nullable = false)
	private Especialidad especialidad;

	@Column(nullable = false)
	private boolean activo = true;

	public ServicioMedico() {
	}

	public ServicioMedico(String nombre, String descripcion, Integer duracionMinutos, BigDecimal tarifa,
			Especialidad especialidad) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.duracionMinutos = duracionMinutos;
		this.tarifa = tarifa;
		this.especialidad = especialidad;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getDuracionMinutos() {
		return duracionMinutos;
	}

	public void setDuracionMinutos(Integer duracionMinutos) {
		this.duracionMinutos = duracionMinutos;
	}

	public BigDecimal getTarifa() {
		return tarifa;
	}

	public void setTarifa(BigDecimal tarifa) {
		this.tarifa = tarifa;
	}

	public Especialidad getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(Especialidad especialidad) {
		this.especialidad = especialidad;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
}
