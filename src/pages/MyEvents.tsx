import { format } from "date-fns";
import { ptBR } from "date-fns/locale";
import { Calendar, Edit, Plus, Trash2, Users } from "lucide-react";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner@2.0.3";
import { CreateEventModal } from "../components/CreateEventModal";
import { EditEventModal } from "../components/EditEventModal";
import { eventsApi } from "../services/api";
import { Event } from "../types";

export const MyEvents: React.FC = () => {
  const [events, setEvents] = useState<Event[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [editingEvent, setEditingEvent] = useState<Event | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    loadEvents();
  }, []);

  const loadEvents = async () => {
    try {
      const response = await eventsApi.getMyEvents();
      setEvents(response.content);
    } catch (error) {
      toast.error("Erro ao carregar eventos");
      console.error("Error loading events:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDeleteEvent = async (eventId: string) => {
    if (!confirm("Tem certeza que deseja deletar este evento?")) {
      return;
    }

    try {
      await eventsApi.deleteEvent(eventId);
      toast.success("Evento deletado com sucesso");
      loadEvents();
    } catch (error: any) {
      const errorMessage = error?.message || "Erro ao deletar evento";
      toast.error(errorMessage);
      console.error("Error deleting event:", error);
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

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8 flex justify-between items-center">
        <div>
          <h1 className="text-gray-900 mb-2">Meus Eventos</h1>
          <p className="text-gray-600">Gerencie os eventos que você criou</p>
        </div>
        <button
          onClick={() => setIsCreateModalOpen(true)}
          className="flex items-center gap-2 bg-blue-600 text-white px-6 py-3 rounded-md hover:bg-blue-700 transition-colors"
        >
          <Plus className="w-5 h-5" />
          Criar Evento
        </button>
      </div>

      {events.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-lg shadow">
          <Calendar className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <h3 className="text-gray-600 mb-2">Nenhum evento criado</h3>
          <p className="text-gray-500 mb-4">
            Comece criando seu primeiro evento
          </p>
          <button
            onClick={() => setIsCreateModalOpen(true)}
            className="bg-blue-600 text-white px-6 py-3 rounded-md hover:bg-blue-700 transition-colors"
          >
            Criar Primeiro Evento
          </button>
        </div>
      ) : (
        <div className="space-y-4">
          {events.map((event) => (
            <div
              key={event.id}
              className="bg-white rounded-lg shadow-md p-6 border border-gray-200 hover:shadow-lg transition-shadow"
            >
              <div className="flex justify-between items-start mb-4">
                <div className="flex-1">
                  <h3 className="text-blue-600 mb-2">{event.title}</h3>
                  <p className="text-gray-600 mb-3">{event.description}</p>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mb-4">
                <div className="flex items-center gap-2 text-gray-700">
                  <Calendar className="w-4 h-4 text-blue-600" />
                  <span className="text-sm">
                    {formatDate(event.startDateTime)}
                  </span>
                </div>

                <div className="flex items-center gap-2 text-gray-700">
                  <Users className="w-4 h-4 text-blue-600" />
                  <span className="text-sm">
                    {event.currentEnrollments || 0} / {event.capacity}{" "}
                    participantes
                  </span>
                </div>
              </div>

              <div className="flex flex-wrap gap-2">
                <button
                  onClick={() => navigate(`/events/${event.id}/enrollments`)}
                  className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors"
                >
                  <Users className="w-4 h-4" />
                  Gerenciar Inscrições
                </button>

                <button
                  onClick={() => setEditingEvent(event)}
                  className="flex items-center gap-2 bg-gray-600 text-white px-4 py-2 rounded-md hover:bg-gray-700 transition-colors"
                >
                  <Edit className="w-4 h-4" />
                  Editar
                </button>

                <button
                  onClick={() => handleDeleteEvent(event.id)}
                  className="flex items-center gap-2 bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 transition-colors"
                >
                  <Trash2 className="w-4 h-4" />
                  Deletar
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      <CreateEventModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onSuccess={loadEvents}
      />

      {editingEvent && (
        <EditEventModal
          event={editingEvent}
          isOpen={!!editingEvent}
          onClose={() => setEditingEvent(null)}
          onSuccess={loadEvents}
        />
      )}
    </div>
  );
};
