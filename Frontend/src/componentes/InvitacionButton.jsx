import { useState } from "react";
import "../styles/InvitacionButton.css";
import InvitacionModal from "./InvitacionModal";

export default function InvitacionButton() {

  const [open, setOpen] = useState(false);
  const [email, setEmail] = useState("");
  const [mensaje, setMensaje] = useState("");
  
  const API_URL = import.meta.env.VITE_API_URL;

  const openModal = () => setOpen(true);
  const closeModal = () => { setOpen(false); setEmail(""); };

  const onSubmit = async (e) => {
    e.preventDefault();

    // Recuperar profesor actual logueado
    const profesor = JSON.parse(localStorage.getItem("currentUser"));
    console.log("Profesor actual:", profesor);

    if (!currentUser || currentUser.role !== "profesor") {
      setMensaje("Solo los profesores pueden enviar invitaciones");
      return;
    }

    const data = {
      correoAlumno: email,
      idSeccion: 1, // ID de la sección o curso al que se invita
    };

    try {

      const response = await fetch(`${API_URL}/api/invitaciones`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
      });

      const result = await response.json();
      console.log("Respuesta del backend:", result);

      if (response.ok && result.success) {
        setMensaje(result.message || "Invitación enviada correctamente 🎉");
      } else {
        setMensaje(result.message || "Error al enviar la invitación");
      }
    } catch (error) {
      console.error("Error al enviar la invitación:", error);
      setMensaje("Error de conexión. Inténtalo de nuevo.");
    }
  };

  return (
    <>
      <button className="btn btn-primary" onClick={openModal} type="button">
        Invitar Alumno
      </button>

      <InvitacionModal open={open} onClose={closeModal} title="Invitar Alumno">
        <form onSubmit={onSubmit} className="form-grid">
          <label className="field">
            <span>Correo del alumno</span>
            <input
              type="email"
              placeholder="alumno@correo.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              autoFocus
            />
          </label>

          <div className="btn-group">
            <button type="button" className="btn" onClick={closeModal}>
              Cancelar
            </button>
            <button type="submit" className="btn btn-primary">
              Enviar Invitación
            </button>
          </div>
        </form>
      </InvitacionModal>

      {mensaje && <p style={{ marginTop: "10px" }}>{mensaje}</p>}
    </>
  );
}
