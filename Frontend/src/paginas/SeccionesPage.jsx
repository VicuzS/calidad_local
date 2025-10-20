import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "../styles/SeccionesPage.css"
import SeccionCard from "../componentes/SeccionCard";
import CrearSeccionModal from "../componentes/CrearSeccionModal"; // ← IMPORTAR

function SeccionesPage(){
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    
    const [secciones, setSecciones] = useState([]);
    const [anioSeleccionado, setAnioSeleccionado] = useState(2025);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [idProfesor, setIdProfesor] = useState(null);
    const [modalOpen, setModalOpen] = useState(false); // ← NUEVO ESTADO
    
    const BASE_URL = 'https://cswproyect-production.up.railway.app';

    // Obtener id_profesor al cargar
    useEffect(() => {
        if (user?.id) {
            obtenerIdProfesor();
        }
    }, [user]);

    // Cargar secciones cuando tengamos el id_profesor
    useEffect(() => {
        if (idProfesor) {
            cargarSecciones();
        }
    }, [idProfesor, anioSeleccionado]);

    const obtenerIdProfesor = async () => {
        try {
            const response = await fetch(
                `${BASE_URL}/api/secciones/profesor-id/${user.id}`,
                {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                    }
                }
            );

            const data = await response.json();
            if (data.success) {
                setIdProfesor(data.idProfesor);
            } else {
                setError("Usuario no es profesor");
            }
        } catch (err) {
            console.error("Error al obtener id_profesor:", err);
            setError("Error al verificar usuario");
        }
    };

    const cargarSecciones = async () => {
        setLoading(true);
        setError("");
        try {
            const response = await fetch(
                `${BASE_URL}/api/secciones/profesor/${idProfesor}/anio/${anioSeleccionado}`,
                {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                    }
                }
            );

            if (response.ok) {
                const data = await response.json();
                setSecciones(data);
            } else {
                setError("Error al cargar las secciones");
            }
        } catch (err) {
            console.error("Error al cargar secciones:", err);
            setError("Error de conexión con el servidor");
        } finally {
            setLoading(false);
        }
    };

    // ← MODIFICAR ESTA FUNCIÓN
    const handleAgregarSeccion = () => {
        setModalOpen(true);
    };

    // ← NUEVA FUNCIÓN
    const handleCrearSeccion = async (nombreSeccion) => {
        console.log("🔵 Iniciando creación de sección"); // ← AGREGAR
        console.log("🔵 idProfesor actual:", idProfesor); // ← AGREGAR
        console.log("🔵 nombreSeccion:", nombreSeccion); // ← AGREGAR
        console.log("🔵 anioSeleccionado:", anioSeleccionado); // ← AGREGAR
        
        setLoading(true);
        setModalOpen(false);
        
        try {
            const payload = {
                idProfesor: idProfesor,
                nombreCurso: nombreSeccion,
                anio: anioSeleccionado,
                codigo: Math.floor(Math.random() * 10000)
            };
            
            console.log("🟢 Payload a enviar:", JSON.stringify(payload, null, 2)); // ← AGREGAR
            
            const response = await fetch(`${BASE_URL}/api/secciones`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload)
            });

            const data = await response.json();
            console.log("🟢 Respuesta del servidor:", data); // ← AGREGAR

            if (data.success) {
                await cargarSecciones();
                console.log("✅ Sección creada:", data.seccion);
            } else {
                alert(data.message || "Error al crear la sección");
            }
        } catch (err) {
            console.error("💥 Error al crear sección:", err);
            alert("Error de conexión con el servidor");
        } finally {
            setLoading(false);
        }
    };

    const handleEliminarSeccion = async (idSeccion) => {
        setLoading(true);
        try {
            const response = await fetch(
                `${BASE_URL}/api/secciones/${idSeccion}/profesor/${idProfesor}`,
                {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                    }
                }
            );

            const data = await response.json();

            if (data.success) {
                await cargarSecciones();
            } else {
                alert(data.message || "Error al eliminar la sección");
            }
        } catch (err) {
            console.error("Error al eliminar sección:", err);
            alert("Error de conexión con el servidor");
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = async () => {
        const confirmar = window.confirm("¿Está seguro que desea cerrar sesión?");
        if (confirmar) {
            await logout();
            navigate("/login");
        }
    };

    return(
        <div className="seccionesPage-body">
            <div className="main-seccionesPage-container row">
                <div className="perfil-container col">
                    <h2>SALUDOS, {user?.nombres?.toUpperCase() || 'USUARIO'}</h2>
                    <i className="fa-solid fa-user"></i>
                </div>
                <div className="secciones-crud-container col">
                    <div className="secciones-header-container row">
                        <select 
                            value={anioSeleccionado} 
                            onChange={(e) => setAnioSeleccionado(Number(e.target.value))}
                            disabled={loading}
                        >
                            <option value="2024">2024</option>
                            <option value="2025">2025</option>
                            <option value="2026">2026</option>
                            <option value="2027">2027</option>
                        </select>
                        <button 
                            onClick={handleLogout} 
                            className="logout-button" 
                            title="Cerrar sesión"
                            disabled={loading}
                        > 
                            Cerrar Sesión
                        </button>
                        <button 
                            className="button-seccionesPage" 
                            onClick={handleAgregarSeccion}
                            disabled={loading || !idProfesor}
                        >
                            {loading ? "Cargando..." : "Agregar nueva sección"}
                        </button>
                    </div>

                    {error && (
                        <div style={{color: 'red', padding: '10px', textAlign: 'center'}}>
                            {error}
                        </div>
                    )}

                    <div className="secciones-container row">
                        {loading && secciones.length === 0 ? (
                            <p>Cargando secciones...</p>
                        ) : secciones.length === 0 ? (
                            <p>No hay secciones para este año</p>
                        ) : (
                            secciones.map((sec) => (
                                <SeccionCard 
                                    key={sec.idSeccion} 
                                    seccion={sec}
                                    onEliminar={handleEliminarSeccion}
                                />
                            ))
                        )}
                    </div>
                </div>
            </div>

            {/* ← AGREGAR EL MODAL */}
            <CrearSeccionModal
                open={modalOpen}
                onClose={() => setModalOpen(false)}
                onCrear={handleCrearSeccion}
                anioActual={anioSeleccionado}
            />
        </div>
    );
}

export default SeccionesPage;