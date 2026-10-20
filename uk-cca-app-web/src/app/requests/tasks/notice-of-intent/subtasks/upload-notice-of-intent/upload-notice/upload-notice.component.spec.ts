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
import { getByText } from '@testing';

import { NonComplianceNoticeOfIntentSubmitRequestTaskPayload, RequestTaskItemDTO } from 'cca-api';

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

  const initialPayload: NonComplianceNoticeOfIntentSubmitRequestTaskPayload = {
    payloadType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PAYLOAD',
    noticeOfIntent: { file: 'uuid-1', comments: 'Existing comments' },
    nonComplianceAttachments: { 'uuid-1': 'existing-notice.pdf', 'uuid-2': 'updated-notice.pdf' },
    sectionsCompleted: {},
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
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Upload notice of intent' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);
    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT',
        payload: initialPayload,
      },
      requestInfo: { accountId: 1 },
    } as RequestTaskItemDTO);
    store.setIsEditable(true);

    fixture = TestBed.createComponent(UploadNoticeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    tasksApiService.saveRequestTaskAction.mockClear();
    requestTaskFileService.buildFormControl.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the upload step content', () => {
    expect(getByText('Upload notice', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Upload the notice of intent to be sent to the operator.', fixture.nativeElement)).toBeTruthy();
    expect(
      getByText(
        'Remember to review your uploaded notice before proceeding with the "Notify Operator" action, as any referenced dates may no longer be current.',
        fixture.nativeElement,
      ),
    ).toBeTruthy();
    expect(getByText('Return to: Upload notice of intent', fixture.nativeElement)).toBeTruthy();
  });

  it('should save the updated notice and navigate to check your answers', () => {
    component['form'].setValue({
      file: { uuid: 'uuid-2', file: { name: 'updated-notice.pdf' } as File },
      comments: 'Updated comments',
    });

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 123,
      requestTaskActionType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_PAYLOAD',
        noticeOfIntent: {
          file: 'uuid-2',
          comments: 'Updated comments',
        },
        sectionsCompleted: {
          uploadNoticeOfIntent: 'IN_PROGRESS',
        },
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });
});
