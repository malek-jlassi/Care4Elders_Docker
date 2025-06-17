import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

interface DeliveryCostResponse {
  deliveryCost: number;
  distanceInKm: number;
  userLocation: {
    latitude: number;
    longitude: number;
  };
  destinationLocation: {
    latitude: number;
    longitude: number;
  };
}

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {
  private apiUrl = 'http://localhost:8080/api/delivery';

  constructor(private http: HttpClient) {}

  calculateDeliveryCost(region: string): Observable<DeliveryCostResponse> {
    return this.http.get<DeliveryCostResponse>(`${this.apiUrl}/distance-details`, {
      params: { region }
    });
  }

  createDeliveryBill(deliveryData: any): Observable<any> {
    const { userId, ...billData } = deliveryData;
    return this.http.post(`${this.apiUrl}/generate-bill`, billData, {
      params: { userId }
    });
  }

  getDeliveryBills(): Observable<any> {
    return this.http.get(`${this.apiUrl}/bills`);
  }

  getDeliveryBillsByUser(userId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/bills/user/${userId}`);
  }

  getDeliveryBillsByStatus(status: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/bills/status/${status}`);
  }

  getDeliveryBillsByRegion(region: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/bills/region/${region}`);
  }

  downloadDeliveryBillPdf(billId: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/bills/${billId}/pdf`, {
      responseType: 'blob'
    });
  }
} 