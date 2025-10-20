package com.unmsm.scorely.services.imp;

import com.unmsm.scorely.dto.LoginResponse;
import com.unmsm.scorely.repository.PersonaRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PersonaRepository personaRepository;

    public AuthService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    public LoginResponse login(String correo, String contraseña) {
        return personaRepository.login(correo, contraseña)
                .map(user -> {
                    if (user.getTipo() != null) {
                        user.setTipo(user.getTipo().toLowerCase());
                    }
                    return new LoginResponse(true, "Login exitoso", user);
                })
                .orElse(new LoginResponse(false, "Credenciales inválidas", null));
    }
}
