import { Component, Inject, PLATFORM_ID, OnInit, OnDestroy } from "@angular/core"
import { FormsModule } from "@angular/forms"
import { CommonModule, isPlatformBrowser } from "@angular/common"
import { environment } from "../../../environments/environment"
import { Task } from "../shared/task.model"
import { Router } from "@angular/router"
import { QuillModule } from 'ngx-quill';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Subscription } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import axios from 'axios';
import BugReportService from "../shared/bugReport.service"

@Component({
  selector: "app-report-form",
  standalone: true,
  imports: [FormsModule, CommonModule, QuillModule],
  templateUrl: "./bugReportForm.component.html",
  styleUrls: ["./bugReportForm.component.css"],
})

export class BugReportFormComponent implements OnInit, OnDestroy {
  reportText = ""
  userId = 1
  taskId = 0
  apiUrl = `${environment.apiUrl}/report`
  isSubmitting = false
  isSuccess = false
  errorMessage = ""
  task: Task = {
    id: 0,
    title: '',
    description: '',
    link: '',
    image: '',
    difficulty: 'MEDIUM',
    payout: 0,
    budget: 0
  }
  quillModules: any
  uploadedFiles: File[] = []
  maxImageSize = 5 * 1024 * 1024 // 5MB
  maxVideoSize = 50 * 1024 * 1024 // 50MB
  maxTotalSize = 150 * 1024 * 1024 // 150MB
  taskStarted = false;
  iframeAllowed = false;
  taskInfoCollapsed = false;

  // Screen recording properties
  mediaRecorder: MediaRecorder | null = null;
  mediaStream: MediaStream | null = null;
  recordedChunks: BlobPart[] = [];
  isRecording = false;
  screenRecordingBlob: Blob | null = null; // Will only exist in memory for the session
  maxScreenRecordingSize = 500 * 1024 * 1024; // 500MB
  safeTaskLink: SafeResourceUrl | null = null;
  toastVisible = false;
  toastMessage = '';
  toastType: 'success' | 'error' | 'info' = 'info';
  recordingCancelled = false;
  connectionRefused = false;
  recordingDuration = 0;
  recordingTimerId: any = null;

  // Add upload progress tracking
  uploadProgress = 0;
  isUploading = false;

  // Add file validation
  private readonly fileConfig = {
    maxTotalSize: 150 * 1024 * 1024, // 150MB
    maxImageSize: 5 * 1024 * 1024,   // 5MB
    maxVideoSize: 50 * 1024 * 1024,  // 50MB
    validTypes: new Set([
      'image/jpeg', 'image/png', 'image/gif', 'image/webp',
      'video/mp4', 'video/webm', 'video/quicktime'
    ])
  };

