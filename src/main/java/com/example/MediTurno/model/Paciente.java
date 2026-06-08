package com.example.MediTurno.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pacientes")
public class Paciente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 80)
	private String nombres;

	@Column(nullable = false, length = 80)
	private String apellidos;

	@Column(nullable = false, unique = true, length = 30)
	private String documento;

	@Column(nullable = false, unique = true, length = 120)
	private String correo;

	@Column(length = 30)
	private String telefono;

	private LocalDate fechaNacimiento;

	@Column(nullable = false)
	private boolean activo = true;

	public Paciente() {
	}

	public Paciente(String nombres, String apellidos, String documento, String correo, String telefono,
			LocalDate fechaNacimiento) {
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.documento = documento;
		this.correo = correo;
		this.telefono = telefono;
		this.fechaNacimiento = fechaNacimiento;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
}
