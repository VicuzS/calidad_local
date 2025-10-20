package com.unmsm.scorely.controllers;

import com.unmsm.scorely.models.Seccion;
import com.unmsm.scorely.repository.ProfesorRepository;
import com.unmsm.scorely.services.SeccionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/secciones")
@CrossOrigin(origins = "*")
public class SeccionController {

    private final SeccionService seccionService;
    private final ProfesorRepository profesorRepository;

    public SeccionController(SeccionService seccionService, ProfesorRepository profesorRepository) {
        this.seccionService = seccionService;
        this.profesorRepository = profesorRepository;
    }
        
    // NUEVO: Obtener id_profesor desde id_persona
    @GetMapping("/profesor-id/{idPersona}")
    public ResponseEntity<Map<String, Object>> obtenerIdProfesor(@PathVariable Integer idPersona) {
        Map<String, Object> response = new HashMap<>();
        
        return profesorRepository.findIdProfesorByIdPersona(idPersona)
            .map(idProfesor -> {
                response.put("success", true);
                response.put("idProfesor", idProfesor);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                response.put("success", false);
                response.put("message", "No se encontró profesor con ese id_persona");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }
    // GET: Obtener secciones de un profesor
    @GetMapping("/profesor/{idProfesor}")
    public ResponseEntity<List<Seccion>> obtenerSeccionesPorProfesor(@PathVariable Integer idProfesor) {
        List<Seccion> secciones = seccionService.obtenerSeccionesPorProfesor(idProfesor);
        return ResponseEntity.ok(secciones);
    }

    // GET: Obtener secciones de un profesor por año
    @GetMapping("/profesor/{idProfesor}/anio/{anio}")
    public ResponseEntity<List<Seccion>> obtenerSeccionesPorProfesorYAnio(
            @PathVariable Integer idProfesor,
            @PathVariable Integer anio) {
        List<Seccion> secciones = seccionService.obtenerSeccionesPorProfesorYAnio(idProfesor, anio);
        return ResponseEntity.ok(secciones);
    }

    // POST: Crear nueva sección
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearSeccion(@RequestBody Seccion seccion) {
        Map<String, Object> response = new HashMap<>();
        try {
            Seccion nuevaSeccion = seccionService.crearSeccion(seccion);
            response.put("success", true);
            response.put("message", "Sección creada exitosamente");
            response.put("seccion", nuevaSeccion);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al crear la sección: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // DELETE: Eliminar sección
    @DeleteMapping("/{idSeccion}/profesor/{idProfesor}")
    public ResponseEntity<Map<String, Object>> eliminarSeccion(
            @PathVariable Integer idSeccion,
            @PathVariable Integer idProfesor) {
        Map<String, Object> response = new HashMap<>();
        
        boolean eliminado = seccionService.eliminarSeccion(idSeccion, idProfesor);
        
        if (eliminado) {
            response.put("success", true);
            response.put("message", "Sección eliminada exitosamente");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "No se pudo eliminar la sección. Verifique los permisos.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }
}