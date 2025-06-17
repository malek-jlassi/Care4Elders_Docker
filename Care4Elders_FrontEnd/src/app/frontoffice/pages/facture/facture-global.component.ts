import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BillingService } from '../../../services/facture.service';
import { Facture } from '../../../models/care-request.model';
import { AuthService } from '../../../services/Auth.service';

import { CommonModule, DatePipe } from '@angular/common';

@Component({
  selector: 'app-facture-global',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './facture-global.component.html',
  styleUrls: ['./facture-global.component.css'],
  providers: [DatePipe]
})
export class FactureGlobalComponent implements OnInit {
  unpaidFactures: Facture[] = [];
  selectedFactures: Set<string> = new Set();
  totalSelected: number = 0;
  totalAll: number = 0;
  loading: boolean = true;
  error: string | null = null;

  constructor(
    private billingService: BillingService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user && user.id) {
        this.billingService.getFacturesByPatient(user.id).subscribe({
          next: (factures) => {
            this.unpaidFactures = factures.filter(f => !f.paid);
            this.totalAll = this.unpaidFactures.reduce((sum, f) => sum + (f.amount || 0), 0);
            this.totalSelected = this.totalAll;
            this.selectedFactures = new Set(this.unpaidFactures.map(f => f.id));
            this.loading = false;
          },
          error: () => {
            this.error = 'Erreur lors du chargement des factures.';
            this.loading = false;
          }
        });
      } else {
        this.error = 'Utilisateur non connecté';
        this.loading = false;
      }
    });
  }

  toggleFacture(factureId: string, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    if (checked) {
      this.selectedFactures.add(factureId);
    } else {
      this.selectedFactures.delete(factureId);
    }
    this.updateTotalSelected();
  }

  updateTotalSelected() {
    this.totalSelected = this.unpaidFactures
      .filter(f => this.selectedFactures.has(f.id))
      .reduce((sum, f) => sum + (f.amount || 0), 0);
  }

  paySelected() {
    if (this.selectedFactures.size === 0) return;
    const selectedIds = Array.from(this.selectedFactures).join(',');
    this.router.navigate(['/payment'], {
      queryParams: {
        amount: this.totalSelected,
        billId: selectedIds
      }
    });
  }

  payAll() {
    if (this.unpaidFactures.length === 0) return;
    const allIds = this.unpaidFactures.map(f => f.id).join(',');
    this.router.navigate(['/payment'], {
      queryParams: {
        amount: this.totalAll,
        billId: allIds
      }
    });
  }
}
