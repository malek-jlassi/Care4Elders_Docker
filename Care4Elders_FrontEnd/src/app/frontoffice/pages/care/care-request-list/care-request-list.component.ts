import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { CareRequest, Facture } from '../../../../models/care-request.model';
import { Output, EventEmitter } from '@angular/core';
import { CareRequestService } from '../../../../services/care-request.service';
import { BillingService } from '../../../../services/facture.service';
import { FormsModule, NgModel } from '@angular/forms';
import { AuthService } from '../../../../services/Auth.service';
import { RouterModule } from '@angular/router';
import { UtilisateurService } from '../../../../services/utilisateur.service';

@Component({
  selector: 'app-care-request-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './care-request-list.component.html',
  styleUrl: './care-request-list.component.css'
})

export class CareRequestListComponent implements OnInit {
  requests: CareRequest[] = [];
factures: Facture[] = [];
showFactureModal: boolean = false;
unpaidFactures: Facture[] = [];
totalUnpaid: number = 0;
pageSizeOptions = [5, 10, 15];
pageSize = 5;
currentPage = 1;
//selectedStatus: string = 'ALL'; // Default to show all

  constructor(
    private careRequestService: CareRequestService,
    private billingService: BillingService,
    private authService: AuthService,
    private utilisateurService: UtilisateurService
  ) {}

  ngOnInit(): void {
    const user = this.utilisateurService.getUser();
    if (user && user.id) {
      this.loadData(user.id);
    } else {
      this.requests = [];
      this.factures = [];
      // Optionally, redirect to login or show a message
    }
  }

  loadData(patientId: string) {
    this.careRequestService.getCareRequestsByPatient(patientId).subscribe((requests: CareRequest[]) => {
      this.requests = requests;
      this.billingService.getFacturesByPatient(patientId).subscribe(factures => {
        this.factures = factures;
        this.updateUnpaidFactures();
        this.requests.forEach(req => {
          const matchedFacture = factures.find(f => f.careRequestId === req.id);
          if (matchedFacture) {
            req.facture = matchedFacture;
          }
        });
      });
    });
  }
  
  
  

  // Update the list of unpaid factures and total
  updateUnpaidFactures() {
    this.unpaidFactures = this.factures.filter(f => !f.paid);
    this.totalUnpaid = this.unpaidFactures.reduce((sum, f) => sum + (f.amount || 0), 0);
  }

  // Open the facture modal
  openFactureModal() {
    this.updateUnpaidFactures();
    this.showFactureModal = true;
  }

  // Close the facture modal
  closeFactureModal() {
    this.showFactureModal = false;
  }

  // Placeholder for Stripe payment
  payWithStripe() {
    // TODO: Integrate with Stripe payment for totalUnpaid
    alert('Paiement Stripe à implémenter pour un montant total de: ' + this.totalUnpaid + ' TND');
  }

  fetchRequests() {
    this.careRequestService.getAll().subscribe(data => {
      console.log('Liste mise à jour:', data); // <--- Log ici
      this.requests = data;
    });
  }
  

  refreshList() {
    this.authService.currentUser$.subscribe(user => {
      if (user?.id) {
        this.loadData(user.id);
      }
    });
  }
  


  @Output() editClicked = new EventEmitter<CareRequest>();

editRequest(request: CareRequest): void {
  this.editClicked.emit(request);
console.log('Edit request:', request);
    // You can open a modal with CareRequestFormComponent and pass the request
  }
  
  deleteRequest(id: string | undefined): void {
    if (!id) return;
  
    if (confirm('Êtes-vous sûr de vouloir supprimer cette demande ?')) {
      this.careRequestService.delete(id).subscribe({
        next: () => {
          alert('La demande a été supprimée avec succès.'); // ou snackbar
          this.refreshList();
        },
        error: err => console.error('Erreur lors de la suppression', err)
      });
    }
  }
  
  get filteredRequests() {
    if (this.selectedStatus === 'ALL') {
      return this.requests;
    }
    return this.requests.filter(req => req.status === this.selectedStatus);
  }
  


get filteredRequestsPaginated() {
  const filtered = this.filteredRequests; // already filtered by status
  const startIndex = (this.currentPage - 1) * this.pageSize;
  return filtered.slice(startIndex, startIndex + this.pageSize);
}

get totalPages() {
  return Math.ceil(this.filteredRequests.length / this.pageSize);
}

nextPage() {
  if (this.currentPage < this.totalPages) {
    this.currentPage++;
  }
}

previousPage() {
  if (this.currentPage > 1) {
    this.currentPage--;
  }
}

private _selectedStatus = 'ALL';

set selectedStatus(value: string) {
  this._selectedStatus = value;
  this.currentPage = 1;
}
get selectedStatus(): string {
  return this._selectedStatus;
}


}
