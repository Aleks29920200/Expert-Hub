import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // Corresponds to PaymentController: createPaymentIntent
  createPaymentIntent(offerId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/payment`, { offerId });
  }
// Добавяме параметър userId към метода
  createHostedCheckoutSession(userId: number, offerId: number): Observable<any> {
    const url = 'http://localhost:8080/api/hostedCheckout/create-checkout-session';
    // Изпращаме JSON обект с userId и offerId, точно както Java-та го очаква в CheckoutRequest
    return this.http.post(url, { userId: userId, offerId: offerId });
  }

}
