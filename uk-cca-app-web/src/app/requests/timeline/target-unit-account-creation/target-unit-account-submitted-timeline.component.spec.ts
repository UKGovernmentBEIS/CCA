import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

import { mockTargetUnitAccountRequestActionState } from './mocks/mock-target-unit-account-request-action-state';
import { TargetUnitAccountSubmittedTimelineComponent } from './target-unit-account-submitted-timeline.component';

describe('TargetUnitAccountSubmittedTimelineComponent', () => {
  let component: TargetUnitAccountSubmittedTimelineComponent;
  let fixture: ComponentFixture<TargetUnitAccountSubmittedTimelineComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TargetUnitAccountSubmittedTimelineComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
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
    expect(screen.getByTestId('target-unit-details-list')).toBeInTheDocument();
    expect(screen.getByTestId('operator-address-list')).toBeInTheDocument();
    expect(screen.getByTestId('responsible-person-list')).toBeInTheDocument();
    expect(screen.getByTestId('administrative-contact-list')).toBeInTheDocument();
  });

  it('should display the correct data', () => {
    const summaryValues = screen
      .getAllByText((_, el) => el.tagName.toLowerCase() === 'dl')
      .map((el) => [
        Array.from(el.querySelectorAll('dt')).map((dt) => dt.textContent.trim()),
        Array.from(el.querySelectorAll('dd'))
          .filter((dt) => dt.textContent.trim() !== 'Change')
          .map((dt) => dt.textContent.trim()),
      ]);

    expect(summaryValues).toEqual([
      [
        ['Operator name', 'Operator type', 'Company Registration Number', 'Nature of business (SIC) code', 'Subsector'],
        ['iojasdoiajsdoijas', 'Limited company', '123456789', '', ''],
      ],
      [['Address'], ['kjhn  kjhn  87678  AL']],
      [
        ['First name', 'Last name', 'Job title', 'Address', 'Phone number', 'Email address'],
        ['asdasdsa', 'lname', 'job', 'kjhn  kjhn  87678  AL', 'UK (44) 1234567890', 'test-test@cca.uk'],
      ],
      [
        ['First name', 'Last name', 'Job title', 'Email address', 'Phone number', 'Address'],
        ['asd', 'England', 'job1', 'test-admin@test.com', 'UK (44) 1234567890', 'kjhn  kjhn  87678  AL'],
      ],
    ]);
  });
});
