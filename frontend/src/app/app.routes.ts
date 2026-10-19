import { Routes } from '@angular/router';
import { TaskCreationComponent } from './tasks/taskCreation/taskCreation.component';
import { LoginComponent } from './users/login/login.component';
import { TaskHomeComponent } from './tasks/taskHome/taskHome.component';
import { BugReportFormComponent } from './tasks/bugReportForm/bugReportForm.component';
import { PaymentComponent } from './payments/payment/payment.component'
import { PayoutComponent } from './payments/payout/payout.component';
import { OverviewComponent } from './users/overview/overview.component';
import { AdminTaskApprovalComponent } from './pages/admin/taskOverview/taskOverview.component';
import { BugReportHomeComponent } from './tasks/bugReportHome/bugReportHome.component';
import { AllTimeLeaderboardComponent } from './leaderboards/all-time-leaderboard/all-time-leaderboard.component';
import { UpdateTaskComponent } from './tasks/taskUpdate/updateTask.component';
import { ProfilePageComponent } from './users/profile-page/profile-page.component';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'task-creation', component: TaskCreationComponent },
  { path: 'task-home', component: TaskHomeComponent },
  { path: 'report', component: BugReportFormComponent },
  { path: 'payment', component: PaymentComponent },
  { path: 'payout', component: PayoutComponent },
  { path: 'user-overview', component: OverviewComponent },
  { path: 'admin-task-overview', component: AdminTaskApprovalComponent },
  { path: 'bug-report-home', component: BugReportHomeComponent },
  { path: 'all-time-leaderboard', component: AllTimeLeaderboardComponent },
  { path: 'update-task/:id', component: UpdateTaskComponent},
  { path: 'profile', component: ProfilePageComponent}
];
