import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { getByText } from '@testing';

import {
  CaExternalContactsService,
  NonComplianceConclusionSubmitRequestTaskPayload,
  RegulatorAuthoritiesService,
  RequestInfoDTO,
  RequestTaskDTO,
  TasksService,
} from 'cca-api';

import {
  mockAdminTerminationNotifyOperatorAdditionalUsers,
  mockAdminTerminationNotifyOperatorDefaultUsers,
  mockAdminTerminationNotifyOperatorExternalContacts,
  mockAdminTerminationNotifyOperatorRegulatorAuthorities,
} from '../../admin-termination/testing/mock-data';
import { ConclusionNotifyOperatorComponent } from './notify-operator.component';

describe('ConclusionNotifyOperatorComponent', () => {
  let component: ConclusionNotifyOperatorComponent;
  let fixture: ComponentFixture<ConclusionNotifyOperatorComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = new ActivatedRouteStub({ taskId: '123' });

  const tasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  const tasksService = {
    getDefaultNoticeRecipients: vi.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorDefaultUsers)),
    getAdditionalNoticeRecipients: vi.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorAdditionalUsers)),
  };

  const caExternalContactsService = {
    getCaExternalContacts: vi.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorExternalContacts)),
  };

  const regulatorAuthoritiesService = {
    getCaRegulators: vi.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorRegulatorAuthorities)),
  };

  const withdrawPayload: NonComplianceConclusionSubmitRequestTaskPayload = {
    payloadType: 'NON_COMPLIANCE_CONCLUSION_SUBMIT_PAYLOAD',
    nonComplianceConclusion: {
      details: {
        complianceRestored: true,
        complianceRestoredDate: '2024-04-01',
        penaltyPaid: true,
        penaltyPaymentDate: '2024-04-02',
        comment: 'All good',
        penaltyOutcome: 'WITHDRAW',
      },
      withdrawNotice: { file: 'uuid-1', comments: 'Notice comments' },
    },
    nonComplianceAttachments: { 'uuid-1': 'notice.pdf' },
    sectionsCompleted: { 'provide-conclusion': 'COMPLETED' },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConclusionNotifyOperatorComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: TasksService, useValue: tasksService },
        { provide: CaExternalContactsService, useValue: caExternalContactsService },
        { provide: RegulatorAuthoritiesService, useValue: regulatorAuthoritiesService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Provide non-compliance conclusion' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_CONCLUSION_SUBMIT',
        payload: withdrawPayload,
      } as RequestTaskDTO,
      requestInfo: { accountId: 1 } as RequestInfoDTO,
    });
    store.setIsEditable(true);

    fixture = TestBed.createComponent(ConclusionNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    tasksApiService.saveRequestTaskAction.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the withdrawal heading and submit button', () => {
    expect(getByText('Notify operator of decision', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Select who should receive the withdrawal notice', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Confirm and complete', fixture.nativeElement)).toBeTruthy();
  });

  it('should submit the notification and navigate to the confirmation page', () => {
    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith(
      expect.objectContaining({
        requestTaskId: 123,
        requestTaskActionType: 'NON_COMPLIANCE_CONCLUSION_NOTIFY_OPERATOR',
        requestTaskActionPayload: expect.objectContaining({
          payloadType: 'NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
        }),
      }),
    );
    expect(router.navigate).toHaveBeenCalledWith(['./confirmation'], { relativeTo: route, replaceUrl: true });
  });
});
