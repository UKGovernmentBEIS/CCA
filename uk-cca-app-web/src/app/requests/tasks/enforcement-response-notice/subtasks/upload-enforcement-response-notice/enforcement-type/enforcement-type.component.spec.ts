import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideZonelessChangeDetection } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { getByText, queryByText } from '@testing';

import { NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload } from 'cca-api';

import EnforcementTypeComponent from './enforcement-type.component';

describe('EnforcementTypeComponent', () => {
  let component: EnforcementTypeComponent;
  let fixture: ComponentFixture<EnforcementTypeComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = new ActivatedRouteStub({ taskId: '123' });

  const tasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  const initialPayload: NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload = {
    payloadType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PAYLOAD',
    enforcementResponseNotice: {
      comments: 'Existing comments',
      type: 'PENALTY',
      file: null,
    },
    sectionsCompleted: {},
  };

  const createComponent = (
    payload: NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload = initialPayload,
  ) => {
    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT',
        payload,
      } as any,
      requestInfo: { accountId: 1 } as any,
    });
    store.setIsEditable(true);

    fixture = TestBed.createComponent(EnforcementTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnforcementTypeComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideZonelessChangeDetection(),
        { provide: ActivatedRoute, useValue: route },
        {
          provide: Router,
          useValue: {
            navigate: vi.fn(),
            url: '/test/enforcement-type',
            events: of({}),
            createUrlTree: () => ({}),
            serializeUrl: () => '',
          },
        },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Upload enforcement response notice' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);
    tasksApiService.saveRequestTaskAction.mockClear();
  });

  it('should create', () => {
    createComponent();

    expect(component).toBeTruthy();
  });

  it('should render both radio options when penaltyReissue is false', () => {
    createComponent();

    expect(getByText('Penalty notice', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Penalty waiver notice', fixture.nativeElement)).toBeTruthy();
  });

  it('should hide PENALTY_WAIVER option when penaltyReissue is true', () => {
    createComponent({
      ...initialPayload,
      penaltyReissue: true,
    });

    expect(getByText('Penalty notice', fixture.nativeElement)).toBeTruthy();
    expect(queryByText('Penalty waiver notice', fixture.nativeElement)).toBeNull();
  });

  it('should save the selected type and navigate to upload notice when there is no existing file', () => {
    createComponent();

    component['form']().setValue({ type: 'PENALTY' });
    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 123,
      requestTaskActionType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_PAYLOAD',
        enforcementResponseNotice: {
          comments: 'Existing comments',
          type: 'PENALTY',
          file: null,
        },
        sectionsCompleted: {
          uploadEnforcementResponseNotice: 'IN_PROGRESS',
        },
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../upload-notice'], { relativeTo: route });
  });

  it('should navigate to check your answers when the payload already has a file', () => {
    createComponent({
      ...initialPayload,
      enforcementResponseNotice: {
        type: 'PENALTY_WAIVER',
        file: 'uuid-1',
        comments: 'Existing comments',
      },
    });

    component['form']().setValue({ type: 'PENALTY_WAIVER' });
    component.onSubmit();

    expect(router.navigate).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });

  it('should preserve other enforcement response notice fields in the saved payload', () => {
    createComponent({
      ...initialPayload,
      enforcementResponseNotice: {
        type: 'PENALTY_WAIVER',
        file: 'uuid-1',
        comments: 'Existing comments',
      },
      nonComplianceAttachments: { 'uuid-1': 'existing-notice.pdf' },
    });

    component['form']().setValue({ type: 'PENALTY' });
    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 123,
      requestTaskActionType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_PAYLOAD',
        enforcementResponseNotice: {
          type: 'PENALTY',
          file: 'uuid-1',
          comments: 'Existing comments',
        },
        sectionsCompleted: {
          uploadEnforcementResponseNotice: 'IN_PROGRESS',
        },
      },
    });
  });
});
