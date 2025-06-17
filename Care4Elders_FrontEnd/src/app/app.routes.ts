// import { Routes } from '@angular/router';
// import { HomeComponent } from './frontoffice/layout/home/home.component';


// export const routes: Routes = [


//   {
//     path: 'frontoffice/home',
//     component: HomeComponent
//   },
  



//     // Redirect empty path to appointments
//     // {
//     //   path: '',
//     //   redirectTo: 'appointments',
//     //   pathMatch: 'full'
//     // },
//     // Appointments module
//     {
//       path: 'appointments',
//       loadChildren: () =>
//         import('./frontoffice/appointment/appointment.module').then(
//           m => m.AppointmentModule
//         )
//     },
//     // Lazy-loaded example of another module
//     // {
//     //   path: 'patients',
//     //   loadChildren: () =>
//     //     import('./frontoffice/patient/patient.module').then(
//     //       m => m.PatientModule
//     //     )
//     // },
//     // Catch-all route for 404 errors
//     {
//       path: '**',
//       redirectTo: ''
//     }
//   ];


import { Routes } from '@angular/router';
import { HomeComponent } from './frontoffice/layout/home/home.component';

//import { HomeComponent } from './frontoffice/home/home.component'; // Make sure you have this
export const routes: Routes = [
  // {
  //   path: '',
  //   redirectTo: 'frontoffice/home',
  //   pathMatch: 'full'
  // },
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'appointments',
    loadChildren: () =>
      import('./frontoffice/pages/appointment/appointment.module').then(
        (m) => m.AppointmentModule
      )
  },  

   {
    path: 'ListeProduit',
    loadComponent: () =>
      import('./frontoffice/pages/afficher-liste-produit/afficher-liste-produit.component').then(
        (m) => m.AfficherListeProduitComponent
      )
  },  

   {
        path: 'utilisateur',
        loadChildren: () =>
          import('./frontoffice/Utilisateur/Utilisateur.module').then(
            m => m.UtilisateurModule
          )
      },

  {
    path: 'care',
    loadComponent: () =>
      import('./frontoffice/pages/care/care-request-page/care-request-page.component').then(m => m.CareRequestPageComponent)
  },
  {
    path: 'factures/global',
    loadComponent: () => import('./frontoffice/pages/facture/facture-global.component').then(m => m.FactureGlobalComponent)
  },
  {
    path: 'facture/:id',
    loadComponent: () =>
      import('./frontoffice/pages/care/facture-detail/facture-detail.component').then(m => m.FactureDetailComponent)
  },
  {
    path: 'delivery/bill',
    loadComponent: () =>
      import('./frontoffice/pages/delivery-bill/delivery-bill.component').then(
        (m) => m.DeliveryBillComponent
      )
  },
  {
    path: 'payment',
    loadComponent: () =>
      import('./frontoffice/pages/payment/payment.component').then(
        (m) => m.PaymentComponent
      )
  },
  {
    path: 'payment/success',
    loadComponent: () =>
      import('./frontoffice/pages/payment/payment-success.component').then(
        (m) => m.PaymentSuccessComponent
      )
  },
  {
    path: 'teleconsultations',
    loadChildren: () =>
      import('./frontoffice/pages/teleconsultation/teleconsultation.module').then(
        m => m.TeleconsultationModule
      )
  },
  {
    path: '**',
    redirectTo: 'frontoffice/home'
  },
  {
    path: '**',
    redirectTo: ''
  }
];
 

  

