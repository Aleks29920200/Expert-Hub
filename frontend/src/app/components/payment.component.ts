import { Component } from '@angular/core';
import { PaymentService } from '../services/payment.service';
import { loadStripe } from '@stripe/stripe-js';

@Component({
  selector: 'app-payment',
  template: `
    <div class="payment-container">
      <h3>Complete your Purchase</h3>
      <button (click)="payWithHostedCheckout()">Pay via Stripe Checkout (Redirect)</button>

      <div class="divider">OR</div>

      <button (click)="payWithCustomFlow()">Pay with Card</button>
    </div>
  `
})
export class PaymentComponent {
  // Replace with your actual Stripe Public Key
  stripePromise = loadStripe('pk_test_YOUR_STRIPE_PUBLIC_KEY');
  offerId = 1; // Example Offer ID

  constructor(private paymentService: PaymentService) {}

  // Option 1: Hosted Checkout (Redirects user to Stripe)
  payWithHostedCheckout() {
    // 1. Взимаме ID-то на логнатия потребител (както направихме в Home)
    let myUserId = 1; // Хардкоднато за тест, ако няма логнат
    const currentUserStr = localStorage.getItem('currentUser');
    if (currentUserStr) {
      try {
        const currentUserObj = JSON.parse(currentUserStr);
        if (currentUserObj && currentUserObj.id) {
          myUserId = currentUserObj.id;
        }
      } catch (e) {
        console.warn('Не можах да прочета логнатия потребител', e);
      }
    }

    // 2. Подаваме И ДВЕТЕ променливи на сървиса (първо userId, после offerId)
    this.paymentService.createHostedCheckoutSession(myUserId, this.offerId).subscribe({
      next: (response: any) => {
        if (response && response.url) {
          window.location.href = response.url;
        }
      },
      error: (err) => {
        console.error('Payment error', err);
      }
    });
  }

  // Option 2: Custom Element Flow (Advanced)
  async payWithCustomFlow() {
    this.paymentService.createPaymentIntent(this.offerId).subscribe({
      next: async (data: any) => {
        const stripe = await this.stripePromise;
        // Use stripe.confirmCardPayment using data.clientSecret here
        console.log('Client Secret received:', data.clientSecret);
      }
    });
  }
}
