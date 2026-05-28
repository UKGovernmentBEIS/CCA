import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { RequestTaskFileService } from '@shared/services';
import { getByText } from '@testing';

import { NonComplianceConclusionSubmitRequestTaskPayload } from 'cca-api';

import { ProvideWithdrawalNoticeComponent } from './provide-withdrawal-notice.component';

describe('ProvideWithdrawalNoticeComponent', () => {
  let component: ProvideWithdrawalNoticeComponent;
  let fixture: ComponentFixture<ProvideWithdrawalNoticeComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = new ActivatedRouteStub({ taskId: '123' });

  const tasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  const requestTaskFileService = {
    buildFormControl: vi.fn().mockReturnValue(new FormControl(null)),
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
        penaltyOutcome: 'WITHDRAW',
      },
      withdrawNotice: null,
    },
    nonComplianceAttachments: { 'uuid-1': 'notice.pdf' },
    sectionsCompleted: { 'provide-conclusion': 'IN_PROGRESS' },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProvideWithdrawalNoticeComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
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

    fixture = TestBed.createComponent(ProvideWithdrawalNoticeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    tasksApiService.saveRequestTaskAction.mockClear();
    requestTaskFileService.buildFormControl.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the provide withdrawal notice step content', () => {
    expect(getByText('Provide withdrawal notice', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Upload file', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Comments', fixture.nativeElement)).toBeTruthy();
  });

  it('should save the withdrawal notice and navigate to check-your-answers', () => {
    component['form'].setValue({
      file: { uuid: 'uuid-1', file: { name: 'notice.pdf' } as File },
      comments: 'Withdrawal notice comments',
    });

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
            penaltyOutcome: 'WITHDRAW',
          },
          withdrawNotice: {
            file: 'uuid-1',
            comments: 'Withdrawal notice comments',
          },
        },
        sectionsCompleted: { 'provide-conclusion': 'IN_PROGRESS' },
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });
});
