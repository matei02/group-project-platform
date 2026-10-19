import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-task-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;

  constructor(
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSignIn() {
    if (this.loginForm.invalid) {
      // Mark all fields as touched to trigger validation messages
      Object.keys(this.loginForm.controls).forEach(key => {
        const control = this.loginForm.get(key);
        control?.markAsTouched();
      });
      return;
    }

    this.loading = true;
    
    // Here you would typically call your authentication service
    // For now, we'll just simulate a login with a timeout
    setTimeout(() => {
      this.loading = false;
      this.router.navigate(['/task-home']);
    }, 1000);
  }

  forgotPassword() {
    const email = this.loginForm.get('email')?.value;
    if (!email) {
      alert('Please enter your email address first');
      return;
    }
    
    alert(`Password reset instructions would be sent to: ${email}`);
    // In a real application, you would call your auth service here
  }
}
