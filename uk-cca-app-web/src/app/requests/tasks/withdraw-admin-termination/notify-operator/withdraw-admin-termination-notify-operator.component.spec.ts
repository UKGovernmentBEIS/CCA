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

import { CaExternalContactsService, NoticeRecipientsService, RegulatorAuthoritiesService, TasksService } from 'cca-api';

import {
  mockReasonForAdminTerminationWithdrawPayload,
  mockWithdrawAdminTerminationNotifyOperatorAdditionalUsers,
  mockWithdrawAdminTerminationNotifyOperatorDefaultUsers,
  mockWithdrawAdminTerminationNotifyOperatorExternalContacts,
  mockWithdrawAdminTerminationNotifyOperatorRegulatorAuthorities,
} from '../mocks/mock-withdraw-admin-termination-payload';
import { WithdrawAdminTerminationTaskService } from '../services/withdraw-admin-termination-task.service';
import WithdrawAdminTerminationNotifyOperatorComponent from './withdraw-admin-termination-notify-operator.component';

describe('WithdrawAdminTerminationNotifyOperatorComponent', () => {
  let component: WithdrawAdminTerminationNotifyOperatorComponent;
  let fixture: ComponentFixture<WithdrawAdminTerminationNotifyOperatorComponent>;
  let store: RequestTaskStore;

  const taskService: Partial<jest.Mocked<WithdrawAdminTerminationTaskService>> = {
    notifyOperator: jest.fn().mockReturnValue(of({})),
  };

  const tasksService: Partial<jest.Mocked<TasksService>> = {
    getDefaultNoticeRecipients: jest.fn().mockReturnValue(of(mockWithdrawAdminTerminationNotifyOperatorDefaultUsers)),
  };

  const noticeRecipientsService: Partial<jest.Mocked<NoticeRecipientsService>> = {
    getAdditionalNoticeRecipients: jest
      .fn()
      .mockReturnValue(of(mockWithdrawAdminTerminationNotifyOperatorAdditionalUsers)),
  };

  const caExternalContactsService: Partial<jest.Mocked<CaExternalContactsService>> = {
    getCaExternalContacts: jest.fn().mockReturnValue(of(mockWithdrawAdminTerminationNotifyOperatorExternalContacts)),
  };

  const regulatorAuthoritiesService: Partial<jest.Mocked<RegulatorAuthoritiesService>> = {
    getCaRegulators: jest.fn().mockReturnValue(of(mockWithdrawAdminTerminationNotifyOperatorRegulatorAuthorities)),
  };

  const taskServiceSpy = jest.spyOn(taskService, 'notifyOperator');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WithdrawAdminTerminationNotifyOperatorComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TaskService, useValue: taskService },
        { provide: TasksService, useValue: tasksService },
        { provide: NoticeRecipientsService, useValue: noticeRecipientsService },
        { provide: CaExternalContactsService, useValue: caExternalContactsService },
        { provide: RegulatorAuthoritiesService, useValue: regulatorAuthoritiesService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Withdraw admin termination' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'ADMIN_TERMINATION_APPLICATION_WITHDRAW' as any } });
    store.setPayload(mockReasonForAdminTerminationWithdrawPayload);

    fixture = TestBed.createComponent(WithdrawAdminTerminationNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(screen.getByText('Notify operator of decision')).toBeInTheDocument();
    expect(screen.getByText('Select who should receive the admin termination withdrawal notice')).toBeInTheDocument();
  });

  it('should contain submit button and "return to" link', () => {
    expect(screen.getByText('Confirm and complete')).toBeInTheDocument();
    expect(screen.getByText('Return to: Withdraw admin termination')).toBeInTheDocument();
  });

  it('should display the correct form fields', () => {
    expect(screen.getByText('Select any additional users you want to notify')).toBeInTheDocument();
    expect(screen.getByText('Select the external contacts you want to notify')).toBeInTheDocument();
    expect(
      screen.getByText('Select the name and signature that will be shown on the official notice document'),
    ).toBeInTheDocument();
  });

  it('should submit form and call "processRequestTaskActionSpy" method', async () => {
    const user = UserEvent.setup();

    await user.click(screen.getByLabelText('fname2 lname2, Sector user, test-add@example.com'));
    await user.click(screen.getByLabelText('ext-cont@cca.uk'));

    const select = screen.getByLabelText(
      'Select the name and signature that will be shown on the official notice document',
    );
    await user.selectOptions(select, '0: reg-userid');

    await user.click(screen.getByText('Confirm and complete'));

    expect(taskServiceSpy).toHaveBeenCalledTimes(1);
    expect(taskServiceSpy).toHaveBeenCalledWith({
      externalContacts: [1],
      operators: [],
      sectorUsers: ['sec-id2'],
      signatory: 'reg-userid',
    });
  });
});
