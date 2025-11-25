import React from "react";
import {
  Navigate,
  Route,
  BrowserRouter as Router,
  Routes,
} from "react-router-dom";
import { Toaster } from "sonner@2.0.3";
import { Navbar } from "./components/Navbar";
import { ProtectedRoute } from "./components/ProtectedRoute";
import { AuthProvider, useAuth } from "./contexts/AuthContext";
import { EventEnrollments } from "./pages/EventEnrollments";
import { Login } from "./pages/Login";
import { MyEnrollments } from "./pages/MyEnrollments";
import { MyEvents } from "./pages/MyEvents";
import { OrganizerDashboard } from "./pages/OrganizerDashboard";
import { ParticipantDashboard } from "./pages/ParticipantDashboard";
import { Profile } from "./pages/Profile";
import { Register } from "./pages/Register";
import { UserType } from "./types";

const DashboardRouter: React.FC = () => {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (user.type === UserType.PARTICIPANTE) {
    return <ParticipantDashboard />;
  }

  return <OrganizerDashboard />;
};

const AppContent: React.FC = () => {
  const { user } = useAuth();

  return (
    <div className="min-h-screen bg-gray-50">
      {user && <Navbar />}

      <Routes>
        <Route
          path="/login"
          element={user ? <Navigate to="/dashboard" replace /> : <Login />}
        />
        <Route
          path="/register"
          element={user ? <Navigate to="/dashboard" replace /> : <Register />}
        />

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardRouter />
            </ProtectedRoute>
          }
        />

        <Route
          path="/my-events"
          element={
            <ProtectedRoute requireUserType={UserType.ORGANIZADOR}>
              <MyEvents />
            </ProtectedRoute>
          }
        />

        <Route
          path="/my-enrollments"
          element={
            <ProtectedRoute requireUserType={UserType.PARTICIPANTE}>
              <MyEnrollments />
            </ProtectedRoute>
          }
        />

        <Route
          path="/events/:eventId/enrollments"
          element={
            <ProtectedRoute requireUserType={UserType.ORGANIZADOR}>
              <EventEnrollments />
            </ProtectedRoute>
          }
        />

        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <Profile />
            </ProtectedRoute>
          }
        />

        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </div>
  );
};

export default function App() {
  return (
    <Router>
      <AuthProvider>
        <AppContent />
        <Toaster position="top-right" richColors />
      </AuthProvider>
    </Router>
  );
}
