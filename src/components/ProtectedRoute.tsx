import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import { UserType } from "../types";

interface ProtectedRouteProps {
  children: React.ReactNode;
  requireUserType?: UserType;
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requireUserType,
}) => {
  const { user, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (requireUserType && user.type !== requireUserType) {
    return <Navigate to="/dashboard" replace />;
  }

  return <>{children}</>;
};
