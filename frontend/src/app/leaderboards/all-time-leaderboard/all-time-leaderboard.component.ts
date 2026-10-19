import { Component, OnInit } from '@angular/core';
import { LeaderboardService } from '../shared/leaderboard.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-all-time-leaderboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './all-time-leaderboard.component.html',
  styleUrls: ['./all-time-leaderboard.component.css'],
})
export class AllTimeLeaderboardComponent implements OnInit {
  leaderboard: any[] = [];

  async ngOnInit() {
    const response = await LeaderboardService.getAllTimeLeaderboard();
    this.leaderboard = response.leaderboard
  }
}
