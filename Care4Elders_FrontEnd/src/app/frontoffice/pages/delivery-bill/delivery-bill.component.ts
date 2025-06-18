import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TopbarComponent } from '../../../shared/layout/topbar/topbar.component';
import { NavbarComponent } from '../../../shared/layout/navbar/navbar.component';
import { FooterComponent } from '../../../shared/layout/footer/footer.component';
import { DeliveryService } from '../../../services/delivery.service';
import { HttpClientModule } from '@angular/common/http';
import { AuthService } from '../../../services/Auth.service';

interface DeliveryInfo {
  fullName: string;
  phone: string;
  address: string;
  city: string;
  postalCode: string;
  specialInstructions: string;
}

interface ProductDetails {
  id: string;
  nomProduit: string;
  descriptionProduit: string;
  region: string;
  prix: number;
  qt: number;
  image: string;
}

@Component({
  selector: 'app-delivery-bill',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TopbarComponent,
    NavbarComponent,
    FooterComponent,
    HttpClientModule
  ],
  templateUrl: './delivery-bill.component.html',
  styleUrls: ['./delivery-bill.component.css']
})
export class DeliveryBillComponent implements OnInit {
  productDetails: ProductDetails = {
    id: '',
    nomProduit: '',
    descriptionProduit: '',
    region: '',
    prix: 0,
    qt: 0,
    image: ''
  };

  deliveryInfo: DeliveryInfo = {
    fullName: '',
    phone: '',
    address: '',
    city: '',
    postalCode: '',
    specialInstructions: ''
  };

  userId: string = '';
  deliveryCost: number = 0;
  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private deliveryService: DeliveryService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Get user information from auth service
    const currentUserId = this.authService.getCurrentUserId();
    if (currentUserId) {
      this.userId = currentUserId;
      this.loadUserInfo(currentUserId);
    } else {
      console.error('User not logged in');
      this.router.navigate(['/utilisateur/login']);
      return;
    }

    // Get product details from route params
    this.route.queryParams.subscribe(params => {
      this.productDetails = {
        id: params['productId'],
        nomProduit: params['nomProduit'],
        descriptionProduit: params['descriptionProduit'],
        region: params['region'],
        prix: parseFloat(params['prix']),
        qt: parseInt(params['qt']),
        image: params['image']
      };

      // Calculate delivery cost based on region and distance
      if (this.productDetails.region) {
        this.calculateDeliveryCost();
      }
    });
  }

  loadUserInfo(userId: string): void {
    this.authService.getUserDetails(userId).subscribe({
      next: (data) => {
        this.deliveryInfo = {
          fullName: data.username || '',
          phone: data.phoneNumber || '',
          address: data.adressePatient || '',
          city: data.city || '',
          postalCode: data.postalCode || '',
          specialInstructions: ''
        };
      },
      error: (error) => {
        console.error('Error loading user info:', error);
        this.errorMessage = 'Failed to load user information';
      }
    });
  }

  calculateDeliveryCost(): void {
    this.deliveryService.calculateDeliveryCost(this.productDetails.region)
      .subscribe({
        next: (response) => {
          this.deliveryCost = response.deliveryCost;
        },
        error: (error) => {
          console.error('Error calculating delivery cost:', error);
          this.errorMessage = 'Failed to calculate delivery cost';
        }
      });
  }

  calculateTotal(): number {
    return this.productDetails.prix * this.productDetails.qt + this.deliveryCost;
  }

  onSubmit(): void {
    if (!this.userId) {
      this.errorMessage = 'Please log in to place an order';
      return;
    }

    // Log the user ID to verify it's being set
    console.log('Submitting order with user ID:', this.userId);

    const deliveryData = {
      user: {
        id: this.userId,
        name: this.deliveryInfo.fullName,
        email: '', // You can fill this if you have it
        phone: this.deliveryInfo.phone
      },
      region: this.productDetails.region,
      distanceInKm: 0, // Optionally set if you have it from deliveryCost API
      orderItems: [
        {
          product: {
            id: this.productDetails.id,
            name: this.productDetails.nomProduit,
            price: this.productDetails.prix,
            description: this.productDetails.descriptionProduit,
            imageUrl: this.productDetails.image
          },
          quantity: this.productDetails.qt
        }
      ]
    };

    this.isLoading = true;
    this.deliveryService.createDeliveryBill(deliveryData)
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          // Rediriger vers la page de paiement
          this.router.navigate(['/payment'], {
            queryParams: {
              amount: this.calculateTotal(),
              billId: response.id
            }
          });
        },
        error: (error) => {
          this.isLoading = false;
          console.error('Error creating delivery bill:', error);
          this.errorMessage = error.error || 'Failed to create delivery bill';
        }
      });
  }
} 