package com.unmsm.scorely.services;

import com.unmsm.scorely.dto.AceptarInvitacionResponse;
import com.unmsm.scorely.dto.InvitacionRequest;
import com.unmsm.scorely.dto.InvitacionResponse;

public interface InvitacionService {
    InvitacionResponse crearInvitacion(InvitacionRequest request, Integer idProfesor);
    AceptarInvitacionResponse aceptarInvitacion(String token, Integer idAlumno);
    InvitacionResponse obtenerInvitacionPorToken(String token);
}
