// Enums
export enum UserType {
  ORGANIZADOR = 'ORGANIZADOR',
  PARTICIPANTE = 'PARTICIPANTE'
}

export enum EnrollmentStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELED = 'CANCELED'
}

// User
export interface User {
  id: string;
  name: string;
  email: string;
  type: UserType;
  createdAt?: string;
}

export interface CreateUserRequest {
  name: string;
  email: string;
  password: string;
  type: UserType;
}

export interface UpdateUserRequest {
  name?: string;
  email?: string;
  password?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}

// Event
export interface Event {
  id: string;
  title: string;
  description: string;
  startDateTime: string;
  endDateTime: string;
  location: string;
  capacity: number;
  organizerId: string;
  organizerName?: string;
  currentEnrollments?: number;
  createdAt?: string;
}

export interface CreateEventRequest {
  title: string;
  description: string;
  startDateTime: string;
  endDateTime: string;
  location: string;
  capacity: number;
}

export interface UpdateEventRequest {
  title?: string;
  description?: string;
  startDateTime?: string;
  endDateTime?: string;
  location?: string;
  capacity?: number;
}

// Enrollment
export interface Enrollment {
  id: string;
  eventId: string;
  eventTitle?: string;
  eventStartDateTime?: string;
  eventLocation?: string;
  participantId: string;
  status: EnrollmentStatus;
  enrollmentDate: string;
  event?: Event; // Mantendo opcional para compatibilidade se necessário, mas o backend manda flattened
  participantName?: string;
  participantEmail?: string;
}

export interface CreateEnrollmentRequest {
  eventId: string;
}

// Pagination
export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

// API Response
export interface ApiResponse<T> {
  data?: T;
  message?: string;
  error?: string;
}
