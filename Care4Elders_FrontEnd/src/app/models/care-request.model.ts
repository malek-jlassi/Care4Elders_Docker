// src/app/models/care-request.model.ts
export interface PatientInfo {
  id: string;
  username: string;
  //lastName: string;
  adressePatient: string;
  phoneNumber: number;
  email: string;
}

export interface CareRequest {
  id?: string;
  patient: PatientInfo; // Now an object, not a string
  maladiesChroniques: string[];
  careType: string;
  description: string;
  startDate: string;
  endDate: string;
  status: string;
  dateCreation?: string;
  dateUpdate?: string;
  facture?: Facture;
}

export interface Facture {
  id: string;
  careRequestId: string;
  patientId: string;
  description: string;
  amount: number;
  dateIssued: string;
  paid: boolean;
}