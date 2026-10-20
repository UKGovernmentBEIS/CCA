import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { NonComplianceConclusionSubmittedComponent } from './non-compliance-conclusion-submitted.component';
import {
  nonComplianceConclusionSubmittedActionStateMock,
  nonComplianceConclusionWithdrawSubmittedPayload,
} from './tests/mock-data';

describe('NonComplianceConclusionSubmittedComponent', () => {
  let component: NonComplianceConclusionSubmittedComponent;
  let fixture: ComponentFixture<NonComplianceConclusionSubmittedComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonComplianceConclusionSubmittedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(nonComplianceConclusionSubmittedActionStateMock);

    fixture = TestBed.createComponent(NonComplianceConclusionSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct data', () => {
    const summaryValues = getSummaryListData(fixture.nativeElement);

    expect(summaryValues).toEqual([
      [
        [
          'Has compliance been restored?',
          'When did the operator become compliant?',
          'Has the operator paid the penalty?',
          'When did the operator pay?',
          'Your comments on the status of compliance',
          'Would you like to reissue or withdraw the penalty?',
        ],
        ['Yes', '02 Mar 2025', 'Yes', '02 Mar 2025', 'A Martini. Shaken, Not Stirred.', 'None of the above'],
      ],
    ]);
  });

  it('should display recipients for a withdrawal conclusion', () => {
    actionStore.setState({
      action: {
        ...nonComplianceConclusionSubmittedActionStateMock.action,
        payload: nonComplianceConclusionWithdrawSubmittedPayload,
      },
    });
    fixture.detectChanges();

    const summaryValues = getSummaryListData(fixture.nativeElement);

    expect(summaryValues).toEqual([
      [
        [
          'Has compliance been restored?',
          'When did the operator become compliant?',
          'Has the operator paid the penalty?',
          'When did the operator pay?',
          'Your comments on the status of compliance',
          'Would you like to reissue or withdraw the penalty?',
        ],
        ['Yes', '02 Mar 2025', 'Yes', '02 Mar 2025', 'A Martini. Shaken, Not Stirred.', 'Withdraw'],
      ],
      [
        ['Upload file', 'Comments'],
        ['withdrawal-notice.pdf', 'Withdrawal notice comments'],
      ],
      [
        ['Users notified'],
        [
          'John William, Responsible person, williamsj@abc.comMatthew Johnson, Administrative contact, mjohnson@def.comAlex Turner, Operator user',
        ],
      ],
    ]);
  });
});
