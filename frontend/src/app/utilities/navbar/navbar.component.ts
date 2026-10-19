// import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { Component, OnInit } from '@angular/core';
import UserService from '../../users/shared/user.service';

// Define the interface for your user data if you have a specific shape
interface UserDetail {
  id: number;
  username: string;
  virtualMoney: number;
  // Add any other user properties you expect here
}

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {
  ngOnInit() {
    this.fetchData();
  }
  mobileMenuOpen: boolean = false;

  data: UserDetail | any;
  private currentCreatorId: number = 1; // For now hardcoded user 1

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  async fetchData() {
    console.log('Fetching data...');
    try {
      // Fetch user details
      const userData = await UserService.getUserById(this.currentCreatorId);
      this.data = userData;
      console.log('User data:', this.data);

    } catch (error) {
      console.error('Error fetching data:', error);
      this.data = null; // Clear data on error
    }
  }
}
