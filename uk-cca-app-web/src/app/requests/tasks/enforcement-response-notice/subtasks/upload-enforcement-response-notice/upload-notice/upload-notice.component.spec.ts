import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideZonelessChangeDetection } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { RequestTaskFileService } from '@shared/services';
import { getByText, queryByText } from '@testing';

import { NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload } from 'cca-api';

import UploadNoticeComponent from './upload-notice.component';

describe('UploadNoticeComponent', () => {
  let component: UploadNoticeComponent;
  let fixture: ComponentFixture<UploadNoticeComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = new ActivatedRouteStub({ taskId: '123' });

  const tasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  const requestTaskFileService = {
    buildFormControl: vi.fn().mockReturnValue(new FormControl(null)),
  };

  const initialPayload: NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload = {
    payloadType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PAYLOAD',
    enforcementResponseNotice: {
      type: 'PENALTY',
      file: 'uuid-1',
      comments: 'Existing comments',
    },
    nonComplianceAttachments: { 'uuid-1': 'existing-notice.pdf', 'uuid-2': 'updated-notice.pdf' },
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

    fixture = TestBed.createComponent(UploadNoticeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadNoticeComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideZonelessChangeDetection(),
        { provide: ActivatedRoute, useValue: route },
        {
          provide: Router,
          useValue: {
            navigate: vi.fn(),
            url: '/test/upload-notice',
            events: of({}),
            createUrlTree: () => ({}),
            serializeUrl: () => '',
          },
        },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Upload enforcement response notice' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);
    tasksApiService.saveRequestTaskAction.mockClear();
    requestTaskFileService.buildFormControl.mockClear();
  });

  it('should create', () => {
    createComponent();

    expect(component).toBeTruthy();
  });

  it('should render penalty waiver heading when type is PENALTY_WAIVER', () => {
    createComponent({
      ...initialPayload,
      enforcementResponseNotice: {
        ...initialPayload.enforcementResponseNotice,
        type: 'PENALTY_WAIVER',
      },
    });

    expect(getByText('Upload penalty waiver notice', fixture.nativeElement)).toBeTruthy();
  });

  it('should render penalty heading when type is PENALTY', () => {
    createComponent();

    expect(getByText('Upload penalty notice', fixture.nativeElement)).toBeTruthy();
  });

  it('should show reminder when penaltyReissue is true', () => {
    createComponent({
      ...initialPayload,
      penaltyReissue: true,
      enforcementResponseNotice: {
        ...initialPayload.enforcementResponseNotice,
        type: 'PENALTY_WAIVER',
      },
    });

    expect(
      getByText(
        'Remember to review your uploaded notice before proceeding with the "Notify Operator" action, as any referenced dates may no longer be current.',
        fixture.nativeElement,
      ),
    ).toBeTruthy();
  });

  it('should show reminder when type is PENALTY', () => {
    createComponent();

    expect(
      getByText(
        'Remember to review your uploaded notice before proceeding with the "Notify Operator" action, as any referenced dates may no longer be current.',
        fixture.nativeElement,
      ),
    ).toBeTruthy();
  });

  it('should hide reminder when type is PENALTY_WAIVER and penaltyReissue is false', () => {
    createComponent({
      ...initialPayload,
      enforcementResponseNotice: {
        ...initialPayload.enforcementResponseNotice,
        type: 'PENALTY_WAIVER',
      },
    });

    expect(
      queryByText(
        'Remember to review your uploaded notice before proceeding with the "Notify Operator" action, as any referenced dates may no longer be current.',
        fixture.nativeElement,
      ),
    ).toBeNull();
  });

  it('should save the updated notice and navigate to check your answers', () => {
    createComponent();

    component['form'].setValue({
      file: { uuid: 'uuid-2', file: { name: 'updated-notice.pdf' } as File },
      comments: 'Updated comments',
    });
    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 123,
      requestTaskActionType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_PAYLOAD',
        enforcementResponseNotice: {
          type: 'PENALTY',
          file: 'uuid-2',
          comments: 'Updated comments',
        },
        sectionsCompleted: {
          uploadEnforcementResponseNotice: 'IN_PROGRESS',
        },
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });

  it('should override type to PENALTY when penaltyReissue is true', () => {
    createComponent({
      ...initialPayload,
      penaltyReissue: true,
      enforcementResponseNotice: {
        type: 'PENALTY_WAIVER',
        file: 'uuid-1',
        comments: 'Existing comments',
      },
    });

    component['form'].setValue({
      file: { uuid: 'uuid-2', file: { name: 'updated-notice.pdf' } as File },
      comments: 'Updated comments',
    });
    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 123,
      requestTaskActionType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_PAYLOAD',
        enforcementResponseNotice: {
          type: 'PENALTY',
          file: 'uuid-2',
          comments: 'Updated comments',
        },
        sectionsCompleted: {
          uploadEnforcementResponseNotice: 'IN_PROGRESS',
        },
      },
    });
  });
});
