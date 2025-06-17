import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { CareRequest } from '../../../../models/care-request.model';
import { Facture } from '../../../../models/facture.model';
import { BillingService } from '../../../../services/facture.service';
import { CareRequestService } from '../../../../services/care-request.service';
import { StripeService } from '../../../../services/stripe.service';
import { TopbarComponent } from '../../../../shared/layout/topbar/topbar.component';
import { NavbarComponent } from '../../../../shared/layout/navbar/navbar.component';
import { FooterComponent } from '../../../../shared/layout/footer/footer.component';

@Component({
  selector: 'app-facture-detail',
  standalone: true,
  imports: [CommonModule, TopbarComponent, NavbarComponent, FooterComponent],
  templateUrl: './facture-detail.component.html',
  styleUrls: ['./facture-detail.component.css']
})
export class FactureDetailComponent implements OnInit {
  facture: Facture | null = null;
  careRequest: CareRequest | null = null;
  loading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private billingService: BillingService,
    private careRequestService: CareRequestService,
    private stripeService: StripeService
  ) {}

  ngOnInit(): void {
    const factureId = this.route.snapshot.paramMap.get('id');
    if (factureId) {
      this.loadFactureDetails(factureId);
    } else {
      this.error = 'ID de facture non trouvé';
      this.loading = false;
    }
  }

  private loadFactureDetails(factureId: string): void {
    this.billingService.getFactureById(factureId).subscribe({
      next: (response: any) => {
        if (this.isValidFacture(response)) {
          this.facture = response;
          if (response.careRequestId) {
            this.loadCareRequest(response.careRequestId);
          } else {
            this.loading = false;
            this.error = 'Demande de soin non trouvée';
          }
        } else {
          this.error = 'Format de facture invalide';
          this.loading = false;
        }
      },
      error: (error: unknown) => {
        console.error('Erreur lors du chargement de la facture:', error);
        this.error = 'Erreur lors du chargement des détails de la facture';
        this.loading = false;
      }
    });
  }

  private isValidFacture(facture: any): facture is Facture {
    return (
      facture &&
      typeof facture._id === 'string' &&
      typeof facture.careRequestId === 'string' &&
      typeof facture.patientId === 'string' &&
      typeof facture.amount === 'number' &&
      typeof facture.paid === 'boolean' &&
      facture.dateCreation instanceof Date &&
      (facture.status === 'PENDING' || facture.status === 'PAID' || facture.status === 'CANCELLED')
    );
  }

  private loadCareRequest(careRequestId: string): void {
    this.careRequestService.getById(careRequestId).subscribe({
      next: (careRequest) => {
        this.careRequest = careRequest;
        this.loading = false;
      },
      error: (error: unknown) => {
        console.error('Erreur lors du chargement de la demande de soin:', error);
        this.error = 'Erreur lors du chargement des détails de la demande de soin';
        this.loading = false;
      }
    });
  }

  proceedToPayment(): void {
    if (this.facture) {
      this.router.navigate(['/payment', this.facture._id]);
    }
  }
} 