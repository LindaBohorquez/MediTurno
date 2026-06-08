# Flujo completo para Postman

Base URL: `http://localhost:8080`

> Ejecuta las solicitudes en orden. Guarda los `id` que devuelve cada respuesta para usarlos en los pasos siguientes.

## 1. Crear especialidad

`POST /api/especialidades`

```json
{
  "nombre": "Medicina general",
  "descripcion": "Atencion primaria y control general"
}
```

## 2. Crear medico asociado

`POST /api/medicos`

```json
{
  "nombres": "Carlos",
  "apellidos": "Perez",
  "documento": "MED-100",
  "correo": "carlos.perez@mediturno.com",
  "telefono": "3101234567",
  "registroMedico": "RM-100",
  "especialidadId": 1
}
```

## 3. Crear paciente

`POST /api/pacientes`

```json
{
  "nombres": "Ana",
  "apellidos": "Gomez",
  "documento": "PAC-100",
  "correo": "ana.gomez@example.com",
  "telefono": "3001234567",
  "fechaNacimiento": "1995-01-10"
}
```

## 4. Crear servicio medico

`POST /api/servicios-medicos`

```json
{
  "nombre": "Consulta general",
  "descripcion": "Consulta medica general",
  "duracionMinutos": 30,
  "tarifa": 50000,
  "especialidadId": 1
}
```

## 5. Registrar disponibilidad medica inicial

`POST /api/disponibilidades`

```json
{
  "medicoId": 1,
  "fecha": "2026-06-15",
  "horaInicio": "09:00:00",
  "horaFin": "09:30:00"
}
```

## 6. Agendar cita

`POST /api/citas`

```json
{
  "pacienteId": 1,
  "medicoId": 1,
  "servicioMedicoId": 1,
  "disponibilidadMedicaId": 1,
  "motivo": "Control general",
  "observaciones": "Primera cita"
}
```

## 7. Verificar disponibilidad ocupada

`GET /api/disponibilidades/1`

Resultado esperado: `"estado": "OCUPADA"`.

## 8. Cancelar cita

`PATCH /api/citas/1/cancelar`

```json
{
  "motivo": "Paciente no puede asistir",
  "politica": "FLEXIBLE"
}
```

## 9. Verificar disponibilidad libre

`GET /api/disponibilidades/1`

Resultado esperado: `"estado": "DISPONIBLE"`.

## 10. Crear nueva disponibilidad para reprogramar

`POST /api/disponibilidades`

```json
{
  "medicoId": 1,
  "fecha": "2026-06-16",
  "horaInicio": "10:00:00",
  "horaFin": "10:30:00"
}
```

## 11. Reprogramar cita

`PATCH /api/citas/1/reprogramar`

```json
{
  "nuevaDisponibilidadId": 2,
  "observaciones": "Cambio de horario solicitado"
}
```

## 12. Generar reporte

`GET /api/reportes/citas?desde=2026-06-01&hasta=2026-06-30`

Capturas sugeridas para el documento:

1. Respuesta de crear especialidad.
2. Respuesta de crear medico.
3. Respuesta de agendar cita.
4. Consulta de disponibilidad ocupada.
5. Respuesta de cancelar cita.
6. Consulta de disponibilidad libre.
7. Respuesta de reprogramar cita.
8. Respuesta del reporte de citas.
