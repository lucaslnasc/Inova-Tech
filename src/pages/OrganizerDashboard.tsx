import { Calendar, Plus } from "lucide-react";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { CreateEventModal } from "../components/CreateEventModal";
import { EventCard } from "../components/EventCard";
import { eventsApi } from "../services/api";
import { Event } from "../types";

export const OrganizerDashboard: React.FC = () => {
  const navigate = useNavigate();
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [events, setEvents] = useState<Event[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadEvents();
  }, []);

  const loadEvents = async () => {
    try {
      const response = await eventsApi.getMyEvents(0, 10);
      setEvents(response.content);
    } catch (error) {
      toast.error("Erro ao carregar eventos");
      console.error("Error loading events:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleEventCreated = () => {
    setIsCreateModalOpen(false);
    loadEvents();
  };

  const handleViewEnrollments = (event: Event) => {
    navigate(`/event-enrollments/${event.id}`);
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
          <h1 className="text-gray-900 mb-2">Dashboard do Organizador</h1>
          <p className="text-gray-600">Gerencie seus eventos e inscrições</p>
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
        <div className="bg-white rounded-lg shadow p-8 text-center">
          <Calendar className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <h3 className="text-gray-600 mb-2">Bem-vindo ao seu painel!</h3>
          <p className="text-gray-500 mb-4">
            Crie um novo evento ou acesse "Meus Eventos" para gerenciar seus
            eventos existentes
          </p>
        </div>
      ) : (
        <div>
          <h2 className="text-xl font-semibold text-gray-900 mb-4">
            Seus Eventos Recentes
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {events.map((event) => (
              <EventCard
                key={event.id}
                event={event}
                onAction={handleViewEnrollments}
                actionLabel="Ver Inscrições"
                showOrganizer={false}
              />
            ))}
          </div>
        </div>
      )}

      <CreateEventModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onSuccess={handleEventCreated}
      />
    </div>
  );
};