  private subscriptions: Subscription[] = [];
  private stopRecordingResolver: (() => void) | null = null;

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private sanitizer: DomSanitizer,
    private http: HttpClient,
  ) {
    const nav = this.router.getCurrentNavigation();
    if (nav?.extras?.state?.['task']) {
      this.task = nav.extras.state['task'];
      this.taskId = this.task.id || 0;
    } else {
      // Redirect to tasks page if no task is provided
      // Consider if this is the right place if persistence is truly off
      // as a refresh would also trigger this if task state isn't passed again.
      if (isPlatformBrowser(this.platformId)) { // Only navigate in browser
        this.router.navigate(['/tasks']);
      }
    }
    // Persistence via restoreFormState() has been removed.
  }

  async ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      const Quill = (await import('quill')).default;
      const { ImageHandler, VideoHandler } = await import('ngx-quill-upload');
      Quill.register('modules/imageHandler', ImageHandler);
      Quill.register('modules/videoHandler', VideoHandler);
      this.initQuillModules();

      if (this.task?.link) {
        let url = this.task.link;
        if (!url.startsWith('http://') && !url.startsWith('https://')) {
          url = 'https://' + url;
          this.task.link = url;
        }
        this.safeTaskLink = this.sanitizer.bypassSecurityTrustResourceUrl(url);
        this.iframeAllowed = true;
        this.connectionRefused = false;

        setTimeout(() => {
          const iframe = document.querySelector('iframe');
          try {
            if (iframe && (!iframe.contentWindow || iframe.contentWindow.location.href === 'about:blank')) {
              this.connectionRefused = true;
            }
          } catch (error) {
            this.connectionRefused = true;
          }
        }, 3000);
      } else {
        this.iframeAllowed = false;
      }
      this.setupBeforeUnloadWarning(); // Still useful to warn about unsaved data in current session
    }
  }

  async ngOnDestroy(): Promise<void> {
    if (this.mediaRecorder && this.isRecording) {
      await this.stopScreenRecording();
    }
    if (this.mediaStream) {
      this.mediaStream.getTracks().forEach(track => track.stop());
      this.mediaStream = null;
    }
    if (this.recordingTimerId) {
      clearInterval(this.recordingTimerId);
    }
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  setupBeforeUnloadWarning(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    const beforeUnloadHandler = (event: BeforeUnloadEvent) => {
      // Warn if there's text, uploaded files, or an active recording
      if (this.reportText.trim() || this.uploadedFiles.length > 0 || this.isRecording) {
        event.preventDefault();
        event.returnValue = 'You have unsaved changes. Are you sure you want to leave? Your report will be lost.';
        return event.returnValue;
      }
    };
    window.addEventListener('beforeunload', beforeUnloadHandler);
    this.subscriptions.push(new Subscription(() => {
      window.removeEventListener('beforeunload', beforeUnloadHandler);
    }));
  }

  initQuillModules() {
    this.quillModules = {
      toolbar: [
        ['bold', 'italic', 'underline', 'strike'],
        ['blockquote', 'code-block'],
        [{ 'header': 1 }, { 'header': 2 }],
        [{ 'list': 'ordered' }, { 'list': 'bullet' }],
        [{ 'indent': '-1' }, { 'indent': '+1' }],
        [{ 'color': [] }, { 'background': [] }],
        ['clean'],
        ['link', 'image', 'video']
      ],
      imageHandler: {
        upload: (file: File) => this.handleFileUpload(file, 'image'),
        accepts: ['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg'],
      },
      videoHandler: {
        upload: (file: File) => this.handleFileUpload(file, 'video'),
        accepts: ['mp4', 'webm', 'mov', 'avi', 'mkv'],
      },
      clipboard: { matchVisual: false },
      keyboard: { bindings: { tab: false } }
    };
  }

  handleFileUpload(file: File, type: 'image' | 'video'): Promise<string> {
    return new Promise((resolve, reject) => {
      const isImage = type === 'image';
      const maxSize = isImage ? this.maxImageSize : this.maxVideoSize;
      const allowedTypes = isImage
        ? ['image/jpeg', 'image/png', 'image/jpg', 'image/gif', 'image/webp', 'image/svg+xml']
        : ['video/mp4', 'video/webm', 'video/quicktime', 'video/mov', 'video/avi', 'video/x-matroska'];

      const fileTypeOk = allowedTypes.some(allowedType => {
        if (!isImage && file.type.startsWith('video/')) return true;
        return allowedTypes.includes(file.type);
      });

      if (!fileTypeOk) {
        this.showToast(`We do not accept ${file.type} file type!`, 'error');
        return reject('Invalid file type');
      }
      if (file.size > maxSize) {
        this.showToast(`Cannot upload more than ${maxSize / (1024 * 1024)} MB for a single ${type}!`, 'error');
        return reject('File too large');
      }
      const currentTotalSize = this.uploadedFiles.reduce((acc, f) => acc + f.size, 0);
      if (currentTotalSize + file.size > this.maxTotalSize) {
        this.showToast('Total upload size exceeded (150MB max)!', 'error');
        return reject('Total size exceeded');
      }
      this.uploadedFiles.push(file);
      // REMOVED: this.saveFormState();
      const objectUrl = URL.createObjectURL(file);
      this.showToast(`${isImage ? 'Image' : 'Video'} '${file.name}' ready for report.`, 'success');
      resolve(objectUrl);
    });
  }

  showToast(message: string, type: 'success' | 'error' | 'info' = 'info') {
    if (!isPlatformBrowser(this.platformId)) return;
    this.toastMessage = message;
    this.toastType = type;
    this.toastVisible = true;
    setTimeout(() => { this.toastVisible = false; }, 5000);
  }

  // REMOVED: saveFormState() method
  // REMOVED: restoreFormState() method
  // REMOVED: clearFormState() method

  onQuillContentChanged(event: any) {
    this.reportText = event.html;
    // REMOVED: this.saveFormState();
  }

  updateRecordingTimer() {
    this.recordingDuration = 0;
    if (this.recordingTimerId) clearInterval(this.recordingTimerId);
    this.recordingTimerId = setInterval(() => { this.recordingDuration++; }, 1000);
  }

  formatDuration(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
  }

  async onStartTask() {
    this.recordingCancelled = false;
    this.screenRecordingBlob = null;
    this.recordedChunks = [];

    try {
      this.mediaStream = await navigator.mediaDevices.getDisplayMedia({
        video: { frameRate: { ideal: 30 }, displaySurface: 'browser' } as any,
        audio: false
      });

      this.taskStarted = true;
      const mimeType = MediaRecorder.isTypeSupported('video/webm;codecs=vp9')
        ? 'video/webm;codecs=vp9' : 'video/webm';

      this.mediaRecorder = new MediaRecorder(this.mediaStream, {
        mimeType, videoBitsPerSecond: 2500000
      });

      this.mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) this.recordedChunks.push(event.data);
      };

      this.mediaRecorder.onstop = () => {
        this.isRecording = false;
        if (this.recordingTimerId) {
          clearInterval(this.recordingTimerId);
          this.recordingTimerId = null;
        }
        if (this.recordedChunks.length > 0) {
          const blob = new Blob(this.recordedChunks, { type: this.mediaRecorder?.mimeType || 'video/webm' });
          if (blob.size > this.maxScreenRecordingSize) {
            this.showToast(`Screen recording exceeds maximum size of ${this.maxScreenRecordingSize / (1024 * 1024)}MB. Recording not saved.`, 'error');
            this.screenRecordingBlob = null;
          } else {
            this.screenRecordingBlob = blob;
            if (!this.recordingCancelled && !this.isSubmitting) {
              this.showToast(`Screen recording saved (${(blob.size / (1024 * 1024)).toFixed(2)} MB)`, 'success');
            }
          }
          this.recordedChunks = [];
        }
        if (this.mediaStream) {
          this.mediaStream.getTracks().forEach(track => track.stop());
          this.mediaStream = null;
        }
        if (this.stopRecordingResolver) {
          this.stopRecordingResolver();
          this.stopRecordingResolver = null;
        }
      };

      this.mediaStream.getTracks().forEach(track => {
        track.onended = async () => {
          if (this.isRecording) {
            this.showToast('Screen sharing ended by user. Finalizing recording...', 'info');
            await this.stopScreenRecording();
          }
        };
      });

      this.mediaRecorder.start(1000);
      this.isRecording = true;
      this.updateRecordingTimer();
      this.showToast('Screen recording started. Please keep this tab open.', 'info');

    } catch (error) {
      console.error('Error starting screen recording:', error);
      this.taskStarted = false;
      this.recordingCancelled = true;
      this.isRecording = false;
      if (this.mediaStream) {
        this.mediaStream.getTracks().forEach(track => track.stop());
        this.mediaStream = null;
      }
      this.showToast('Task start failed: Screen recording permission denied or error.', 'error');
    }
  }

  stopScreenRecording(): Promise<void> {
    return new Promise((resolve) => {
      if (this.mediaRecorder && this.mediaRecorder.state === "recording") {
        this.stopRecordingResolver = resolve;
        this.mediaRecorder.stop();
      } else {
        if (this.mediaStream && !this.isRecording) {
          this.mediaStream.getTracks().forEach(track => track.stop());
          this.mediaStream = null;
        }
        resolve();
      }
    });
  }

  async submitReport(): Promise<void> {
    try {
      this.recordingCancelled = true;
      this.isUploading = true;
      this.uploadProgress = 0;

      if (!this.reportText.trim()) {
        throw new Error("Please enter a bug report before submitting");
      }

      if (this.isRecording || (this.mediaRecorder && this.mediaRecorder.state === "recording")) {
        this.showToast('Finalizing screen recording, please wait...', 'info');
        await this.stopScreenRecording();
      }

      if (!this.screenRecordingBlob) {
        throw new Error("Screen recording is required");
      }

      const formData = new FormData();
      formData.append('requestingUserId', this.userId.toString());
      formData.append('taskId', this.taskId.toString());
      formData.append('reportText', this.reportText);
      formData.append('status', "PENDING");

      // Add screen recording with proper metadata
      formData.append('screenRecording',  
        this.screenRecordingBlob,
        `screen-recording-${this.taskId}-${Date.now()}.webm`
      );

      // Add uploaded files with proper metadata
      this.uploadedFiles.forEach(file => {
        formData.append('additionalFiles', file, file.name);
      });

      // Add task metadata
      if (this.task) {
        formData.append('taskMetadata', JSON.stringify({
          taskId: this.task.id,
          difficulty: this.task.difficulty,
          timestamp: new Date().toISOString()
        }));
      }

      console.log("--- FormData Content for Submission ---");
      for (const [key, value] of formData.entries()) {
        if (value instanceof File) {
          console.log(`  ${key}: File { name: "${value.name}", size: ${value.size}, type: "${value.type}" }`);
        } else {
          console.log(`  ${key}: ${value}`);
        }
      }
      if (this.screenRecordingBlob) {
        console.log("  (Raw Screen Recording Blob details: size:", this.screenRecordingBlob.size, "type:", this.screenRecordingBlob.type, ")");
      }
      console.log("--- End of FormData ---");

      const response = await BugReportService.submitReport(
        this.taskId,
        formData,
        (progress) => {
          this.uploadProgress = progress;
          this.showToast(`Uploading: ${progress}%`, 'info');
        }
      );

      this.isUploading = false;
      this.isSuccess = true;
      this.showToast("Bug report submitted successfully!", 'success');

      // Clear form and navigate away
      setTimeout(() => {
        this.taskStarted = false;
        this.screenRecordingBlob = null;
        this.reportText = '';
        this.uploadedFiles = [];
        this.router.navigate(['/task-home']);
      }, 2000);

    } catch (error) {
      this.isUploading = false;
      this.errorMessage = error instanceof Error ? error.message : "Failed to submit the bug report. Please try again.";
      this.showToast(this.errorMessage, 'error');
      this.recordingCancelled = false;
      console.error('Error submitting bug report:', error);
    }
  }

  triggerFileInput(fileInput: HTMLInputElement) {
    fileInput.click();
  }

  handleManualFileUpload(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;

    const files = Array.from(input.files);
    let currentTotalSize = this.uploadedFiles.reduce((acc, f) => acc + f.size, 0);

    for (const file of files) {
      const isImage = file.type.startsWith('image/');
      const isVideo = file.type.startsWith('video/');

      if (!isImage && !isVideo) {
        this.showToast(`File '${file.name}' has an unsupported type (${file.type}). Only images and videos are allowed.`, 'error');
        continue;
      }
      const maxSize = isImage ? this.maxImageSize : this.maxVideoSize;
      if (file.size > maxSize) {
        this.showToast(`File '${file.name}' (${(file.size / 1024 / 1024).toFixed(2)}MB) exceeds the max size of ${(maxSize / 1024 / 1024)}MB for its type.`, 'error');
        continue;
      }
      if (currentTotalSize + file.size > this.maxTotalSize) {
        this.showToast('Cannot add more files, total upload size limit (150MB) would be exceeded.', 'error');
        break;
      }
      this.uploadedFiles.push(file);
      currentTotalSize += file.size;
      this.showToast(`File '${file.name}' added.`, 'success');
    }
    // REMOVED: this.saveFormState();
    input.value = '';
  }

  removeFile(index: number) {
    if (index >= 0 && index < this.uploadedFiles.length) {
      const removedFile = this.uploadedFiles.splice(index, 1);
      // REMOVED: this.saveFormState();
      this.showToast(`File '${removedFile[0].name}' removed.`, 'info');
    }
  }

  handleIframeError() {
    this.connectionRefused = true;
    console.log('Iframe failed to load, showing fallback UI');
  }

  handleIframeLoad(event: Event) {
    try {
      const iframe = event.target as HTMLIFrameElement;
      if (iframe.contentWindow && iframe.contentWindow.location.href !== 'about:blank') {
        this.connectionRefused = false;
      } else {
        this.connectionRefused = true;
      }
    } catch (error) {
      console.error('Cannot access iframe content:', error);
      this.connectionRefused = true;
    }
  }
}