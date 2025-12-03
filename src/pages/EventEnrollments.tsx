import { format } from "date-fns";
import { ptBR } from "date-fns/locale";
import {
  ArrowLeft,
  CheckCircle,
  Clock,
  Mail,
  User,
  XCircle,
} from "lucide-react";
import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "sonner@2.0.3";
import { enrollmentsApi, eventsApi } from "../services/api";
import { Enrollment, EnrollmentStatus, Event } from "../types";

export const EventEnrollments: React.FC = () => {
  const { eventId } = useParams<{ eventId: string }>();
  const navigate = useNavigate();
  const [event, setEvent] = useState<Event | null>(null);
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [filter, setFilter] = useState<"all" | EnrollmentStatus>("all");

  useEffect(() => {
    if (eventId) {
      loadData();
    }
  }, [eventId]);

  const loadData = async () => {
    if (!eventId) return;

    try {
      const [eventData, enrollmentsResponse] = await Promise.all([
        eventsApi.getEventById(eventId),
        enrollmentsApi.getEventEnrollments(eventId),
      ]);
      setEvent(eventData);
      setEnrollments(enrollmentsResponse.content);
    } catch (error) {
      toast.error("Erro ao carregar dados");
      console.error("Error loading data:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleConfirmEnrollment = async (enrollmentId: string) => {
    try {
      await enrollmentsApi.confirmEnrollment(enrollmentId);
      toast.success("Inscrição confirmada com sucesso");
      loadData();
    } catch (error) {
      toast.error("Erro ao confirmar inscrição");
      console.error("Error confirming enrollment:", error);
    }
  };

  const handleRejectEnrollment = async (enrollmentId: string) => {
    if (!confirm("Tem certeza que deseja rejeitar esta inscrição?")) {
      return;
    }

    try {
      await enrollmentsApi.rejectEnrollment(enrollmentId);
      toast.success("Inscrição rejeitada");
      loadData();
    } catch (error) {
      toast.error("Erro ao rejeitar inscrição");
      console.error("Error rejecting enrollment:", error);
    }
  };

  const formatDate = (dateString: string) => {
    try {
      const date = new Date(dateString);
      return format(date, "dd/MM/yyyy 'às' HH:mm", { locale: ptBR });
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

  const confirmedCount = enrollments.filter(
    (e) => e.status === EnrollmentStatus.CONFIRMED
  ).length;
  const pendingCount = enrollments.filter(
    (e) => e.status === EnrollmentStatus.PENDING
  ).length;

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!event) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <p className="text-gray-600">Evento não encontrado</p>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <button
        onClick={() => navigate("/my-events")}
        className="flex items-center gap-2 text-blue-600 hover:text-blue-700 mb-6"
      >
        <ArrowLeft className="w-4 h-4" />
        Voltar para Meus Eventos
      </button>

      <div className="bg-white rounded-lg shadow-md p-6 mb-8">
        <h1 className="text-gray-900 mb-2">{event.title}</h1>
        <p className="text-gray-600 mb-4">{event.description}</p>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-blue-50 p-4 rounded-lg">
            <div className="text-blue-600">Total de Inscrições</div>
            <div className="text-gray-900">{enrollments.length}</div>
          </div>
          <div className="bg-green-50 p-4 rounded-lg">
            <div className="text-green-600">Confirmadas</div>
            <div className="text-gray-900">
              {confirmedCount} / {event.capacity}
            </div>
          </div>
          <div className="bg-yellow-50 p-4 rounded-lg">
            <div className="text-yellow-600">Pendentes</div>
            <div className="text-gray-900">{pendingCount}</div>
          </div>
        </div>
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
          Todas ({enrollments.length})
        </button>
        <button
          onClick={() => setFilter(EnrollmentStatus.PENDING)}
          className={`px-4 py-2 rounded-md transition-colors ${
            filter === EnrollmentStatus.PENDING
              ? "bg-blue-600 text-white"
              : "bg-gray-200 text-gray-700 hover:bg-gray-300"
          }`}
        >
          Pendentes ({pendingCount})
        </button>
        <button
          onClick={() => setFilter(EnrollmentStatus.CONFIRMED)}
          className={`px-4 py-2 rounded-md transition-colors ${
            filter === EnrollmentStatus.CONFIRMED
              ? "bg-blue-600 text-white"
              : "bg-gray-200 text-gray-700 hover:bg-gray-300"
          }`}
        >
          Confirmadas ({confirmedCount})
        </button>
      </div>

      {filteredEnrollments.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-lg shadow">
          <Clock className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <h3 className="text-gray-600 mb-2">Nenhuma inscrição encontrada</h3>
          <p className="text-gray-500">
            {filter === "all"
              ? "Ainda não há inscrições para este evento"
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
              <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                <div className="flex-1">
                  <div className="flex items-center gap-3 mb-2">
                    <User className="w-5 h-5 text-blue-600" />
                    <span className="text-gray-900">
                      {enrollment.participantName || "Participante"}
                    </span>
                    {getStatusBadge(enrollment.status)}
                  </div>

                  <div className="flex items-center gap-2 text-gray-600 text-sm mb-2">
                    <Mail className="w-4 h-4" />
                    {enrollment.participantEmail || "N/A"}
                  </div>

                  <div className="flex items-center gap-2 text-gray-600 text-sm">
                    <Clock className="w-4 h-4" />
                    Inscrito em {formatDate(enrollment.enrollmentDate)}
                  </div>
                </div>

                {enrollment.status === EnrollmentStatus.PENDING && (
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleConfirmEnrollment(enrollment.id)}
                      className="flex items-center gap-2 bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 transition-colors"
                    >
                      <CheckCircle className="w-4 h-4" />
                      Confirmar
                    </button>
                    <button
                      onClick={() => handleRejectEnrollment(enrollment.id)}
                      className="flex items-center gap-2 bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 transition-colors"
                    >
                      <XCircle className="w-4 h-4" />
                      Rejeitar
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
