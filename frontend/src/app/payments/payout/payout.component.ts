import { Component, OnInit, OnDestroy } from '@angular/core';
import { loadStripe, StripeElements, StripeCardElement, Stripe, TokenResult } from '@stripe/stripe-js';
import { PaymentService } from '../shared/payment.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import UserService from '../../users/shared/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-payout',
  imports: [CommonModule, FormsModule],
  templateUrl: './payout.component.html',
  styleUrls: ['./payout.component.css']
})


export class PayoutComponent {
  recipientEmail: string = '';
  amount: number = 5;
  currency: string = 'eur';
  userId: number = 1;
  errorMessage: string = '';
  constructor(private router: Router) { }

  limitDecimals(event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = input.value;

    if (value.includes('.')) {
      const [whole, decimal] = value.split('.');
      if (decimal.length > 2) {
        input.value = `${whole}.${decimal.slice(0, 2)}`;
        this.amount = parseFloat(input.value);
      }
    }
  }


  async submitPayout() {
    this.errorMessage = '';
    try {
      const result = await PaymentService.withdraw(
        this.recipientEmail,
        this.amount,
        this.currency,
        this.userId
      );

      console.log('Payout successful!', result);
      await UserService.decreaseVirtualMoney(this.userId, this.amount);
      this.router.navigate(['/user-overview']);

    } catch (err: any) {
      console.error('Payout error:', err);
      if (err.status === 400) {
        this.errorMessage = err.response.data || 'An unknown error occurred.';
        console.error('Payout failed:', this.errorMessage);
      }
      else {
        this.errorMessage = 'Something went wrong!';
      }
    }
  }
}

