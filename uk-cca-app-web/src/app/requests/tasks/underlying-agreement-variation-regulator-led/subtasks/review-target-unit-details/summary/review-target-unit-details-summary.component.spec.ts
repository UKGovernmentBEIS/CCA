import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

import { mockRequestTaskState } from '../../../testing/mock-data';
import ReviewTargetUnitDetailsSummaryComponent from './review-target-unit-details-summary.component';

describe('ReviewTargetUnitDetailsSummaryComponent', () => {
  let component: ReviewTargetUnitDetailsSummaryComponent;
  let fixture: ComponentFixture<ReviewTargetUnitDetailsSummaryComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewTargetUnitDetailsSummaryComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockRequestTaskState);

    fixture = TestBed.createComponent(ReviewTargetUnitDetailsSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    const targetUnitDetailsHeading = screen.getAllByText('Target unit details')[0];
    expect(targetUnitDetailsHeading).toHaveTextContent('Target unit details');
    expect(screen.getByText('Summary')).toBeInTheDocument();
  });
});
