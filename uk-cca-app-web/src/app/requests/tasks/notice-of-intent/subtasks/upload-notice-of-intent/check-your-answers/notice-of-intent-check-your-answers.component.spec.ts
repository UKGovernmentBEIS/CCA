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

import { NonComplianceNoticeOfIntentSubmitRequestTaskPayload } from 'cca-api';

import NoticeOfIntentCheckYourAnswersComponent from './notice-of-intent-check-your-answers.component';

describe('NoticeOfIntentCheckYourAnswersComponent', () => {
  let component: NoticeOfIntentCheckYourAnswersComponent;
  let fixture: ComponentFixture<NoticeOfIntentCheckYourAnswersComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = new ActivatedRouteStub({ taskId: '123' });

  const tasksApiService = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  const initialPayload: NonComplianceNoticeOfIntentSubmitRequestTaskPayload = {
    payloadType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PAYLOAD',
    noticeOfIntent: { noticeOfIntentFile: 'uuid-1', comments: 'Existing comments' },
    nonComplianceAttachments: { 'uuid-1': 'existing-notice.pdf' },
    sectionsCompleted: { uploadNoticeOfIntent: 'IN_PROGRESS' },
  };

  const createComponent = (isEditable = true) => {
    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT',
        payload: initialPayload,
      } as any,
      requestInfo: { accountId: 1 } as any,
    });
    store.setIsEditable(isEditable);

    fixture = TestBed.createComponent(NoticeOfIntentCheckYourAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoticeOfIntentCheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideZonelessChangeDetection(),
        { provide: ActivatedRoute, useValue: route },
        {
          provide: Router,
          useValue: {
            navigate: jest.fn(),
            url: '/test/check-your-answers',
            events: of({}),
            createUrlTree: () => ({}),
            serializeUrl: () => '',
          },
        },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Upload notice of intent' },
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

  it('should render the final check your answers step for editable tasks', () => {
    createComponent();

    expect(getByText('Check your answers', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Confirm and complete', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Return to: Upload notice of intent', fixture.nativeElement)).toBeTruthy();
  });

  it('should save the completed section and navigate back to the task page', () => {
    createComponent();

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 123,
      requestTaskActionType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_PAYLOAD',
        noticeOfIntent: {
          noticeOfIntentFile: 'uuid-1',
          comments: 'Existing comments',
        },
        sectionsCompleted: {
          uploadNoticeOfIntent: 'COMPLETED',
        },
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../../..'], { relativeTo: route, replaceUrl: true });
  });

  it('should hide the confirm button and avoid saving when the task is read only', () => {
    createComponent(false);

    expect(queryByText('Confirm and complete', fixture.nativeElement)).toBeNull();

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();
  });
});
