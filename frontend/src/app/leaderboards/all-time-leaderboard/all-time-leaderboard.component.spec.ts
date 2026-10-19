import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllTimeLeaderboardComponent } from './all-time-leaderboard.component';

describe('AllTimeLeaderboardComponent', () => {
  let component: AllTimeLeaderboardComponent;
  let fixture: ComponentFixture<AllTimeLeaderboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllTimeLeaderboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllTimeLeaderboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
