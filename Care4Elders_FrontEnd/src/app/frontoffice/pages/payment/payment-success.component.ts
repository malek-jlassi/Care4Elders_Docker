import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TopbarComponent } from '../../../shared/layout/topbar/topbar.component';
import { NavbarComponent } from '../../../shared/layout/navbar/navbar.component';
import { FooterComponent } from '../../../shared/layout/footer/footer.component';
import { DeliveryService } from '../../../services/delivery.service';
import { BillingService } from '../../../services/facture.service';
import { catchError, delay, retryWhen, take } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-payment-success',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    TopbarComponent,
    NavbarComponent,
    FooterComponent
  ],
  templateUrl: './payment-success.component.html',
  styleUrls: ['./payment-success.component.css']
})
export class PaymentSuccessComponent implements OnInit {
  billId: string = '';
  paymentId: string = '';
  isDownloading: boolean = false;
  errorMessage: string = '';

  isCareFacture = false;
  billIds: string[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private deliveryService: DeliveryService,
    private billingService: BillingService
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.billId = params['billId'];
      this.paymentId = params['paymentId'];
      // If billId contains a comma, treat as multiple care request factures
      if (this.billId && this.billId.includes(',')) {
        this.isCareFacture = true;
        this.billIds = this.billId.split(',');
      } else {
        this.isCareFacture = false;
        this.billIds = [this.billId];
      }
    });
  }

  downloadBill() {
    if (!this.billId) {
      this.errorMessage = 'ID de facture manquant';
      return;
    }
    this.isDownloading = true;
    this.errorMessage = '';

    if (this.isCareFacture) {
      // Download all care factures (for now, just download the first; can be enhanced to zip)
      const downloadOne = (id: string) => {
        this.billingService.getFacturePdf(id).subscribe({
          next: (blob: Blob | null) => {
            this.isDownloading = false;
            if (blob) {
              const url = window.URL.createObjectURL(blob);
              const link = document.createElement('a');
              link.href = url;
              link.download = `recu-facture-${id}.pdf`;
              document.body.appendChild(link);
              link.click();
              document.body.removeChild(link);
              window.URL.revokeObjectURL(url);
            }
          },
          error: (error) => {
            this.isDownloading = false;
            this.errorMessage = 'Erreur lors du téléchargement de la facture de soin.';
          }
        });
      };
      downloadOne(this.billIds[0]);
    } else {
      // Product delivery bill
      this.deliveryService.downloadDeliveryBillPdf(this.billId)
        .pipe(
          retryWhen(errors => 
            errors.pipe(
              delay(1000),
              take(3)
            )
          ),
          catchError(error => {
            console.error('Error downloading bill:', error);
            this.errorMessage = 'Erreur lors du téléchargement de la facture produit. Veuillez réessayer.';
            return of(null);
          })
        )
        .subscribe({
          next: (blob: Blob | null) => {
            this.isDownloading = false;
            if (blob) {
              const url = window.URL.createObjectURL(blob);
              const link = document.createElement('a');
              link.href = url;
              link.download = `recu-produit-${this.billId}.pdf`;
              document.body.appendChild(link);
              link.click();
              document.body.removeChild(link);
              window.URL.revokeObjectURL(url);
            }
          },
          error: (error) => {
            this.isDownloading = false;
            this.errorMessage = 'Erreur lors du téléchargement de la facture produit.';
          }
        });
    }
  }
}