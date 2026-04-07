import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideZonelessChangeDetection } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { getByText } from '@testing';

import {
  CaExternalContactsService,
  NonComplianceNoticeOfIntentSubmitRequestTaskPayload,
  NoticeRecipientsService,
  RegulatorAuthoritiesService,
  TasksService,
} from 'cca-api';

import {
  mockAdminTerminationNotifyOperatorAdditionalUsers,
  mockAdminTerminationNotifyOperatorDefaultUsers,
  mockAdminTerminationNotifyOperatorExternalContacts,
  mockAdminTerminationNotifyOperatorRegulatorAuthorities,
} from '../../admin-termination/testing/mock-data';
import NoticeOfIntentNotifyOperatorComponent from './notice-of-intent-notify-operator.component';

describe('NoticeOfIntentNotifyOperatorComponent', () => {
  let component: NoticeOfIntentNotifyOperatorComponent;
  let fixture: ComponentFixture<NoticeOfIntentNotifyOperatorComponent>;
  let store: RequestTaskStore;

  const tasksApiService: Partial<jest.Mocked<TasksApiService>> = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoticeOfIntentNotifyOperatorComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideZonelessChangeDetection(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: TasksService, useValue: tasksService },
        { provide: NoticeRecipientsService, useValue: noticeRecipientsService },
        { provide: CaExternalContactsService, useValue: caExternalContactsService },
        { provide: RegulatorAuthoritiesService, useValue: regulatorAuthoritiesService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Upload notice of intent' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({
      requestTask: { type: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT' },
      requestInfo: { accountId: 1 },
    });
    store.setPayload({
      payloadType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PAYLOAD',
      noticeOfIntent: { noticeOfIntentFile: 'uuid-1', comments: 'Please review' },
      sectionsCompleted: { uploadNoticeOfIntent: 'COMPLETED' },
      nonComplianceAttachments: { 'uuid-1': 'notice.pdf' },
    } as NonComplianceNoticeOfIntentSubmitRequestTaskPayload);

    fixture = TestBed.createComponent(NoticeOfIntentNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(getByText('Notify operator of decision', fixture.nativeElement)).toBeTruthy();
    expect(
      getByText('Select who should receive the notice of intent notification', fixture.nativeElement),
    ).toBeTruthy();
  });

  it('should contain submit button and return link text', () => {
    expect(getByText('Confirm and complete', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Return to: Upload notice of intent', fixture.nativeElement)).toBeTruthy();
  });

  it('should render the uploaded notice review warning as inset text', () => {
    const insetText = fixture.nativeElement.querySelector('.govuk-inset-text');

    expect(insetText).toBeTruthy();
    expect(
      getByText('Make sure to review your uploaded notice before proceeding.', fixture.nativeElement),
    ).toBeTruthy();
    expect(getByText('Referenced dates may be outdated', insetText)).toBeTruthy();
    expect(getByText('Ensure all details are current', insetText)).toBeTruthy();
  });
});
