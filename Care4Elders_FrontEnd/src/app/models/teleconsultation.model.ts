export interface Teleconsultation {
  id?: string;
  consultationDate: string;// format ISO 8601 ex: "2025-05-09T14:30:00"
  durationMinutes: number;
  status?: 'en_attente' | 'confirmee' | 'terminee' | 'annulee';
  patientId: string;
  doctorId: string;
  videoLink?: string;
  createdAt?: string;
  updatedAt?: string;
}
