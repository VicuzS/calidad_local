package com.unmsm.scorely.services.imp;

import org.springframework.stereotype.Service;

import com.unmsm.scorely.dto.AceptarInvitacionResponse;
import com.unmsm.scorely.dto.InvitacionRequest;
import com.unmsm.scorely.dto.InvitacionResponse;
import com.unmsm.scorely.enums.EstadoInvitacion;
import com.unmsm.scorely.models.Alumno;
import com.unmsm.scorely.models.Invitacion;
import com.unmsm.scorely.models.Seccion;
import com.unmsm.scorely.repository.AlumnoRepository;
import com.unmsm.scorely.repository.InvitacionRepository;
import com.unmsm.scorely.repository.SeccionRepository;
import com.unmsm.scorely.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j // Para los log.info(), log.error(), log.debug()
public class InvitacionServiceImpl implements InvitacionService {

    private final InvitacionRepository invitacionRepository;
    private final SeccionRepository seccionRepository;
    private final AlumnoRepository alumnoRepository;
    private final TokenGenerator tokenGenerator;
    private final EmailService emailService;
    private final InvitacionValidator invitacionValidator;
    private final MatriculaService matriculaService;

    public InvitacionServiceImpl(
            InvitacionRepository invitacionRepository,
            SeccionRepository seccionRepository,
            AlumnoRepository alumnoRepository,
            TokenGenerator tokenGenerator,
            EmailService emailService,
            InvitacionValidator invitacionValidator,
            MatriculaService matriculaService
    ) {
        this.invitacionRepository = invitacionRepository;
        this.seccionRepository = seccionRepository;
        this.alumnoRepository = alumnoRepository;
        this.tokenGenerator = tokenGenerator;
        this.emailService = emailService;
        this.invitacionValidator = invitacionValidator;
        this.matriculaService = matriculaService;
    }

    @Override
    @Transactional // Es una transaccion para una base de datos
    public InvitacionResponse crearInvitacion(InvitacionRequest request, Integer idProfesor) {
        log.info("Creando invitación para crreo: {}", request.getCorreoAlumno());

        Seccion seccion = seccionRepository.findById(request.getIdSeccion())
                .orElseThrow(() -> new RuntimeException("Seccion no encontrada")); // Cambiar por exception

        if (!seccion.getProfesor().getIdProfesor().equals(idProfesor)) {
            throw new RuntimeException("No tienes permisos para invitar alumnos a esta sección");
        }

        if (invitacionRepository.existsPendingInvitationByCorreoAndSeccion(
                request.getCorreoAlumno(), request.getIdSeccion())) {
            throw new RuntimeException("Invitación duplicada no aceptada");
        }

        alumnoRepository.findByCorreo(request.getCorreoAlumno())
                .ifPresent(alumno -> {
                    if (matriculaService.estaMatriculado(alumno, seccion)){
                        throw new RuntimeException("Alumno ya está matriculado");
                    }
                });


        Invitacion invitacion = Invitacion.builder()
                .seccion(seccion)
                .correo(request.getCorreoAlumno())
                .estado(EstadoInvitacion.PENDIENTE)
                .token(tokenGenerator.generateToken())
                .build();

        invitacion =  invitacionRepository.save(invitacion);

        emailService.enviarInvitacion(invitacion);

        log.info("Invitación creada exitosamente con ID: {}", invitacion.getIdInvitaciones());

        return mapearAResponse(invitacion);
    }

    @Override
    public AceptarInvitacionResponse aceptarInvitacion(String token, Integer idAlumno) {
        log.info("Procesando aceptación de invitación con token {}", token);

        Invitacion invitacion = invitacionRepository.findByToken(token)
                .orElseThrow(()-> new RuntimeException("InvitacionNoEncontrada"));

        try{
            invitacionValidator.validarInvitacion(invitacion);
        } catch (Exception e){
            invitacionRepository.save(invitacion); // Guardar estado expirado
            return AceptarInvitacionResponse.expirada();
        }

        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        /*if (!invitacion.getCorreo().equalsIgnoreCase(alumno.getPersona().getCorreo())){
            throw new RuntimeException("Esta invitación no corresponde a tu correo");
        }*/

        if (matriculaService.estaMatriculado(alumno, invitacion.getSeccion())){
            invitacion.setEstado(EstadoInvitacion.ACEPTADA);
            invitacionRepository.save(invitacion);
            return AceptarInvitacionResponse.yaMatriculado(
                    invitacion.getSeccion().getNombreCurso()
            );
        }

        matriculaService.matricularAlumno(alumno, invitacion.getSeccion());

        invitacion.setEstado(EstadoInvitacion.ACEPTADA);
        invitacionRepository.save(invitacion);

        log.info("Invitacion aceptada exitosamente para el alumno {}", idAlumno);

        return AceptarInvitacionResponse.exitosa(
                invitacion.getSeccion().getIdSeccion(),
                invitacion.getSeccion().getNombreCurso()
        );
    }

    @Override
    public InvitacionResponse obtenerInvitacionPorToken(String token) {
        Invitacion invitacion = invitacionRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invitacion no encontrada"));

        return mapearAResponse(invitacion);
    }

    private InvitacionResponse mapearAResponse(Invitacion invitacion) {
        String nombreProfesor = String.format("%s %s",
                invitacion.getSeccion().getProfesor().getPersona(),
                "");

        return InvitacionResponse.builder()
                .idInvitacion(invitacion.getIdInvitaciones())
                .correo(invitacion.getCorreo())
                .nombreCurso(invitacion.getSeccion().getNombreCurso())
                .nombreProfesor(nombreProfesor)
                .estado(invitacion.getEstado().name())
                .token(invitacion.getToken())
                .fechaCreacion(invitacion.getFechaCreacion())
                .fechaExpiracion(invitacion.getFechaExpiracion())
                .mensaje("Invitación creada exitosamente")
                .build();
    }
}
