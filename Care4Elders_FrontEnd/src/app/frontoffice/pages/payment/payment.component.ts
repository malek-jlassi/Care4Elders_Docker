import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StripeService } from '../../../services/stripe.service';
import { CommonModule } from '@angular/common';
import { TopbarComponent } from '../../../shared/layout/topbar/topbar.component';
import { NavbarComponent } from '../../../shared/layout/navbar/navbar.component';
import { FooterComponent } from '../../../shared/layout/footer/footer.component';

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [CommonModule, TopbarComponent, NavbarComponent, FooterComponent],
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css']
})
export class PaymentComponent implements OnInit, OnDestroy, AfterViewInit {
  amount: number = 0;
  deliveryBillId: string = '';
  isLoading: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private stripeService: StripeService
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.amount = parseFloat(params['amount']);
      this.deliveryBillId = params['billId'];
    });
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.stripeService.mountCardElement('card-element');
    }, 100);
  }

  ngOnDestroy() {
    this.stripeService.unmountCardElement();
  }

  async handleSubmit(event: Event) {
    event.preventDefault();
    this.isLoading = true;

    try {
      const result = await this.stripeService.processPayment(this.amount, this.deliveryBillId);
      
      this.router.navigate(['/payment/success'], {
        queryParams: {
          paymentId: result.id,
          billId: this.deliveryBillId
        }
      });
    } catch (error: any) {
      const errorElement = document.getElementById('card-errors');
      if (errorElement) {
        errorElement.textContent = error.message || 'Une erreur est survenue lors du paiement.';
      }
    } finally {
      this.isLoading = false;
    }
  }
} 