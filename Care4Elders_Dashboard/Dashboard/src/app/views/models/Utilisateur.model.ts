export enum Role {
  ADMIN = 'ADMIN',
  PATIENT = 'PATIENT',
  AIDANT = 'AIDANT',
  MEDECIN = 'MEDECIN'
}

export interface Utilisateur {
  id?: string;             // Optionnel pour la création
  username: string;
  email: string;
  phoneNumber: string;
  dateOfBirth: string;     // Format ISO string (ex: '2025-04-24')
  password: string;
  role: Role;

  // Attributs spécifiques pour Patient
  adressePatient?: string;
  dossierMedicalPdf?: string;

  // Attributs spécifiques pour Médecin
  specialite?: string;
  numeroOrdre?: string;
  fichierDiplome?: string;

  // Attributs spécifiques pour Aidant
  adresseAidant?: string;
  cartedIentite?: string;
}



  