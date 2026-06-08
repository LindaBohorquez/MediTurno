# MediTurno - Documentacion de entrega final

## 1. Proposito

MediTurno es un sistema de gestion de citas medicas. El codigo implementa el dominio descrito en la documentacion academica: registro de pacientes y medicos, gestion de especialidades, servicios medicos, disponibilidad, agendamiento, cancelacion, reprogramacion, notificaciones y reportes.

El objetivo de esta documentacion es evidenciar que el codigo coincide con lo solicitado en el documento del profesor: arquitectura por capas, minimo 8 clases de dominio, patrones creacional/estructural/comportamiento, pruebas y evidencias del flujo funcional.

## 2. Arquitectura

![Arquitectura por capas](imagenes/arquitectura-capas.svg)

La arquitectura usada es MVC con Spring Boot y capas separadas:

- Controller: recibe solicitudes HTTP y delega.
- Facade: simplifica flujos complejos de cita.
- Service: contiene reglas de negocio.
- Repository: acceso a datos con Spring Data JPA.
- Model: entidades JPA y comportamiento basico de dominio.
- Factory: creacion de notificadores por canal.
- Strategy: politicas intercambiables de cancelacion.

## 3. Modelo de clases

![UML de clases](imagenes/uml-clases.svg)

Clases principales creadas:

| Capa | Clases |
| --- | --- |
| Modelo | `Paciente`, `Medico`, `Especialidad`, `ServicioMedico`, `DisponibilidadMedica`, `Cita`, `EstadoCita`, `EstadoDisponibilidad` |
| Repository | `PacienteRepository`, `MedicoRepository`, `EspecialidadRepository`, `ServicioMedicoRepository`, `DisponibilidadMedicaRepository`, `CitaRepository` |
| Service | `PacienteService`, `MedicoService`, `EspecialidadService`, `ServicioMedicoService`, `DisponibilidadMedicaService`, `CitaService`, `NotificacionService`, `ReporteCitasService` |
| Controller | `PacienteController`, `MedicoController`, `EspecialidadController`, `ServicioMedicoController`, `DisponibilidadMedicaController`, `CitaController`, `ReporteCitasController` |
| DTO | `CitaRequest`, `CancelarCitaRequest`, `ReprogramarCitaRequest`, `DisponibilidadMedicaRequest`, `MedicoRequest`, `ServicioMedicoRequest`, `ReporteCitasResponse` |
| Patrones | `CitaFacade`, `NotificacionFactory`, `Notificador`, `PoliticaCancelacion`, estrategias de cancelacion |

## 4. Patrones implementados

![Patrones de diseno](imagenes/patrones-diseno.svg)

### 4.1 Factory Method

Implementado en:

- `NotificacionFactory`
- `Notificador`
- `EmailNotificacion`
- `SmsNotificacion`
- `WhatsAppNotificacion`
- `CanalNotificacion`

Justificacion: evita condicionales repetidos para crear notificaciones por canal. Agregar un nuevo canal implica crear otra implementacion de `Notificador`.

### 4.2 Facade

Implementado en:

- `CitaFacade`

Justificacion: el flujo de cita involucra reglas de negocio, persistencia, disponibilidad y notificacion. La fachada expone operaciones simples para el controller:

- `agendar`
- `confirmar`
- `cancelar`
- `reprogramar`

### 4.3 Strategy

Implementado en:

- `PoliticaCancelacion`
- `CancelacionFlexibleStrategy`
- `CancelacionRestrictivaStrategy`
- `PoliticaCancelacionSelector`
- `TipoPoliticaCancelacion`

Justificacion: las reglas de cancelacion pueden variar sin modificar `CitaService`.

## 5. Endpoints disponibles

![Mapa de endpoints](imagenes/endpoints-api.svg)

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

## 6. Flujo funcional con evidencias

![Flujo funcional](imagenes/flujo-postman.svg)

El flujo completo probado fue:

1. Crear especialidad.
2. Crear medico asociado a la especialidad.
3. Crear paciente.
4. Crear servicio medico.
5. Registrar disponibilidad medica.
6. Agendar cita.
7. Verificar disponibilidad ocupada.
8. Cancelar cita.
9. Verificar disponibilidad libre.
10. Registrar nueva disponibilidad.
11. Reprogramar cita.
12. Generar reporte.

