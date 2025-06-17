export interface Appointment {
    id: string;                              // Unique identifier
    patientId: string;                       // Reference to patient
    doctorId: string;                        // Reference to doctor
    date: string;                            // ISO date: '2025-04-10T10:30:00Z'
    durationMinutes: number;                // Duration of the appointment
    type: 'in_person' | 'teleconsultation';  // Mode of consultation
    status: 'pending' | 'confirmed' | 'cancelled' | 'completed';
  
    reason?: string;                         // Reason for the visit (symptoms, concern)
    notes?: string;                          // Doctor's notes after the consultation
    prescriptionId?: string;                // Link to a prescription (if any)
  
    createdAt: string;
    updatedAt?: string;
  }
  