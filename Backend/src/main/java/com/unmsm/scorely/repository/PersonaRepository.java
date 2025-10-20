package com.unmsm.scorely.repository;

import com.unmsm.scorely.models.PersonaUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface PersonaRepository extends CrudRepository<PersonaUser, Integer> {
    @Query(value = """
        SELECT *
        FROM v_usuario_info
        WHERE correo = ?1 AND contrasena = ?2
        LIMIT 1
        """, nativeQuery = true)
    Optional<PersonaUser> login(String correo, String contraseña);
}
