import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "../paginas/Login";
import Register from "../paginas/Register";
import Dashboard from "../paginas/SeccionesPage";
import ProtectedRoute from "../context/ProtectedRoute";
import { useAuth } from "../context/AuthContext";
import InvitacionButton from "../componentes/InvitacionButton";
import SeccionesPage from "../paginas/SeccionesPage";
import TareasIndividuales from "../paginas/TareasIndividuales";
import CrearTareaPage from "../paginas/CrearTareaPage";

//Le puso el invitacionButton para probar nada más :vs

//Si quieres ver tu vista, reemplaza SeccionesPage por el tuyo aca abajo y al final pon en export verVista
//Restauralo despues de probarlo para que no haya conflictos en el merge luego
function verVista() {
  return (
    <BrowserRouter>
      <Routes>
        //<Route path="/" element={<SeccionesPage />} />
        //<Route path="*" element={<SeccionesPage />} />
        <Route path="/" element={<CrearTareaPage />} />
        <Route path="*" element={<CrearTareaPage />} />
      </Routes>
    </BrowserRouter>
  );
}

function AppRouter() {
  return (
    <BrowserRouter>
      <InvitacionButton />
      <Routes>
        <Route path="/login" element={<PublicRoute><Login /></PublicRoute>} />
        <Route path="/register" element={<PublicRoute><Register /></PublicRoute>} />
        <Route path="/" element={<Navigate to="/login" replace />} />

        <Route 
          path="/seccionesPage" 
          element={
            //<ProtectedRoute>
              <Dashboard />
            //</ProtectedRoute>
          } 
        />

        <Route 
          path="/tareasIndividuales"
          element={
            //<ProtectedRoute>
              <TareasIndividuales />
            //</ProtectedRoute>
          }
        />

        <Route 
          path="/CrearTareaPage"
          element={
            //<ProtectedRoute>
              <CrearTareaPage />
            //</ProtectedRoute>
          }
        />

        <Route path="/crearTarea" element={<CrearTareaPage />} />

        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

function PublicRoute({ children }) {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <div>Cargando...</div>;
  }

  if (isAuthenticated) {
    return <Navigate to="/seccionesPage" replace />;
  }

  return children;
}

export default AppRouter;