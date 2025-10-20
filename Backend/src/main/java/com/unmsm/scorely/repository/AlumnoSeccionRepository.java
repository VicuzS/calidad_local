package com.unmsm.scorely.repository;

import com.unmsm.scorely.models.AlumnoSeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AlumnoSeccionRepository extends JpaRepository<AlumnoSeccion, Integer> {

    //Verifica si existe el alumno en la sección
    @Query("SELECT COUNT(als) > 0 FROM AlumnoSeccion als " +
            "WHERE als.alumno.idAlumno = :idAlumno " +
            "AND als.seccion.idSeccion = :idSeccion")
    boolean existsByAlumnoAndSeccion(
            @Param("idAlumno") Integer idAlumno,
            @Param("idSeccion") Integer idSeccion
    );

    @Query("SELECT als FROM AlumnoSeccion als " +
            "WHERE als.alumno.idAlumno = :idAlumno " +
            "AND als.seccion.idSeccion = :idSeccion")
    Optional<AlumnoSeccion> findByAlumnoAndSeccion(
            @Param("idAlumno") Integer idAlumno,
            @Param("idSeccion") Integer idSeccion
    );
}
