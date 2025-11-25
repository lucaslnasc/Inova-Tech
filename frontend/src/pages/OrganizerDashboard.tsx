import React, { useState } from 'react';
import { Plus } from 'lucide-react';
import { CreateEventModal } from '../components/CreateEventModal';

export const OrganizerDashboard: React.FC = () => {
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

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

      <div className="bg-white rounded-lg shadow p-8 text-center">
        <h3 className="text-gray-600 mb-2">Bem-vindo ao seu painel!</h3>
        <p className="text-gray-500 mb-4">
          Crie um novo evento ou acesse "Meus Eventos" para gerenciar seus eventos existentes
        </p>
      </div>

      <CreateEventModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
      />
    </div>
  );
};