Evidencias disponibles:

| Paso | Archivo |
| --- | --- |
| Crear especialidad | `docs/evidencias-flujo/01-crear-especialidad.json` |
| Crear medico | `docs/evidencias-flujo/02-crear-medico.json` |
| Crear paciente | `docs/evidencias-flujo/03-crear-paciente.json` |
| Crear servicio medico | `docs/evidencias-flujo/04-crear-servicio-medico.json` |
| Registrar disponibilidad | `docs/evidencias-flujo/05-registrar-disponibilidad.json` |
| Agendar cita | `docs/evidencias-flujo/06-agendar-cita.json` |
| Verificar ocupada | `docs/evidencias-flujo/07-verificar-disponibilidad-ocupada.json` |
| Cancelar cita | `docs/evidencias-flujo/08-cancelar-cita.json` |
| Verificar libre | `docs/evidencias-flujo/09-verificar-disponibilidad-libre.json` |
| Nueva disponibilidad | `docs/evidencias-flujo/10-registrar-nueva-disponibilidad.json` |
| Reprogramar cita | `docs/evidencias-flujo/11-reprogramar-cita.json` |
| Generar reporte | `docs/evidencias-flujo/12-generar-reporte.json` |

Resultado verificado:

- Luego de agendar: disponibilidad `OCUPADA`.
- Luego de cancelar: disponibilidad `DISPONIBLE`.
- Luego de reprogramar: cita `REPROGRAMADA`.
- Reporte final: `total = 1`.

## 7. Pruebas

![Evidencia de pruebas](imagenes/evidencia-pruebas.svg)

Pruebas implementadas:

| Test | Cobertura |
| --- | --- |
| `MediTurnoApplicationTests` | Carga del contexto Spring |
| `CitaFlowMockMvcIntegrationTest` | Flujo HTTP completo con `MockMvc`: crear datos base, agendar, cancelar, liberar disponibilidad, reprogramar y generar reporte |
| `ControllerResponseMockMvcTest` | Respuestas de controller `201`, `400`, `404` y `204` |
| `RepositoryH2Test` | Repositories con H2 para solapamientos, filtros por medico/fecha/estado y citas activas |
| `CitaServiceTest` | Agendar cita valida, rechazar disponibilidad ocupada, cancelar, reprogramar, reprogramar cita cancelada |
| `ReporteCitasServiceTest` | Reporte por rango, filtro por medico y rechazo de rango de fechas invalido |
| `DisponibilidadMedicaTest` | Reservar, liberar y rechazar reserva repetida |
| `NotificacionFactoryTest` | Crear notificacion por canal y canal por defecto |
| `PoliticaCancelacionTest` | Estrategia flexible, restrictiva y cancelacion tardia |

Comando de validacion:

```powershell
.\mvnw.cmd clean verify
```

Resultado esperado:

```text
Tests run: 26, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 8. Cobertura y analisis de calidad

- JaCoCo queda configurado en `pom.xml` mediante `jacoco-maven-plugin`.
- El comando `.\mvnw.cmd clean verify` ejecuta las pruebas y genera cobertura.
- Reporte HTML local: `target/site/jacoco/index.html`.
- Reporte XML para SonarQube: `target/site/jacoco/jacoco.xml`.
- Cobertura actual: lineas `74.69%`, instrucciones `68.83%`, clases `100%`.
- SonarQube queda preparado mediante `sonar-project.properties` para revisar complejidad, duplicacion y deuda tecnica.

Comando sugerido para SonarQube local:

```powershell
.\mvnw.cmd sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=TU_TOKEN
```

## 9. Archivos de apoyo

- `docs/postman-flujo-completo.md`: guia paso a paso para Postman.
- `docs/MediTurno.postman_collection.json`: coleccion importable en Postman.
- `docs/resumen-tecnico-codigo.md`: resumen tecnico por capas.
- `docs/imagenes`: diagramas e imagenes generadas.
- `docs/evidencias-flujo`: respuestas JSON reales del flujo probado.
