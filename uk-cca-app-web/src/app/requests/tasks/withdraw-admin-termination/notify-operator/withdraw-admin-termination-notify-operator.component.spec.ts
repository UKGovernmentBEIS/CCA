import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

import { CaExternalContactsService, NoticeRecipientsService, RegulatorAuthoritiesService, TasksService } from 'cca-api';

import {
  mockReasonForAdminTerminationWithdrawPayload,
  mockWithdrawAdminTerminationNotifyOperatorAdditionalUsers,
  mockWithdrawAdminTerminationNotifyOperatorDefaultUsers,
  mockWithdrawAdminTerminationNotifyOperatorExternalContacts,
  mockWithdrawAdminTerminationNotifyOperatorRegulatorAuthorities,
} from '../testing/mock-data';
import WithdrawAdminTerminationNotifyOperatorComponent from './withdraw-admin-termination-notify-operator.component';

describe('WithdrawAdminTerminationNotifyOperatorComponent', () => {
  let component: WithdrawAdminTerminationNotifyOperatorComponent;
  let fixture: ComponentFixture<WithdrawAdminTerminationNotifyOperatorComponent>;
  let store: RequestTaskStore;

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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WithdrawAdminTerminationNotifyOperatorComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
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
    expect(getByText('Notify operator of decision', fixture.nativeElement)).toBeTruthy();
    expect(
      getByText('Select who should receive the admin termination withdrawal notice', fixture.nativeElement),
    ).toBeTruthy();
  });

  it('should contain submit button and "return to" link', () => {
    expect(getByText('Confirm and complete', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Return to: Withdraw admin termination', fixture.nativeElement)).toBeTruthy();
  });

  it('should display the correct form fields', () => {
    expect(getByText('Select any additional users you want to notify', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Select the external contacts you want to notify', fixture.nativeElement)).toBeTruthy();
    expect(
      getByText(
        'Select the name and signature that will be shown on the official notice document',
        fixture.nativeElement,
      ),
    ).toBeTruthy();
  });
});
