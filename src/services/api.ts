import {
  CreateEnrollmentRequest,
  CreateEventRequest,
  CreateUserRequest,
  Enrollment,
  Event,
  LoginRequest,
  LoginResponse,
  PageResponse,
  UpdateEventRequest,
  UpdateUserRequest,
  User,
  UserType,
} from '../types';

const API_BASE_URL = 'http://localhost:8080';

// Helper to get auth headers
const getAuthHeaders = (): HeadersInit => {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    ...(token && { Authorization: `Bearer ${token}` }),
  };
};

// Helper to handle API responses
async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const error = await response.text();
    throw new Error(error || `HTTP error! status: ${response.status}`);
  }
  return response.json();
}

// AUTH & USERS
export const authApi = {
  login: async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await fetch(`${API_BASE_URL}/api/users/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    const result = await handleResponse<{ data: LoginResponse }>(response);
    return result.data;
  },

  createUser: async (data: CreateUserRequest): Promise<User> => {
    const response = await fetch(`${API_BASE_URL}/api/users`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    const result = await handleResponse<{ data: User }>(response);
    return result.data;
  },

  getUserById: async (id: string): Promise<User> => {
    const response = await fetch(`${API_BASE_URL}/api/users/${id}`, {
      headers: getAuthHeaders(),
    });
    const result = await handleResponse<{ data: User }>(response);
    return result.data;
  },

  getUserByEmail: async (email: string): Promise<User> => {
    const response = await fetch(`${API_BASE_URL}/api/users/email/${email}`, {
      headers: getAuthHeaders(),
    });
    const result = await handleResponse<{ data: User }>(response);
    return result.data;
  },

  getAllUsers: async (): Promise<User[]> => {
    const response = await fetch(`${API_BASE_URL}/api/users`, {
      headers: getAuthHeaders(),
    });
    const result = await handleResponse<{ data: User[] }>(response);
    return result.data;
  },

  updateUser: async (id: string, data: UpdateUserRequest): Promise<User> => {
    const response = await fetch(`${API_BASE_URL}/api/users/${id}`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(data),
    });
    const result = await handleResponse<{ data: User }>(response);
    return result.data;
  },

  deleteUser: async (id: string): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/api/users/${id}`, {
      method: 'DELETE',
      headers: getAuthHeaders(),
    });
    if (!response.ok) {
      throw new Error('Failed to delete user');
    }
  },

  getUsersByType: async (userType: UserType): Promise<User[]> => {
    const response = await fetch(`${API_BASE_URL}/api/users/type/${userType}`, {
      headers: getAuthHeaders(),
    });
    const result = await handleResponse<{ data: User[] }>(response);
    return result.data;
  },

  searchUsersByName: async (name: string): Promise<User[]> => {
    const response = await fetch(`${API_BASE_URL}/api/users/search?name=${encodeURIComponent(name)}`, {
      headers: getAuthHeaders(),
    });
    const result = await handleResponse<{ data: User[] }>(response);
    return result.data;
  },

  checkEmailAvailability: async (email: string): Promise<boolean> => {
    const response = await fetch(`${API_BASE_URL}/api/users/check-email?email=${encodeURIComponent(email)}`, {
      headers: getAuthHeaders(),
    });
    const result = await handleResponse<{ data: boolean }>(response);
    return result.data;
  },

  validatePassword: async (userId: string, password: string): Promise<boolean> => {
    const response = await fetch(`${API_BASE_URL}/api/users/${userId}/validate-password?password=${encodeURIComponent(password)}`, {
      method: 'POST',
      headers: getAuthHeaders(),
    });
    const result = await handleResponse<{ data: boolean }>(response);
    return result.data;
  },
};

// EVENTS
export const eventsApi = {
  createEvent: async (data: CreateEventRequest): Promise<Event> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/events`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(data),
    });
    const result = await handleResponse<{ data: Event }>(response);
    return result.data;
  },

  getAllEvents: async (page = 0, size = 10): Promise<PageResponse<Event>> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/events?page=${page}&size=${size}`, {
      headers: getAuthHeaders(),
    });
    const result = await handleResponse<{ data: PageResponse<Event> }>(response);
    return result.data;
  },

  getEventById: async (id: string): Promise<Event> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/events/${id}`, {
      headers: getAuthHeaders(),
    });
    const result = await handleResponse<{ data: Event }>(response);
    return result.data;
  },

  updateEvent: async (id: string, data: UpdateEventRequest): Promise<Event> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/events/${id}`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(data),
    });
    const result = await handleResponse<{ data: Event }>(response);
    return result.data;
  },

  deleteEvent: async (id: string): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/events/${id}`, {
      method: 'DELETE',
      headers: getAuthHeaders(),
    });
    if (!response.ok) {
      throw new Error('Failed to delete event');
    }
  },

  searchEvents: async (query: string, page = 0, size = 10): Promise<PageResponse<Event>> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/events/search?query=${encodeURIComponent(query)}&page=${page}&size=${size}`, {
      headers: getAuthHeaders(),
    });
    const result = await handleResponse<{ data: PageResponse<Event> }>(response);
    return result.data;
  },

  getMyEvents: async (page = 0, size = 10): Promise<PageResponse<Event>> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/events/my-events?page=${page}&size=${size}`, {
      headers: getAuthHeaders(),
    });
    const result = await handleResponse<{ data: PageResponse<Event> }>(response);
    return result.data;
  },
};

// ENROLLMENTS
export const enrollmentsApi = {
  createEnrollment: async (data: CreateEnrollmentRequest): Promise<Enrollment> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/enrollments`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(data),
    });
    const result = await handleResponse<{ data: Enrollment }>(response);
    return result.data;
  },

  getMyEnrollments: async (page = 0, size = 10): Promise<PageResponse<Enrollment>> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/enrollments/my-enrollments?page=${page}&size=${size}`, {
      headers: getAuthHeaders(),
    });
    const result = await handleResponse<{ data: PageResponse<Enrollment> }>(response);
    return result.data;
  },

  getEventEnrollments: async (eventId: string, page = 0, size = 10): Promise<PageResponse<Enrollment>> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/enrollments/event/${eventId}?page=${page}&size=${size}`, {
      headers: getAuthHeaders(),
    });
    const result = await handleResponse<{ data: PageResponse<Enrollment> }>(response);
    return result.data;
  },

  cancelEnrollment: async (enrollmentId: string): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/enrollments/${enrollmentId}`, {
      method: 'DELETE',
      headers: getAuthHeaders(),
    });
    if (!response.ok) {
      throw new Error('Failed to cancel enrollment');
    }
  },

  confirmEnrollment: async (enrollmentId: string): Promise<Enrollment> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/enrollments/${enrollmentId}/confirm`, {
      method: 'PUT',
      headers: getAuthHeaders(),
    });
    const result = await handleResponse<{ data: Enrollment }>(response);
    return result.data;
  },

  rejectEnrollment: async (enrollmentId: string): Promise<Enrollment> => {
    const response = await fetch(`${API_BASE_URL}/api/v1/enrollments/${enrollmentId}/reject`, {
      method: 'PUT',
      headers: getAuthHeaders(),
    });
    const result = await handleResponse<{ data: Enrollment }>(response);
    return result.data;
  },
};
