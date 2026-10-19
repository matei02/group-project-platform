import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import TaskService from "../shared/tasks.service"
import { Task } from '../shared/task.model';

@Component({
  selector: 'app-task-home',
  standalone: true,
  templateUrl: './taskHome.component.html',
  styleUrls: ['./taskHome.component.css'],
  imports: [CommonModule]
})



export class TaskHomeComponent implements OnInit {
  
  tasks: Task[] = [];
  creatorId = 1!;
  role = "creator";
  isLoading: boolean = true;

  constructor(
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.fetchTasks();
  
  }

  fetchTasks(): void {
    const creatorId = 1;
    const role = "creator";
    this.isLoading = true;
  
   TaskService.getTasksFromCreator(creatorId)
      .then((response) => {
        this.tasks = response.tasks;
        
        this.tasks = [...this.tasks.sort((a: Task, b: Task) => (Number(b.id || 0) - Number(a.id || 0)))];
  
       


        console.log(this.tasks);
        this.isLoading = false;
      })
      .catch((error) => {
        console.error('Error fetching tasks:', error);
        this.isLoading = false;
      });
  }
  
  archive(taskId: number): void {
    TaskService.archiveTask(taskId)
    .then(() => {
        this.fetchTasks(); // Refresh the task list after archiving
      })
      .catch((error) => {
        console.error('Error archiving task:', error);
      });
  }

  navigateToCreate() {
    this.router.navigate(['/task-creation']);
  }

  navigateToUpdate(taskId: string): void {
  console.log('Navigating to update task with ID:', taskId);
  this.router.navigate(['/update-task', taskId]);
}

  navigateToReportPage(task: Task) {
    this.router.navigate(['/report'], { state: { task: task } });
  }
}

export interface RenderTask {
  id: number;
  title: string;
  description: string;
  difficulty: 'LOW' | 'MEDIUM' | 'HIGH';
  status: 'ACCEPTED' | 'PENDING' | 'DECLINED' | 'DEACTIVATED';
  previewUrl?: string | null; 
  creator?: { id: number };
}