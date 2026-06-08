# Resumen tecnico del codigo MediTurno

Documento completo con imagenes: `docs/documentacion-entrega-final.md`

Imagenes de evidencia:

- `docs/imagenes/uml-clases.svg`
- `docs/imagenes/arquitectura-capas.svg`
- `docs/imagenes/patrones-diseno.svg`
- `docs/imagenes/endpoints-api.svg`
- `docs/imagenes/flujo-postman.svg`
- `docs/imagenes/evidencia-pruebas.svg`

## Clases creadas por capa

### Modelo JPA

- `Paciente`: datos personales y contacto del paciente.
- `Medico`: profesional de salud asociado a una `Especialidad`.
- `Especialidad`: catalogo de areas medicas.
- `ServicioMedico`: servicio ofrecido por una especialidad, con duracion y tarifa.
- `DisponibilidadMedica`: horario disponible, ocupado o cancelado de un medico.
- `Cita`: reserva medica con paciente, medico, servicio, disponibilidad, fecha, hora y estado.
- `EstadoCita`: `AGENDADA`, `CONFIRMADA`, `CANCELADA`, `REPROGRAMADA`, `ATENDIDA`.
- `EstadoDisponibilidad`: `DISPONIBLE`, `OCUPADA`, `CANCELADA`.

### Repositories

- `PacienteRepository`
- `MedicoRepository`
- `EspecialidadRepository`
- `ServicioMedicoRepository`
- `DisponibilidadMedicaRepository`
- `CitaRepository`

### Services

- `PacienteService`: registro, consulta, actualizacion y desactivacion de pacientes.
- `MedicoService`: gestion de medicos y validacion de especialidad activa.
- `EspecialidadService`: CRUD controlado de especialidades.
- `ServicioMedicoService`: gestion de servicios medicos.
- `DisponibilidadMedicaService`: consulta, registro, reserva, liberacion y cancelacion de disponibilidades.
- `CitaService`: reglas de negocio de agendamiento, confirmacion, cancelacion y reprogramacion.
- `NotificacionService`: coordina el envio simulado de notificaciones.
- `ReporteCitasService`: genera resumen administrativo de citas por rango de fechas.

### DTOs

- `CitaRequest`
- `CancelarCitaRequest`
- `ReprogramarCitaRequest`
- `DisponibilidadMedicaRequest`
- `MedicoRequest`
- `ServicioMedicoRequest`
- `ReporteCitasResponse`

### Manejo de errores

- `ReglaNegocioException`
- `RecursoNoEncontradoException`
- `ApiExceptionHandler`

## Endpoints disponibles

### Pacientes

- `GET /api/pacientes`
- `GET /api/pacientes/{id}`
- `POST /api/pacientes`
- `PUT /api/pacientes/{id}`
- `DELETE /api/pacientes/{id}`

### Medicos

- `GET /api/medicos`
- `GET /api/medicos?especialidadId={id}`
- `GET /api/medicos/{id}`
- `POST /api/medicos`
- `PUT /api/medicos/{id}`
- `DELETE /api/medicos/{id}`

### Especialidades

- `GET /api/especialidades`
- `GET /api/especialidades/{id}`
- `POST /api/especialidades`
- `PUT /api/especialidades/{id}`
- `DELETE /api/especialidades/{id}`

### Servicios medicos

- `GET /api/servicios-medicos`
- `GET /api/servicios-medicos?especialidadId={id}`
- `GET /api/servicios-medicos/{id}`
- `POST /api/servicios-medicos`
- `PUT /api/servicios-medicos/{id}`
- `DELETE /api/servicios-medicos/{id}`

### Disponibilidades

- `GET /api/disponibilidades`
- `GET /api/disponibilidades?medicoId={id}`
- `GET /api/disponibilidades?fecha=YYYY-MM-DD`
- `GET /api/disponibilidades?medicoId={id}&fecha=YYYY-MM-DD`
- `GET /api/disponibilidades/{id}`
- `POST /api/disponibilidades`
- `PATCH /api/disponibilidades/{id}/reservar`
- `PATCH /api/disponibilidades/{id}/liberar`
- `DELETE /api/disponibilidades/{id}`

### Citas

- `GET /api/citas`
- `GET /api/citas?pacienteId={id}`
- `GET /api/citas?medicoId={id}`
- `GET /api/citas/{id}`
- `POST /api/citas`
- `PATCH /api/citas/{id}/confirmar`
- `PATCH /api/citas/{id}/cancelar`
- `PATCH /api/citas/{id}/reprogramar`

### Reportes

- `GET /api/reportes/citas`
- `GET /api/reportes/citas?desde=YYYY-MM-DD&hasta=YYYY-MM-DD`
- `GET /api/reportes/citas?desde=YYYY-MM-DD&hasta=YYYY-MM-DD&medicoId={id}`

## Patrones implementados

### Factory Method

- `NotificacionFactory`: crea un `Notificador` segun el canal solicitado.
- `Notificador`: interfaz comun para canales de notificacion.
- `EmailNotificacion`, `SmsNotificacion`, `WhatsAppNotificacion`: implementaciones concretas.
- `CanalNotificacion`: enum de canales disponibles.

### Facade

- `CitaFacade`: expone operaciones simples para agendar, confirmar, cancelar y reprogramar citas.
- Orquesta `CitaService` y `NotificacionService`.
- `CitaController` usa `CitaFacade` para los flujos principales, evitando orquestacion en el controller.

### Strategy

- `PoliticaCancelacion`: contrato de validacion.
- `CancelacionFlexibleStrategy`: permite cancelar citas futuras no atendidas.
- `CancelacionRestrictivaStrategy`: exige minimo 24 horas de anticipacion.
- `PoliticaCancelacionSelector`: selecciona estrategia por `TipoPoliticaCancelacion`.

## Pruebas existentes

- `MediTurnoApplicationTests`: prueba de carga del contexto Spring.
- `CitaFlowMockMvcIntegrationTest`: prueba de integracion HTTP con `MockMvc` para el flujo completo.
- `ControllerResponseMockMvcTest`: respuestas `201`, `400`, `404` y `204`.
- `RepositoryH2Test`: queries reales con H2 para solapamientos, filtros y citas activas.
- `CitaServiceTest`: agendar cita valida, rechazar disponibilidad ocupada, cancelar cita valida, reprogramar cita y reprogramar cita cancelada.
- `ReporteCitasServiceTest`: reporte por rango, filtro por medico y rechazo de rango invalido.
- `DisponibilidadMedicaTest`: reservar, liberar y rechazar reserva repetida.
- `NotificacionFactoryTest`: crear notificacion por canal y usar email por defecto.
- `PoliticaCancelacionTest`: validar estrategias flexible y restrictiva, incluida cancelacion tardia.

Resultado de validacion:

```text
Tests run: 26, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Evidencia funcional

- Guia Postman: `docs/postman-flujo-completo.md`
- Coleccion Postman: `docs/MediTurno.postman_collection.json`
- Respuestas reales del flujo: `docs/evidencias-flujo`

## Cobertura y calidad

- JaCoCo esta configurado en `pom.xml`.
- `.\mvnw.cmd clean verify` genera `target/site/jacoco/index.html` y `target/site/jacoco/jacoco.xml`.
- Cobertura actual: lineas `74.69%`, instrucciones `68.83%`, clases `100%`.
- SonarQube queda preparado con `sonar-project.properties`.
- Para obtener metricas S7 completas en dashboard se debe ejecutar SonarQube/SonarCloud con un token valido.
