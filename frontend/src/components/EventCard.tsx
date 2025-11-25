import { format } from "date-fns";
import { ptBR } from "date-fns/locale";
import { Calendar, MapPin, User, Users } from "lucide-react";
import React from "react";
import { Event } from "../types";

interface EventCardProps {
  event: Event;
  onAction?: (event: Event) => void;
  actionLabel?: string;
  actionDisabled?: boolean;
  showOrganizer?: boolean;
  allowActionWhenFull?: boolean;
}

export const EventCard: React.FC<EventCardProps> = ({
  event,
  onAction,
  actionLabel,
  actionDisabled,
  showOrganizer = false,
  allowActionWhenFull = false,
}) => {
  const currentEnrollments = event.currentEnrollments || 0;
  const isFull = currentEnrollments >= event.capacity;

  const formatEventDate = (dateString: string) => {
    try {
      const date = new Date(dateString);
      return format(date, "dd 'de' MMMM 'de' yyyy 'às' HH:mm", {
        locale: ptBR,
      });
    } catch {
      return dateString;
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow p-6 border border-gray-200">
      <h3 className="text-blue-600 mb-2">{event.title}</h3>

      <p className="text-gray-600 mb-4 line-clamp-2">{event.description}</p>

      <div className="space-y-2 mb-4">
        <div className="flex items-center gap-2 text-gray-700">
          <Calendar className="w-4 h-4 text-blue-600" />
          <span className="text-sm">
            {formatEventDate(event.startDateTime)}
          </span>
        </div>

        <div className="flex items-center gap-2 text-gray-700">
          <MapPin className="w-4 h-4 text-blue-600" />
          <span className="text-sm">{event.location}</span>
        </div>

        <div className="flex items-center gap-2 text-gray-700">
          <Users className="w-4 h-4 text-blue-600" />
          <span className="text-sm">
            {currentEnrollments} / {event.capacity} participantes
          </span>
          {isFull && (
            <span className="text-xs bg-red-100 text-red-700 px-2 py-1 rounded">
              Lotado
            </span>
          )}
        </div>

        {showOrganizer && event.organizerName && (
          <div className="flex items-center gap-2 text-gray-700">
            <User className="w-4 h-4 text-blue-600" />
            <span className="text-sm">{event.organizerName}</span>
          </div>
        )}
      </div>

      {onAction && actionLabel && (
        <button
          onClick={() => onAction(event)}
          disabled={actionDisabled || (isFull && !allowActionWhenFull)}
          className="w-full bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
        >
          {actionLabel}
        </button>
      )}
    </div>
  );
};
