import { Calendar, Search } from "lucide-react";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner@2.0.3";
import { EventCard } from "../components/EventCard";
import { enrollmentsApi, eventsApi } from "../services/api";
import { Event } from "../types";

export const ParticipantDashboard: React.FC = () => {
  const navigate = useNavigate();
  const [events, setEvents] = useState<Event[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [enrolledEventIds, setEnrolledEventIds] = useState<Set<string>>(
    new Set()
  );

  useEffect(() => {
    loadEvents();
    loadMyEnrollments();
  }, []);

  const loadEvents = async () => {
    try {
      const response = await eventsApi.getAllEvents(0, 100);
      setEvents(response.content);
    } catch (error) {
      toast.error("Erro ao carregar eventos");
      console.error("Error loading events:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const loadMyEnrollments = async () => {
    try {
      const response = await enrollmentsApi.getMyEnrollments();
      // Only consider active enrollments (not canceled)
      const activeEnrollments = response.content.filter(
        (e) => e.status !== "CANCELED"
      );
      const eventIds = new Set(activeEnrollments.map((e) => e.eventId));
      setEnrolledEventIds(eventIds);
    } catch (error) {
      console.error("Error loading enrollments:", error);
    }
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      loadEvents();
      return;
    }

    setIsLoading(true);
    try {
      const response = await eventsApi.searchEvents(searchQuery);
      setEvents(response.content);
    } catch (error) {
      toast.error("Erro ao buscar eventos");
      console.error("Error searching events:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleEnroll = async (event: Event) => {
    try {
      await enrollmentsApi.createEnrollment({ eventId: event.id });
      toast.success(
        "Inscrição realizada com sucesso! Aguarde a confirmação do organizador."
      );
      setEnrolledEventIds((prev) => new Set(prev).add(event.id));
      // Reload enrollments to update the UI
      await loadMyEnrollments();
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : "Erro ao realizar inscrição";
      toast.error(errorMessage);
      console.error("Error enrolling:", error);
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
      <div className="mb-8">
        <h1 className="text-gray-900 mb-2">Eventos Disponíveis</h1>
        <p className="text-gray-600">
          Explore e inscreva-se nos eventos que interessam você
        </p>
      </div>

      <div className="mb-8">
        <div className="flex gap-4">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onKeyPress={(e) => e.key === "Enter" && handleSearch()}
              className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-600 focus:border-transparent"
              placeholder="Buscar eventos por título ou localização..."
            />
          </div>
          <button
            onClick={handleSearch}
            className="bg-blue-600 text-white px-6 py-3 rounded-md hover:bg-blue-700 transition-colors"
          >
            Buscar
          </button>
        </div>
      </div>

      {events.length === 0 ? (
        <div className="text-center py-12">
          <Calendar className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <h3 className="text-gray-600 mb-2">Nenhum evento disponível</h3>
          <p className="text-gray-500">Novos eventos serão exibidos aqui</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {events.map((event) => (
            <EventCard
              key={event.id}
              event={event}
              onAction={
                enrolledEventIds.has(event.id)
                  ? () => navigate("/my-enrollments")
                  : handleEnroll
              }
              actionLabel={
                enrolledEventIds.has(event.id)
                  ? "Ver Inscrição"
                  : "Inscrever-se"
              }
              actionDisabled={false}
              allowActionWhenFull={enrolledEventIds.has(event.id)}
              showOrganizer={true}
            />
          ))}
        </div>
      )}
    </div>
  );
};
