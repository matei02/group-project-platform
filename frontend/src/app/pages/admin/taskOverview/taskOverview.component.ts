import { Component, OnInit } from '@angular/core';
import { Task } from '../../../tasks/shared/task.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import TaskService from '../../../tasks/shared/tasks.service';

@Component({
  selector: 'app-admin-task-approval',
  standalone: true,
  templateUrl: './taskOverview.component.html',
  styleUrls: ['./taskOverview.component.css'],
  imports: [CommonModule, FormsModule],
})
export class AdminTaskApprovalComponent implements OnInit {
  tasks: Task[] = [];
  displayedTasks: Task[] = [];
  isLoading: boolean = true;
  searchQuery = '';
  filterStatus: 'ALL' | 'PENDING' | 'ACCEPTED' | 'DECLINED' = 'ALL';

  private subscriptions: Subscription = new Subscription();

  constructor() {}

  ngOnInit(): void {
    this.loadTasks();
  }

  async loadTasks(): Promise<void> {
    this.isLoading = true;
    try {
      const data = await TaskService.getAllTasks();
      this.tasks = [...data.tasks];
      this.applyFilters();
    } catch (error) {
      console.error('Error fetching tasks:', error);
    } finally {
      this.isLoading = false;
    }
  }

  async approveTask(taskId: number): Promise<void> {
    try {
      await TaskService.approveTask(taskId);
      console.log('Task APPROVED successfully');
      await this.refreshTasks();
    } catch (error) {
      console.error('Error approving task:', error);
    }
  }

  async declineTask(taskId: number): Promise<void> {
    try {
      await TaskService.declineTask(taskId);
      console.log('Task DECLINED successfully');
      await this.refreshTasks();
    } catch (error) {
      console.error('Error declining task:', error);
    }
  }

  async refreshTasks(): Promise<void> {
    await this.loadTasks();
  }

  filterByStatus(status: 'ALL' | 'PENDING' | 'ACCEPTED' | 'DECLINED'): void {
    this.filterStatus = status;
    this.applyFilters();
  }

  searchTasks(): void {
    this.applyFilters();
  }

  private applyFilters(): void {
    let filtered = [...this.tasks];

    if (this.filterStatus !== 'ALL') {
      filtered = filtered.filter(task => task.status === this.filterStatus);
    }

    if (this.searchQuery.trim() !== '') {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(task =>
        task.title.toLowerCase().includes(query)
      );
    }

    this.displayedTasks = filtered;
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}