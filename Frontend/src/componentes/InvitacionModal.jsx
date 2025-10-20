import PropTypes from "prop-types";
import { useEffect, useRef } from "react";
import { createPortal } from "react-dom";
import "../styles/InvitacionModal.css";

export default function InvitacionModal({ open, onClose, title, children }) {
  const dialogRef = useRef(null);

  InvitacionModal.propTypes = {
    open: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
    title: PropTypes.string,
    children: PropTypes.node,
  };

  useEffect(() => {
    const dialog = dialogRef.current;

    if (open && !dialog.open) {
      dialog.showModal(); // abre el modal nativo
      document.body.style.overflow = "hidden"; // bloquea scroll
    } else if (!open && dialog.open) {
      dialog.close(); // cierra el modal
      document.body.style.overflow = ""; // restaura scroll
    }

    return () => {
      document.body.style.overflow = "";
    };
  }, [open]);

  return createPortal(
    <dialog
      ref={dialogRef}
      className="modal"
      onCancel={onClose} 
    >
      <div className="modal-header">
        <h2 id="modal-title">{title}</h2>
        <button
          className="icon-btn"
          onClick={onClose}
          aria-label="Cerrar"
          type="button"
        >
          ×
        </button>
      </div>

      <div className="modal-body">{children}</div>
    </dialog>,
    document.body
  );
}