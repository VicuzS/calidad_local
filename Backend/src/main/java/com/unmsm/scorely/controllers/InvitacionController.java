package com.unmsm.scorely.controllers;

import com.unmsm.scorely.dto.*;
import com.unmsm.scorely.services.InvitacionService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/invitaciones")
@Slf4j
public class InvitacionController {

    @Value("${frontend.url}")
    private String frontendUrlEnv;

    private final InvitacionService invitacionService;

    public InvitacionController(InvitacionService invitacionService) {
        this.invitacionService = invitacionService;
    }

    /**
     * Endpoint para que el profesor cree una invitación
     * POST /api/invitaciones
     */
    @PostMapping
    public ResponseEntity<ApiResponse<InvitacionResponse>> crearInvitacion(
            @Valid @RequestBody InvitacionRequest request,
            Authentication authentication
    ) {
        log.info("Solicitud de creación de invitación recibida");

        Integer idProfesor = obtenerIdProfesorDeAuthentication(authentication);

        InvitacionResponse response = invitacionService.crearInvitacion(request, idProfesor);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Invitación enviada exitosamente"));
    }

    /**
     * Endpoint para obtener información de una invitación por token
     * GET /api/invitaciones/info?token=xxx
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<InvitacionResponse>> obtenerInformacionInvitacion(
            @RequestParam String token
    ) {
        log.info("Consultando información de invitación");

        InvitacionResponse response = invitacionService.obtenerInvitacionPorToken(token);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Información de invitación obtenida")
        );
    }

    /**
     * Endpoint para aceptar invitación (redirige al frontend)
     * GET /api/invitaciones/aceptar?token=xxx
     *
     * Este endpoint maneja la lógica de redirección:
     * - Si el usuario está logueado -> redirige al frontend con modal de confirmación
     * - Si no está logueado -> redirige a login/registro
     */
    @GetMapping("/aceptar")
    public void aceptarInvitacionRedirect(
            @RequestParam String token,
            Authentication authentication,
            HttpServletResponse response
    ) throws IOException {
        log.info("Procesando aceptación de invitación via GET");

        String frontendUrl = frontendUrlEnv;

        // Verificar si el usuario está autenticado
        if (authentication == null || !authentication.isAuthenticated()) {
            // Usuario no logueado -> redirigir a login con el token
            String redirectUrl = String.format("%s/login?invitacion=%s", frontendUrl, token);
            response.sendRedirect(redirectUrl);
            return;
        }

        // Usuario logueado -> redirigir al home con modal de confirmación
        String redirectUrl = String.format("%s/seccionesPage?invitacion=%s", frontendUrl, token);
        response.sendRedirect(redirectUrl);
    }

    @PostMapping("/confirmar")
    public ResponseEntity<ApiResponse<AceptarInvitacionResponse>> confirmarAceptacion(
            @Valid @RequestBody AceptarInvitacionRequest request
    ) {
        log.info("Confirmando aceptación de invitación provisional");

        Integer idAlumno = request.getIdAlumno(); // viene del frontend

        AceptarInvitacionResponse response = invitacionService.aceptarInvitacion(
                request.getToken(),
                idAlumno
        );

        if (response.isExito()) {
            return ResponseEntity.ok(ApiResponse.success(response, "Te has unido al curso exitosamente"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.success(response, response.getMensaje()));
        }
    }

    /**
     * Endpoint para rechazar una invitación
     * POST /api/invitaciones/rechazar
     */
    @PostMapping("/rechazar")
    public ResponseEntity<ApiResponse<String>> rechazarInvitacion(
            @Valid @RequestBody AceptarInvitacionRequest request,
            Authentication authentication
    ) {
        log.info("Rechazando invitación");

        // TODO: Implementar lógica de rechazo si es necesario

        return ResponseEntity.ok(
                ApiResponse.success("Invitación rechazada", "Invitación rechazada exitosamente")
        );
    }

    /**
     * Extrae el ID del profesor del objeto Authentication
     */
    private Integer obtenerIdProfesorDeAuthentication(Authentication authentication) {
        // TODO: Implementar según el sistema de autenticación
        return 1; // CAMBIAR EN PRODUCCIÓN
    }

    /**
     * Extrae el ID del alumno del objeto Authentication
     */
    private Integer obtenerIdAlumnoDeAuthentication(Authentication authentication) {
        // TODO: Implementar según el sistema de autenticación
        return 1; // CAMBIAR EN PRODUCCIÓN
    }
}