package com.example.MediTurno.dto;

public record MedicoRequest(
		String nombres,
		String apellidos,
		String documento,
		String correo,
		String telefono,
		String registroMedico,
		Long especialidadId,
		Boolean activo) {
}
