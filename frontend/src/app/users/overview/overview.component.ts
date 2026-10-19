import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import axios from 'axios';
import { environment }  from '../../../environments/environment'
import UserService from '../shared/user.service';

// Define the interface for the response from your backend's spending endpoint
interface TotalSpendingData {
  totalPayout: number;
  totalBudget: number;
}

// Define the interface for your user data if you have a specific shape
interface UserDetail {
    id: number;
    username: string;
    virtualMoney: number;
    // Add any other user properties you expect here
}

@Component({
  selector: 'app-overview',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.css',
})
export class OverviewComponent implements OnInit {
  // data: any 
  data: UserDetail | any;
  totalSpendingData: TotalSpendingData | null = null;
  errorMessage: string = '';

  private currentCreatorId: number = 1; // For now hardcoded user 1


  ngOnInit() {
    this.fetchData();
  }

  async fetchData() {
    console.log('Fetching data...');
    try {
      // Fetch user details
      const userData = await UserService.getUserById(this.currentCreatorId);
      this.data = userData;
      console.log('User data:', this.data);

      // Fetch total spending and budget for the same user
      const spendingData = await UserService.getTotalSpendingAndBudgetByCreator(this.currentCreatorId);
      this.totalSpendingData = spendingData;
      console.log('Spending data:', this.totalSpendingData);

      this.errorMessage = ''; // Clear any previous errors

    } catch (error) {
      console.error('Error fetching data:', error);
      this.errorMessage = 'Failed to load user and spending data.';
      this.data = null; // Clear data on error
      this.totalSpendingData = null; // Clear spending data on error
    }
  }
}
