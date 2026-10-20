import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { mockAdminTerminationWithdrawSubmittedRequestActionState } from '@requests/common';
import { getSummaryListData } from '@testing';

import { AdminTerminationWithdrawSubmittedTimelineComponent } from './admin-termination-withdraw-submitted-timeline.component';

describe('AdminTerminationWithdrawSubmittedTimelineComponent', () => {
  let component: AdminTerminationWithdrawSubmittedTimelineComponent;
  let fixture: ComponentFixture<AdminTerminationWithdrawSubmittedTimelineComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminTerminationWithdrawSubmittedTimelineComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(mockAdminTerminationWithdrawSubmittedRequestActionState);

    fixture = TestBed.createComponent(AdminTerminationWithdrawSubmittedTimelineComponent);
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
        ['Explain why you are withdrawing the admin termination', 'Uploaded files'],
        ['erhserseresrg', 'METS Project Retro.xlsx'],
      ],
      [
        ['Users', 'Name and signature on the official notice', 'Official notice'],
        [
          'oper3 user, Responsible person, oper3@cca.uka-fname lname, Administrative contact, test-admin@test.comFred_1 William_1, Sector contact, fredwilliam_1@agindustries.org.uk',
          'Regulator England',
          'Withdrawal of intent to terminate agreement.pdf',
        ],
      ],
    ]);
  });
});
