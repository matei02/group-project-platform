import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QuillModule } from 'ngx-quill';
import { FormsModule } from '@angular/forms';
import BugReportService from '../shared/bugReport.service';

@Component({
  selector: 'app-bug-report-home',
  templateUrl: './bugReportHome.component.html',
  styleUrls: ['./bugReportHome.component.css'],
  imports: [CommonModule, QuillModule, FormsModule]
})
export class BugReportHomeComponent implements OnInit {
  bugReports: any[] = [];
  expandedIndex: number | null = null;
  quillModules = {
    toolbar: false
  };
  toastVisible = false;
  toastMessage = '';
  toastType: 'success' | 'error' | 'info' = 'info';

  constructor() { }

  async ngOnInit() {
    try {
      this.bugReports = await BugReportService.getAllBugReports();
      this.bugReports = this.bugReports.map(report => ({
        ...report,
        mediaFiles: report.additionalFiles || [],
        currentImageIndex: 0
      }));
      console.log('Bug reports fetched');
      console.dir(this.bugReports)
    } catch (error) {
      console.error('Error fetching bug reports:', error);
    }
  }

  toggleExpand(index: number) {
    this.expandedIndex = this.expandedIndex === index ? null : index;
  }

  prevImage(index: number) {
    const report = this.bugReports[index];
    report.currentImageIndex = (report.currentImageIndex - 1 + report.mediaFiles.length) % report.mediaFiles.length;
  }

  nextImage(index: number) {
    const report = this.bugReports[index];
    report.currentImageIndex = (report.currentImageIndex + 1) % report.mediaFiles.length;
  }

  openImageInFullScreen(imageBase64: string) {
    const image = new Image();
    image.src = `data:image/png;base64,${imageBase64}`;
    const w = window.open('');
    w?.document.write(image.outerHTML);
  }

  showToast(message: string, type: 'success' | 'error' | 'info' = 'info') {
    this.toastMessage = message;
    this.toastType = type;
    this.toastVisible = true;
    setTimeout(() => { this.toastVisible = false; }, 5000);
  }

  async approveReport(reportId: number, index: number) {
    try {
      await BugReportService.approveReport(reportId);
      this.bugReports[index].status = 'APPROVED';
      this.showToast('Bug report approved successfully!', 'success');
    } catch (error) {
      console.error('Error approving bug report:', error);
      this.showToast('Failed to approve bug report. Please try again.', 'error');
    }
  }

  async declineReport(reportId: number, index: number) {
    try {
      await BugReportService.declineReport(reportId);
      this.bugReports[index].status = 'DECLINED';
      this.showToast('Bug report declined successfully!', 'success');
    } catch (error) {
      console.error('Error declining bug report:', error);
      this.showToast('Failed to decline bug report. Please try again.', 'error');
    }
  }
}
