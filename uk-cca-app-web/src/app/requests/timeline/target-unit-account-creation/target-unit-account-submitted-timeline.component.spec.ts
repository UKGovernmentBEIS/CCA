import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByTestId, getSummaryListData } from '@testing';

import { mockTargetUnitAccountRequestActionState } from './mocks/mock-target-unit-account-request-action-state';
import { TargetUnitAccountSubmittedTimelineComponent } from './target-unit-account-submitted-timeline.component';

describe('TargetUnitAccountSubmittedTimelineComponent', () => {
  let component: TargetUnitAccountSubmittedTimelineComponent;
  let fixture: ComponentFixture<TargetUnitAccountSubmittedTimelineComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TargetUnitAccountSubmittedTimelineComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(mockTargetUnitAccountRequestActionState);

    fixture = TestBed.createComponent(TargetUnitAccountSubmittedTimelineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct data sections', () => {
    expect(getByTestId('target-unit-details-list')).toBeTruthy();
    expect(getByTestId('operator-address-list')).toBeTruthy();
    expect(getByTestId('responsible-person-list')).toBeTruthy();
    expect(getByTestId('administrative-contact-list')).toBeTruthy();
  });

  it('should display the correct data', () => {
    const summaryValues = getSummaryListData(fixture.nativeElement);

    expect(summaryValues).toEqual([
      [
        [
          'Operator name',
          'Operator type',
          'Does your company have a registration number?',
          'Company number',
          'Standard Industrial Classification (SIC) codes',
          'Subsector',
        ],
        ['iojasdoiajsdoijas', 'Limited company', 'Yes', '123456789', '', ''],
      ],
      [['Address'], ['kjhnkjhn87678AL']],
      [
        ['First name', 'Last name', 'Job title', 'Address', 'Phone number', 'Email address'],
        ['asdasdsa', 'lname', 'job', 'kjhnkjhn87678AL', 'UK (44) 1234567890', 'test-test@cca.uk'],
      ],
      [
        ['First name', 'Last name', 'Job title', 'Email address', 'Phone number', 'Address'],
        ['asd', 'England', 'job1', 'test-admin@test.com', 'UK (44) 1234567890', 'kjhnkjhn87678AL'],
      ],
    ]);
  });
});
