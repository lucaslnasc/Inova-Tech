import { Lock, Mail, Save, User } from "lucide-react";
import React, { useState } from "react";
import { toast } from "sonner@2.0.3";
import { useAuth } from "../contexts/AuthContext";
import { authApi } from "../services/api";

export const Profile: React.FC = () => {
  const { user, updateUser } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [formData, setFormData] = useState({
    name: user?.name || "",
    email: user?.email || "",
    currentPassword: "",
    newPassword: "",
    confirmNewPassword: "",
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!user) return;

    // Validate password change if attempting to change
    if (formData.newPassword) {
      if (!formData.currentPassword) {
        toast.error("Digite sua senha atual para alterá-la");
        return;
      }

      if (formData.newPassword !== formData.confirmNewPassword) {
        toast.error("As novas senhas não coincidem");
        return;
      }

      if (formData.newPassword.length < 6) {
        toast.error("A nova senha deve ter pelo menos 6 caracteres");
        return;
      }

      // Validate current password
      try {
        const isValid = await authApi.validatePassword(
          user.id,
          formData.currentPassword
        );
        if (!isValid) {
          toast.error("Senha atual incorreta");
          return;
        }
      } catch (error) {
        toast.error("Erro ao validar senha atual");
        return;
      }
    }

    setIsLoading(true);
    try {
      const updateData: any = {};

      if (formData.name !== user.name) {
        updateData.name = formData.name;
      }

      if (formData.email !== user.email) {
        updateData.email = formData.email;
      }

      if (formData.newPassword) {
        updateData.password = formData.newPassword;
      }

      if (Object.keys(updateData).length === 0) {
        toast.info("Nenhuma alteração foi feita");
        setIsEditing(false);
        return;
      }

      const updatedUser = await authApi.updateUser(user.id, updateData);
      updateUser(updatedUser);

      toast.success("Perfil atualizado com sucesso!");
      setIsEditing(false);
      setFormData({
        ...formData,
        currentPassword: "",
        newPassword: "",
        confirmNewPassword: "",
      });
    } catch (error) {
      toast.error("Erro ao atualizar perfil");
      console.error("Error updating profile:", error);
    } finally {
      setIsLoading(false);
    }
  };

  if (!user) return null;

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="bg-white rounded-lg shadow-md p-8">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-gray-900">Meu Perfil</h1>
          {!isEditing && (
            <button
              onClick={() => setIsEditing(true)}
              className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors"
            >
              Editar Perfil
            </button>
          )}
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label htmlFor="name" className="block text-gray-700 mb-2">
              Nome Completo
            </label>
            <div className="relative">
              <User className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                id="name"
                type="text"
                value={formData.name}
                onChange={(e) =>
                  setFormData({ ...formData, name: e.target.value })
                }
                disabled={!isEditing}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-600 focus:border-transparent disabled:bg-gray-100"
                required
              />
            </div>
          </div>

          <div>
            <label htmlFor="email" className="block text-gray-700 mb-2">
              Email
            </label>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                id="email"
                type="email"
                value={formData.email}
                onChange={(e) =>
                  setFormData({ ...formData, email: e.target.value })
                }
                disabled={!isEditing}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-600 focus:border-transparent disabled:bg-gray-100"
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-gray-700 mb-2">Tipo de Conta</label>
            <input
              type="text"
              value={
                user.type === "ORGANIZADOR" ? "Organizador" : "Participante"
              }
              disabled
              className="w-full px-4 py-2 border border-gray-300 rounded-md bg-gray-100"
            />
          </div>

          {isEditing && (
            <>
              <div className="border-t pt-6">
                <h3 className="text-gray-900 mb-4">Alterar Senha</h3>
                <p className="text-sm text-gray-600 mb-4">
                  Deixe em branco se não deseja alterar a senha
                </p>

                <div className="space-y-4">
                  <div>
                    <label
                      htmlFor="currentPassword"
                      className="block text-gray-700 mb-2"
                    >
                      Senha Atual
                    </label>
                    <div className="relative">
                      <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                      <input
                        id="currentPassword"
                        type="password"
                        value={formData.currentPassword}
                        onChange={(e) =>
                          setFormData({
                            ...formData,
                            currentPassword: e.target.value,
                          })
                        }
                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-600 focus:border-transparent"
                        placeholder="Digite sua senha atual"
                      />
                    </div>
                  </div>

                  <div>
                    <label
                      htmlFor="newPassword"
                      className="block text-gray-700 mb-2"
                    >
                      Nova Senha
                    </label>
                    <div className="relative">
                      <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                      <input
                        id="newPassword"
                        type="password"
                        value={formData.newPassword}
                        onChange={(e) =>
                          setFormData({
                            ...formData,
                            newPassword: e.target.value,
                          })
                        }
                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-600 focus:border-transparent"
                        placeholder="Digite a nova senha"
                      />
                    </div>
                  </div>

                  <div>
                    <label
                      htmlFor="confirmNewPassword"
                      className="block text-gray-700 mb-2"
                    >
                      Confirmar Nova Senha
                    </label>
                    <div className="relative">
                      <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                      <input
                        id="confirmNewPassword"
                        type="password"
                        value={formData.confirmNewPassword}
                        onChange={(e) =>
                          setFormData({
                            ...formData,
                            confirmNewPassword: e.target.value,
                          })
                        }
                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-600 focus:border-transparent"
                        placeholder="Confirme a nova senha"
                      />
                    </div>
                  </div>
                </div>
              </div>

              <div className="flex gap-4 pt-4">
                <button
                  type="button"
                  onClick={() => {
                    setIsEditing(false);
                    setFormData({
                      name: user.name,
                      email: user.email,
                      currentPassword: "",
                      newPassword: "",
                      confirmNewPassword: "",
                    });
                  }}
                  className="flex-1 bg-gray-200 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-300 transition-colors"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={isLoading}
                  className="flex-1 flex items-center justify-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
                >
                  <Save className="w-4 h-4" />
                  {isLoading ? "Salvando..." : "Salvar Alterações"}
                </button>
              </div>
            </>
          )}
        </form>
      </div>
    </div>
  );
};
