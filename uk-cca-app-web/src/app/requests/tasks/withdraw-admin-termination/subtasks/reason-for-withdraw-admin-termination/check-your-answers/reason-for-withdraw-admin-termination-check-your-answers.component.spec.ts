import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockReasonForAdminTerminationWithdrawPayload } from '../../../testing/mock-data';
import ReasonForWithdrawAdminTerminationCheckYourAnswersComponent from './reason-for-withdraw-admin-termination-check-your-answers.component';

describe('ReasonForWithdrawAdminTerminationCheckYourAnswersComponent', () => {
  let component: ReasonForWithdrawAdminTerminationCheckYourAnswersComponent;
  let fixture: ComponentFixture<ReasonForWithdrawAdminTerminationCheckYourAnswersComponent>;
  let store: RequestTaskStore;

  const withdrawAdminTerminationTaskService: Partial<jest.Mocked<TaskService>> = {
    submitSubtask: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReasonForWithdrawAdminTerminationCheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TaskService, useValue: withdrawAdminTerminationTaskService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Withdraw admin termination' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'ADMIN_TERMINATION_APPLICATION_WITHDRAW' } });
    store.setPayload(mockReasonForAdminTerminationWithdrawPayload);

    fixture = TestBed.createComponent(ReasonForWithdrawAdminTerminationCheckYourAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show proper summary values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
