import { X } from "lucide-react";
import React, { useEffect, useState } from "react";
import { toast } from "sonner@2.0.3";
import { eventsApi } from "../services/api";
import { Event, UpdateEventRequest } from "../types";

interface EditEventModalProps {
  event: Event;
  isOpen: boolean;
  onClose: () => void;
  onSuccess?: () => void;
}

export const EditEventModal: React.FC<EditEventModalProps> = ({
  event,
  isOpen,
  onClose,
  onSuccess,
}) => {
  const [formData, setFormData] = useState<UpdateEventRequest>({});
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (event) {
      // Convert ISO date to datetime-local format
      const startDateForInput = event.startDateTime
        ? event.startDateTime.slice(0, 16)
        : "";
      const endDateForInput = event.endDateTime
        ? event.endDateTime.slice(0, 16)
        : "";

      setFormData({
        title: event.title,
        description: event.description,
        startDateTime: startDateForInput,
        endDateTime: endDateForInput,
        location: event.location,
        capacity: event.capacity,
      });
    }
  }, [event]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (
      !formData.title ||
      !formData.description ||
      !formData.startDateTime ||
      !formData.endDateTime ||
      !formData.location
    ) {
      toast.error("Por favor, preencha todos os campos");
      return;
    }

    if (formData.capacity && formData.capacity < 1) {
      toast.error("O número de participantes deve ser no mínimo 1");
      return;
    }

    // Validate that start date is before end date
    if (new Date(formData.startDateTime) >= new Date(formData.endDateTime)) {
      toast.error("A data de início deve ser anterior à data de término");
      return;
    }

    setIsLoading(true);
    try {
      await eventsApi.updateEvent(event.id, formData);
      toast.success("Evento atualizado com sucesso!");
      onClose();
      if (onSuccess) onSuccess();
    } catch (error) {
      toast.error("Erro ao atualizar evento");
      console.error("Error updating event:", error);
    } finally {
      setIsLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div className="flex justify-between items-center p-6 border-b">
          <h2 className="text-gray-900">Editar Evento</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div>
            <label htmlFor="edit-title" className="block text-gray-700 mb-2">
              Título do Evento *
            </label>
            <input
              id="edit-title"
              type="text"
              value={formData.title || ""}
              onChange={(e) =>
                setFormData({ ...formData, title: e.target.value })
              }
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-600 focus:border-transparent"
              placeholder="Ex: Workshop de React"
              required
            />
          </div>

          <div>
            <label
              htmlFor="edit-description"
              className="block text-gray-700 mb-2"
            >
              Descrição *
            </label>
            <textarea
              id="edit-description"
              value={formData.description || ""}
              onChange={(e) =>
                setFormData({ ...formData, description: e.target.value })
              }
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-600 focus:border-transparent"
              rows={4}
              placeholder="Descreva seu evento..."
              required
            />
          </div>

          <div>
            <label
              htmlFor="edit-startDate"
              className="block text-gray-700 mb-2"
            >
              Data e Hora de Início *
            </label>
            <input
              id="edit-startDate"
              type="datetime-local"
              value={formData.startDateTime || ""}
              onChange={(e) =>
                setFormData({ ...formData, startDateTime: e.target.value })
              }
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-600 focus:border-transparent"
              required
            />
          </div>

          <div>
            <label htmlFor="edit-endDate" className="block text-gray-700 mb-2">
              Data e Hora de Término *
            </label>
            <input
              id="edit-endDate"
              type="datetime-local"
              value={formData.endDateTime || ""}
              onChange={(e) =>
                setFormData({ ...formData, endDateTime: e.target.value })
              }
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-600 focus:border-transparent"
              required
            />
          </div>

          <div>
            <label htmlFor="edit-location" className="block text-gray-700 mb-2">
              Localização *
            </label>
            <input
              id="edit-location"
              type="text"
              value={formData.location || ""}
              onChange={(e) =>
                setFormData({ ...formData, location: e.target.value })
              }
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-600 focus:border-transparent"
              placeholder="Ex: Auditório Principal, São Paulo"
              required
            />
          </div>

          <div>
            <label
              htmlFor="edit-maxParticipants"
              className="block text-gray-700 mb-2"
            >
              Número Máximo de Participantes *
            </label>
            <input
              id="edit-maxParticipants"
              type="number"
              min="1"
              value={formData.capacity || 10}
              onChange={(e) =>
                setFormData({ ...formData, capacity: parseInt(e.target.value) })
              }
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-600 focus:border-transparent"
              required
            />
          </div>

          <div className="flex gap-4 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 bg-gray-200 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-300 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isLoading}
              className="flex-1 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
            >
              {isLoading ? "Salvando..." : "Salvar Alterações"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
