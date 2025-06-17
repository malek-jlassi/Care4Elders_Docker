import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { loadStripe, Stripe, StripeElements } from '@stripe/stripe-js';
import { Observable, firstValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StripeService {
  private apiUrl = 'http://localhost:8080/api/payment';
  private stripePromise = loadStripe('pk_test_51RUsH7Q5Dk9ryeKqi0nWPdvYyNAnEkDWOGqMQCW9gGRPC5EtfB3u3FrBrOuVZDuaWcubp1OlWsWoyB1ooboRqclP00QyTTwUJh');
  private stripe: Stripe | null = null;
  private elements: StripeElements | null = null;
  private cardElement: any = null;

  constructor(private http: HttpClient) {
    this.initializeStripe();
  }

  private async initializeStripe() {
    try {
      this.stripe = await this.stripePromise;
      if (this.stripe) {
        this.elements = this.stripe.elements();
      } else {
        console.error('Failed to initialize Stripe');
      }
    } catch (error) {
      console.error('Error initializing Stripe:', error);
    }
  }

  getCardElement() {
    return this.cardElement;
  }

  getStripe() {
    return this.stripe;
  }

  createPaymentIntent(amount: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/create-payment-intent`, { amount });
  }

  confirmPayment(paymentIntentId: string, billId: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/confirm-payment`, { paymentIntentId, billId });
  }

  async mountCardElement(elementId: string) {
    try {
      if (!this.stripe || !this.elements) {
        await this.initializeStripe();
      }

      if (this.elements) {
        if (this.cardElement) {
          this.cardElement.unmount();
        }
        
        this.cardElement = this.elements.create('card', {
          style: {
            base: {
              color: '#32325d',
              fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
              fontSmoothing: 'antialiased',
              fontSize: '16px',
              '::placeholder': {
                color: '#aab7c4'
              }
            },
            invalid: {
              color: '#fa755a',
              iconColor: '#fa755a'
            }
          }
        });

        const element = document.getElementById(elementId);
        if (element) {
          this.cardElement.mount(`#${elementId}`);
          console.log('Card element mounted successfully');
        } else {
          console.error(`Element with id ${elementId} not found`);
        }
      } else {
        console.error('Stripe Elements not initialized');
      }
    } catch (error) {
      console.error('Error mounting card element:', error);
    }
  }

  unmountCardElement() {
    if (this.cardElement) {
      this.cardElement.unmount();
      this.cardElement = null;
    }
  }

  async processPayment(amount: number, deliveryBillId: string): Promise<any> {
    try {
      if (!this.stripe || !this.cardElement) {
        throw new Error('Stripe not initialized');
      }

      const paymentIntentResponse = await firstValueFrom(this.createPaymentIntent(amount));
      
      if (!paymentIntentResponse || !paymentIntentResponse.clientSecret) {
        throw new Error('Failed to create payment intent');
      }

      const result = await this.stripe.confirmCardPayment(paymentIntentResponse.clientSecret, {
        payment_method: {
          card: this.cardElement,
          billing_details: {
            name: 'Client Care4Elders'
          }
        }
      });

      if (result.error) {
        throw result.error;
      }

      return result.paymentIntent;
    } catch (error) {
      console.error('Payment failed:', error);
      throw error;
    }
  }
} 