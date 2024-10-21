import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

import { mockAdminTerminationFinalDecisionRequestActionState } from '../../common/testing/mock-admin-termination-final-decision-request-action-state';
import { AdminTerminationFinalDecisionSubmittedTimelineComponent } from './admin-termination-final-decision-submitted-timeline.component';

describe('AdminTerminationFinalDecisionSubmittedTimelineComponent', () => {
  let component: AdminTerminationFinalDecisionSubmittedTimelineComponent;
  let fixture: ComponentFixture<AdminTerminationFinalDecisionSubmittedTimelineComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminTerminationFinalDecisionSubmittedTimelineComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(mockAdminTerminationFinalDecisionRequestActionState);

    fixture = TestBed.createComponent(AdminTerminationFinalDecisionSubmittedTimelineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
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
        ['Decision', 'Explain reason', 'Uploaded files'],
        ['Terminate agreement', 'asdsadas', 'No files provided'],
      ],
      [
        ['Users', 'Name and signature on the official notice', 'Official notice'],
        [
          'oper3 user, Responsible person, oper3@cca.uk  a-fname lname, Administrative contact, test-admin@test.com  Fred_1 William_1, Sector contact, fredwilliam_1@agindustries.org.uk',
          'Regulator England',
          'Admin Termination Regulatory reason notice.pdf',
        ],
      ],
    ]);
  });
});
