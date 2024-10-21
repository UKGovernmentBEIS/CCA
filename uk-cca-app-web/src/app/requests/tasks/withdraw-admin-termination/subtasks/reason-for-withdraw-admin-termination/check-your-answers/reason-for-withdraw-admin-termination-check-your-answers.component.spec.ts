import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { mockReasonForAdminTerminationWithdrawPayload } from '../../../mocks/mock-withdraw-admin-termination-payload';
import ReasonForWithdrawAdminTerminationCheckYourAnswersComponent from './reason-for-withdraw-admin-termination-check-your-answers.component';

describe('ReasonForWithdrawAdminTerminationCheckYourAnswersComponent', () => {
  let component: ReasonForWithdrawAdminTerminationCheckYourAnswersComponent;
  let fixture: ComponentFixture<ReasonForWithdrawAdminTerminationCheckYourAnswersComponent>;
  let store: RequestTaskStore;

  const withdrawAdminTerminationTaskService: Partial<jest.Mocked<TaskService>> = {
    submitSubtask: jest.fn().mockReturnValue(of({})),
  };

  const submitSubtaskSpy = jest.spyOn(withdrawAdminTerminationTaskService, 'submitSubtask');

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
    store.setRequestTaskItem({ requestTask: { type: 'ADMIN_TERMINATION_APPLICATION_WITHDRAW' as any } });
    store.setPayload(mockReasonForAdminTerminationWithdrawPayload);

    fixture = TestBed.createComponent(ReasonForWithdrawAdminTerminationCheckYourAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(screen.getByText('Withdraw admin termination')).toBeInTheDocument();
    expect(screen.getByText('Check your answers')).toBeInTheDocument();
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
        ['Explain why you are withdrawing the admin termination', 'Uploaded files'],
        ['mplah mplah', 'No files provided'],
      ],
    ]);
  });

  it('should contain submit button and "return to" link', () => {
    expect(screen.getByText('Confirm and complete')).toBeInTheDocument();
    expect(screen.getByText('Return to: Withdraw admin termination')).toBeInTheDocument();
  });

  it('should submit form and call "submitSubtaskSpy" method', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Confirm and complete'));
    expect(submitSubtaskSpy).toHaveBeenCalledTimes(1);
  });
});
