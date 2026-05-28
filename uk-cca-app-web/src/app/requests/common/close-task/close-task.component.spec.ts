import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { RequestTaskFileService } from '@shared/services';
import { getByText } from '@testing';

import { NonComplianceConclusionSubmitRequestTaskPayload } from 'cca-api';

import { TasksApiService } from '../tasks-api.service';
import { CloseTaskComponent } from './close-task.component';

describe('CloseTaskComponent', () => {
  let component: CloseTaskComponent;
  let fixture: ComponentFixture<CloseTaskComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = new ActivatedRouteStub({ taskId: '123' });

  const tasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  const requestTaskFileService = {
    buildFormControl: vi.fn().mockReturnValue(new FormControl([])),
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CloseTaskComponent],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Provide conclusion of non-compliance' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_CONCLUSION_SUBMIT',
        payload: initialPayload,
      } as any,
      requestInfo: { accountId: 1 } as any,
    });
    store.setIsEditable(true);

    fixture = TestBed.createComponent(CloseTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    tasksApiService.saveRequestTaskAction.mockClear();
    requestTaskFileService.buildFormControl.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the close task content', () => {
    expect(getByText('Close task', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Explain why you have decided to close this task', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Upload supporting documents (optional)', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Are you sure you want to close this task?', fixture.nativeElement)).toBeTruthy();
  });

  it('should require a close explanation', () => {
    component['form'].controls.reason.setValue('');

    expect(component['form'].controls.reason.errors?.required).toEqual('You must provide an explanation');
  });

  it('should close the task and navigate to confirmation', () => {
    component['form'].setValue({
      reason: 'There is nothing left to complete.',
      files: [{ uuid: 'uuid-1', file: { name: 'close-task.pdf' } as File }],
    });

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 123,
      requestTaskActionType: 'NON_COMPLIANCE_CLOSE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_CLOSE_TASK_PAYLOAD',
        closeJustification: {
          reason: 'There is nothing left to complete.',
          files: ['uuid-1'],
        },
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../close-task-confirmation'], {
      relativeTo: route,
      replaceUrl: true,
    });
  });
});
