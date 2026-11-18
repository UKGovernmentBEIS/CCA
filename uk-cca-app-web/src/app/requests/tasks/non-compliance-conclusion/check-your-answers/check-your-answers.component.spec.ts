import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { getByText, queryByText } from '@testing';

import { NonComplianceConclusionSubmitRequestTaskPayload, RequestInfoDTO, RequestTaskDTO } from 'cca-api';

import { CheckYourAnswersComponent } from './check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let component: CheckYourAnswersComponent;
  let fixture: ComponentFixture<CheckYourAnswersComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = new ActivatedRouteStub({ taskId: '123' });

  const tasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  const initialPayload: NonComplianceConclusionSubmitRequestTaskPayload = {
    payloadType: 'NON_COMPLIANCE_CONCLUSION_SUBMIT_PAYLOAD',
    nonComplianceConclusion: {
      details: {
        complianceRestored: false,
        complianceRestoredDate: null,
        penaltyPaid: false,
        penaltyPaymentDate: null,
        comment: 'Some comments',
        penaltyOutcome: 'NONE',
      },
      withdrawNotice: null,
    },
    nonComplianceAttachments: {},
    sectionsCompleted: { 'provide-conclusion': 'IN_PROGRESS' },
  };

  const createComponent = (isEditable = true) => {
    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_CONCLUSION_SUBMIT',
        payload: initialPayload,
      } as RequestTaskDTO,
      requestInfo: { accountId: 1 } as RequestInfoDTO,
    });
    store.setIsEditable(isEditable);

    fixture = TestBed.createComponent(CheckYourAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Provide conclusion of non-compliance' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    tasksApiService.saveRequestTaskAction.mockClear();
  });

  it('should create', () => {
    createComponent();

    expect(component).toBeTruthy();
  });

  it('should render the check your answers page with confirm button for editable tasks', () => {
    createComponent();

    expect(getByText('Check your answers', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Confirm and complete', fixture.nativeElement)).toBeTruthy();
  });

  it('should save with COMPLETED status and navigate back to task page on submit', () => {
    createComponent();

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 123,
      requestTaskActionType: 'NON_COMPLIANCE_CONCLUSION_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_CONCLUSION_SAVE_PAYLOAD',
        nonComplianceConclusion: {
          details: {
            complianceRestored: false,
            complianceRestoredDate: null,
            penaltyPaid: false,
            penaltyPaymentDate: null,
            comment: 'Some comments',
            penaltyOutcome: 'NONE',
          },
          withdrawNotice: null,
        },
        sectionsCompleted: { 'provide-conclusion': 'COMPLETED' },
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../../'], { relativeTo: route, replaceUrl: true });
  });

  it('should hide the confirm button and not save when task is read only', () => {
    createComponent(false);

    expect(queryByText('Confirm and complete', fixture.nativeElement)).toBeNull();

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();
  });
});
