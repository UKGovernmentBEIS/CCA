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
import { Mocked } from 'vitest';

import {
  CaExternalContactsService,
  NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload,
  RegulatorAuthoritiesService,
  TasksService,
} from 'cca-api';

import {
  mockAdminTerminationNotifyOperatorAdditionalUsers,
  mockAdminTerminationNotifyOperatorDefaultUsers,
  mockAdminTerminationNotifyOperatorExternalContacts,
  mockAdminTerminationNotifyOperatorRegulatorAuthorities,
} from '../../admin-termination/testing/mock-data';
import EnforcementResponseNoticeNotifyOperatorComponent from './enforcement-response-notice-notify-operator.component';

describe('EnforcementResponseNoticeNotifyOperatorComponent', () => {
  let component: EnforcementResponseNoticeNotifyOperatorComponent;
  let fixture: ComponentFixture<EnforcementResponseNoticeNotifyOperatorComponent>;
  let store: RequestTaskStore;

  const tasksApiService: Partial<Mocked<TasksApiService>> = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  const tasksService: Partial<Mocked<TasksService>> = {
    getDefaultNoticeRecipients: vi.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorDefaultUsers)),
    getAdditionalNoticeRecipients: vi.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorAdditionalUsers)),
  };

  const caExternalContactsService: Partial<Mocked<CaExternalContactsService>> = {
    getCaExternalContacts: vi.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorExternalContacts)),
  };

  const regulatorAuthoritiesService: Partial<Mocked<RegulatorAuthoritiesService>> = {
    getCaRegulators: vi.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorRegulatorAuthorities)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnforcementResponseNoticeNotifyOperatorComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideZonelessChangeDetection(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: TasksService, useValue: tasksService },
        { provide: CaExternalContactsService, useValue: caExternalContactsService },
        { provide: RegulatorAuthoritiesService, useValue: regulatorAuthoritiesService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Upload enforcement response notice' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({
      requestTask: { type: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT' },
      requestInfo: { accountId: 1 } as any,
    });
    store.setPayload({
      payloadType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PAYLOAD',
      enforcementResponseNotice: { type: 'PENALTY', file: 'uuid-1', comments: 'Please review' },
      sectionsCompleted: { uploadEnforcementResponseNotice: 'COMPLETED' },
      nonComplianceAttachments: { 'uuid-1': 'notice.pdf' },
    } as NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload);

    fixture = TestBed.createComponent(EnforcementResponseNoticeNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(getByText('Notify operator of decision', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Select who should receive the enforcement response notice', fixture.nativeElement)).toBeTruthy();
  });

  it('should contain submit button and return link text', () => {
    expect(getByText('Confirm and complete', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Return to: Upload enforcement response notice', fixture.nativeElement)).toBeTruthy();
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
