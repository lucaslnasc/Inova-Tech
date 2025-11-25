import { Calendar, Home, LogOut, User } from "lucide-react";
import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import { UserType } from "../types";

export const Navbar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  if (!user) return null;

  return (
    <nav className="bg-white shadow-sm border-b">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center gap-8">
            <Link to="/dashboard" className="flex items-center gap-2">
              <Calendar className="w-6 h-6 text-blue-600" />
              <span className="text-blue-600">InovaTech Eventos</span>
            </Link>

            <div className="hidden md:flex items-center gap-4">
              <Link
                to="/dashboard"
                className="flex items-center gap-2 px-3 py-2 rounded-md hover:bg-gray-100 transition-colors"
              >
                <Home className="w-4 h-4" />
                Dashboard
              </Link>

              {user.type === UserType.ORGANIZADOR && (
                <Link
                  to="/my-events"
                  className="flex items-center gap-2 px-3 py-2 rounded-md hover:bg-gray-100 transition-colors"
                >
                  <Calendar className="w-4 h-4" />
                  Meus Eventos
                </Link>
              )}

              {user.type === UserType.PARTICIPANTE && (
                <Link
                  to="/my-enrollments"
                  className="flex items-center gap-2 px-3 py-2 rounded-md hover:bg-gray-100 transition-colors"
                >
                  <Calendar className="w-4 h-4" />
                  Minhas Inscrições
                </Link>
              )}
            </div>
          </div>

          <div className="flex items-center gap-4">
            <Link
              to="/profile"
              className="flex items-center gap-2 px-3 py-2 rounded-md hover:bg-gray-100 transition-colors"
            >
              <User className="w-4 h-4" />
              <span className="hidden md:inline">{user.name}</span>
            </Link>

            <button
              onClick={handleLogout}
              className="flex items-center gap-2 px-3 py-2 rounded-md hover:bg-gray-100 transition-colors text-red-600"
            >
              <LogOut className="w-4 h-4" />
              <span className="hidden md:inline">Sair</span>
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};
