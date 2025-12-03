import { format } from "date-fns";
import { ptBR } from "date-fns/locale";
import {
  AlertCircle,
  Calendar,
  CheckCircle,
  Clock,
  MapPin,
  XCircle,
} from "lucide-react";
import React, { useEffect, useState } from "react";
import { toast } from "sonner@2.0.3";
import { enrollmentsApi } from "../services/api";
import { Enrollment, EnrollmentStatus } from "../types";

export const MyEnrollments: React.FC = () => {
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [filter, setFilter] = useState<"all" | EnrollmentStatus>("all");

  useEffect(() => {
    loadEnrollments();
  }, []);

  const loadEnrollments = async () => {
    try {
      const response = await enrollmentsApi.getMyEnrollments();
      setEnrollments(response.content);
    } catch (error) {
      toast.error("Erro ao carregar inscrições");
      console.error("Error loading enrollments:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCancelEnrollment = async (enrollmentId: string) => {
    if (!confirm("Tem certeza que deseja cancelar esta inscrição?")) {
      return;
    }

    try {
      await enrollmentsApi.cancelEnrollment(enrollmentId);
      toast.success("Inscrição cancelada com sucesso");
      loadEnrollments();
    } catch (error: any) {
      const errorMessage = error?.message || "Erro ao cancelar inscrição";
      toast.error(errorMessage);
      console.error("Error canceling enrollment:", error);
    }
  };

  const formatDate = (dateString: string) => {
    try {
      const date = new Date(dateString);
      return format(date, "dd 'de' MMMM 'de' yyyy 'às' HH:mm", {
        locale: ptBR,
      });
    } catch {
      return dateString;
    }
  };

  const getStatusBadge = (status: EnrollmentStatus) => {
    switch (status) {
      case EnrollmentStatus.CONFIRMED:
        return (
          <span className="inline-flex items-center gap-1 bg-green-100 text-green-700 px-3 py-1 rounded-full text-sm">
            <CheckCircle className="w-4 h-4" />
            Confirmada
          </span>
        );
      case EnrollmentStatus.PENDING:
        return (
          <span className="inline-flex items-center gap-1 bg-yellow-100 text-yellow-700 px-3 py-1 rounded-full text-sm">
            <Clock className="w-4 h-4" />
            Pendente
          </span>
        );
      case EnrollmentStatus.CANCELED:
        return (
          <span className="inline-flex items-center gap-1 bg-red-100 text-red-700 px-3 py-1 rounded-full text-sm">
            <XCircle className="w-4 h-4" />
            Cancelada
          </span>
        );
    }
  };

  const filteredEnrollments =
    filter === "all"
      ? enrollments
      : enrollments.filter((e) => e.status === filter);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-gray-900 mb-2">Minhas Inscrições</h1>
        <p className="text-gray-600">Gerencie suas inscrições em eventos</p>
      </div>

      <div className="mb-6 flex flex-wrap gap-2">
        <button
          onClick={() => setFilter("all")}
          className={`px-4 py-2 rounded-md transition-colors ${
            filter === "all"
              ? "bg-blue-600 text-white"
              : "bg-gray-200 text-gray-700 hover:bg-gray-300"
          }`}
        >
          Todas
        </button>
        <button
          onClick={() => setFilter(EnrollmentStatus.PENDING)}
          className={`px-4 py-2 rounded-md transition-colors ${
            filter === EnrollmentStatus.PENDING
              ? "bg-blue-600 text-white"
              : "bg-gray-200 text-gray-700 hover:bg-gray-300"
          }`}
        >
          Pendentes
        </button>
        <button
          onClick={() => setFilter(EnrollmentStatus.CONFIRMED)}
          className={`px-4 py-2 rounded-md transition-colors ${
            filter === EnrollmentStatus.CONFIRMED
              ? "bg-blue-600 text-white"
              : "bg-gray-200 text-gray-700 hover:bg-gray-300"
          }`}
        >
          Confirmadas
        </button>
        <button
          onClick={() => setFilter(EnrollmentStatus.CANCELED)}
          className={`px-4 py-2 rounded-md transition-colors ${
            filter === EnrollmentStatus.CANCELED
              ? "bg-blue-600 text-white"
              : "bg-gray-200 text-gray-700 hover:bg-gray-300"
          }`}
        >
          Canceladas
        </button>
      </div>

      {filteredEnrollments.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-lg shadow">
          <AlertCircle className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <h3 className="text-gray-600 mb-2">Nenhuma inscrição encontrada</h3>
          <p className="text-gray-500">
            {filter === "all"
              ? "Você ainda não se inscreveu em nenhum evento"
              : "Nenhuma inscrição com este status"}
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {filteredEnrollments.map((enrollment) => (
            <div
              key={enrollment.id}
              className="bg-white rounded-lg shadow-md p-6 border border-gray-200"
            >
              <div className="flex justify-between items-start mb-4">
                <div className="flex-1">
                  <h3 className="text-blue-600 mb-2">
                    {enrollment.eventTitle || "Evento"}
                  </h3>
                </div>
                <div>{getStatusBadge(enrollment.status)}</div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mb-4">
                <div className="flex items-center gap-2 text-gray-700">
                  <Calendar className="w-4 h-4 text-blue-600" />
                  <span className="text-sm">
                    {enrollment.eventStartDateTime
                      ? formatDate(enrollment.eventStartDateTime)
                      : "Data não disponível"}
                  </span>
                </div>

                <div className="flex items-center gap-2 text-gray-700">
                  <MapPin className="w-4 h-4 text-blue-600" />
                  <span className="text-sm">
                    {enrollment.eventLocation || "Local não informado"}
                  </span>
                </div>

                <div className="flex items-center gap-2 text-gray-700">
                  <Clock className="w-4 h-4 text-blue-600" />
                  <span className="text-sm">
                    Inscrito em {formatDate(enrollment.enrollmentDate)}
                  </span>
                </div>
              </div>

              {(enrollment.status === EnrollmentStatus.PENDING ||
                enrollment.status === EnrollmentStatus.CONFIRMED) && (
                <button
                  onClick={() => handleCancelEnrollment(enrollment.id)}
                  className="bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 transition-colors"
                >
                  Cancelar Inscrição
                </button>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
