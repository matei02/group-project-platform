import { Component } from '@angular/core';
import { User } from '../shared/user.model';
import { CommonModule } from '@angular/common';
import UserService from '../shared/user.service';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.css'
})
export class ProfilePageComponent {

  user: User = {
    id: 0,
    username: '',
    virtualMoney: '0',
    experiencePoints: 0
  };

  ngOnInit() {
    this.fetchData();
  }

  async fetchData() {
    this.user = await UserService.getUserById(1); // Assuming user ID 1 for now
  }
 

  constructor() {}

  getCurrentLevel(): number {
    // Simple level calculation: every 500 XP = 1 level
    return Math.floor(this.user.experiencePoints / 500) + 1
  }

  getNextLevelXP(): number {
    const currentLevel = this.getCurrentLevel()
    return currentLevel * 500
  }

  getProgressPercentage(): number {
    const currentLevelXP = (this.getCurrentLevel() - 1) * 500
    const nextLevelXP = this.getNextLevelXP()
    const progress = ((this.user.experiencePoints - currentLevelXP) / (nextLevelXP - currentLevelXP)) * 100
    return Math.min(progress, 100)
  }

  getRank(): string {
    // Mock rank calculation based on XP
    if (this.user.experiencePoints >= 2000) return "Elite"
    if (this.user.experiencePoints >= 1500) return "Expert"
    if (this.user.experiencePoints >= 1000) return "Advanced"
    if (this.user.experiencePoints >= 500) return "Intermediate"
    return "Beginner"
  }

}
