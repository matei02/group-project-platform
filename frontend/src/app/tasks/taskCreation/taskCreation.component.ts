import { Component, Inject, PLATFORM_ID, OnInit, OnDestroy } from "@angular/core"
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import  TaskService  from '../shared/tasks.service';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import UserService from '../../users/shared/user.service';

import Keycloak from 'keycloak-js';

@Component({
  selector: 'app-task-creation',
  standalone: true,
  templateUrl: './taskCreation.component.html',
  styleUrls: ['./taskCreation.component.css'],
  imports: [
    CommonModule,
    FormsModule
  ]
})


export class TaskCreationComponent {
  constructor(private router: Router,   @Inject(PLATFORM_ID) private platformId: Object,) {}

  title: string = '';
  budget: number = 2500;
  link: string = '';
  description: string = '';
  difficulty: 'HIGH' | 'MEDIUM' | 'LOW' = 'LOW';

 toastVisible = false;
  toastMessage = '';
  toastType: 'success' | 'error' | 'info' = 'info';

   uploadedFiles: File[] = []
    maxImageSize = 5 * 1024 * 1024 // 5MB
  maxVideoSize = 50 * 1024 * 1024 // 50MB
  maxTotalSize = 150 * 1024 * 1024 // 150MB
  errorMessage = ""

  previewUrl: string | null = null;

  showToast(message: string, type: 'success' | 'error' | 'info' = 'info') {
    if (!isPlatformBrowser(this.platformId)) return;
    this.toastMessage = message;
    this.toastType = type;
    this.toastVisible = true;
    setTimeout(() => { this.toastVisible = false; }, 5000);
  }


  onTaskAbort() {
    this.router.navigate(['/task-home']);
  }

  async onTaskCreation() {
    try {
        let base64Image: string | null = null;

  if (this.uploadedFiles.length > 0) {
    base64Image = await this.readFileAsBase64(this.uploadedFiles[0]);
  }
    this.errorMessage = '';

      const body = {
        title: this.title,
        budget: this.budget,
        link: this.link,
        description: this.description,
        image: base64Image!,
        payout: 0,
        difficulty: this.difficulty,
        creator: {
          id: 1,
          username: 'Bob',
          virtualMoney: 100,
        }
      };

      await TaskService.createTask(body);
      await UserService.decreaseVirtualMoney(1, this.budget);

      this.router.navigate(['/task-home']);
    } catch (err: any) {
      console.error('Task creation error:', err);
      if (err.response && err.response.status === 400) {
        const errorHeader = err.response.headers['error'];
        this.errorMessage = errorHeader || 'An unknown error occurred.';
        console.error('Task creation failed:', this.errorMessage);
      } else {
        this.errorMessage = 'Something went wrong!';
      }
    }

    this.router.navigate(['/task-home']);

  }

  // async onTaskCreation() {
  // let base64Image: string | null = null;

  // if (this.uploadedFiles.length > 0) {
  //   base64Image = await this.readFileAsBase64(this.uploadedFiles[0]);
  // }

  // console.log('Base64 Image:', base64Image);

  //   const body = {
  //     title: this.title,
  //     budget: this.budget,
  //     link: this.link,
  //     description: this.description,
  //     image: base64Image!,
  //     payout: 0,
  //     difficulty: this.difficulty,
  //     creator:{
  //     id: 1,
  //     name: 'Bob',           
  //     virtualMoney: "100",
  //     } 
  //   };

  //   TaskService.createTask(body);

  //   UserService.decreaseVirtualMoney(1, this.budget);

  //   this.router.navigate(['/task-home']);
   
  // }

  triggerFileInput(fileInput: HTMLInputElement) {
    fileInput.click();
  }

  private readFileAsBase64(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
      const result = reader.result as string;
      // Remove metadata: "data:image/png;base64,..."
      const base64 = result.split(',')[1];
      resolve(base64);
    };
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
}


  handleManualFileUpload(event: Event) {
  const input = event.target as HTMLInputElement;
  if (!input.files || input.files.length === 0) return;

  const file = input.files[0]; // Only take the first file
  const isImage = file.type.startsWith('image/');
  const isVideo = file.type.startsWith('video/');

  if (!isImage && !isVideo) {
    this.showToast(`File '${file.name}' has an unsupported type (${file.type}). Only images and videos are allowed.`, 'error');
    input.value = '';
    return;
  }

  const maxSize = isImage ? this.maxImageSize : this.maxVideoSize;
  if (file.size > maxSize) {
    this.showToast(`File '${file.name}' (${(file.size / 1024 / 1024).toFixed(2)}MB) exceeds the max size of ${(maxSize / 1024 / 1024)}MB for its type.`, 'error');
    input.value = '';
    return;
  }

  if (file.size > this.maxTotalSize) {
    this.showToast('File exceeds total upload size limit (150MB).', 'error');
    input.value = '';
    return;
  }

  this.uploadedFiles = [file]; // Replace any previous file
   this.previewUrl = URL.createObjectURL(file);
  this.showToast(`File '${file.name}' added.`, 'success');
  input.value = '';
}


  removeFile(index: number) {
    if (index >= 0 && index < this.uploadedFiles.length) {
      const removedFile = this.uploadedFiles.splice(index, 1);
      this.showToast(`File '${removedFile[0].name}' removed.`, 'info');
    }
  }

}


