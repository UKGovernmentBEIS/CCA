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
  mockAdminTerminationNotifyOperatorAdditionalUsers,
  mockAdminTerminationNotifyOperatorDefaultUsers,
  mockAdminTerminationNotifyOperatorExternalContacts,
  mockAdminTerminationNotifyOperatorRegulatorAuthorities,
  mockReasonForAdminTerminationPayload,
} from '../mocks/mock-admin-termination-payload';
import { AdminTerminationTaskService } from '../services/admin-termination-task.service';
import AdminTerminationNotifyOperatorComponent from './admin-termination-notify-operator.component';

describe('AdminTerminationNotifyOperatorComponent', () => {
  let component: AdminTerminationNotifyOperatorComponent;
  let fixture: ComponentFixture<AdminTerminationNotifyOperatorComponent>;
  let store: RequestTaskStore;

  const taskService: Partial<jest.Mocked<AdminTerminationTaskService>> = {
    notifyOperator: jest.fn().mockReturnValue(of({})),
  };

  const tasksService: Partial<jest.Mocked<TasksService>> = {
    getDefaultNoticeRecipients: jest.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorDefaultUsers)),
  };

  const noticeRecipientsService: Partial<jest.Mocked<NoticeRecipientsService>> = {
    getAdditionalNoticeRecipients: jest.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorAdditionalUsers)),
  };

  const caExternalContactsService: Partial<jest.Mocked<CaExternalContactsService>> = {
    getCaExternalContacts: jest.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorExternalContacts)),
  };

  const regulatorAuthoritiesService: Partial<jest.Mocked<RegulatorAuthoritiesService>> = {
    getCaRegulators: jest.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorRegulatorAuthorities)),
  };

  const notifyOperatorSpy = jest.spyOn(taskService, 'notifyOperator');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminTerminationNotifyOperatorComponent],
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
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Admin termination' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({
      requestTask: { type: 'ADMIN_TERMINATION_APPLICATION_SUBMIT' as any },
      requestInfo: { accountId: 1 },
    });
    store.setPayload(mockReasonForAdminTerminationPayload);

    fixture = TestBed.createComponent(AdminTerminationNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(screen.getByText('Notify operator of decision')).toBeInTheDocument();
    expect(screen.getByText('Select who should receive the termination notification')).toBeInTheDocument();
  });

  it('should contain submit button and "return to" link', () => {
    expect(screen.getByText('Confirm and complete')).toBeInTheDocument();
    expect(screen.getByText('Return to: Admin termination')).toBeInTheDocument();
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

    expect(notifyOperatorSpy).toHaveBeenCalledTimes(1);
    expect(notifyOperatorSpy).toHaveBeenCalledWith({
      externalContacts: [1],
      operators: [],
      sectorUsers: ['sec-id2'],
      signatory: 'reg-userid',
    });
  });
});
