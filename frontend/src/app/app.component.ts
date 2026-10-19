import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { FormsModule } from '@angular/forms';  // Import FormsModule here
import axios from 'axios';
import { NavbarComponent } from './utilities/navbar/navbar.component';
import { BugReportFormComponent } from './tasks/bugReportForm/bugReportForm.component';


@Component({
  selector: 'app-root',
  standalone: true,
  // standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent, CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'groupproject_frontend';
  data: any = null; // Variable to store the fetched data


 

  // Method to fetch data from the API
  fetchData() {
    console.log('Fetching data...');
    axios
      .get('/users/1')
      .then((response) => {
        this.data = response.data; // Store the fetched data
        console.log(this.data); // Log the fetched data
      })
      .catch((error) => {
        console.error('Error fetching data:', error);
      });
  }
}
