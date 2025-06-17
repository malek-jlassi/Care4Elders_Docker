export interface Facture {
  _id: string;
  careRequestId: string;
  patientId: string;
  amount: number;
  paid: boolean;
  dateCreation: Date;
  paymentDate?: Date;
  status: 'PENDING' | 'PAID' | 'CANCELLED';
} 