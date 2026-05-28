import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { getByText } from '@testing';

import { NonComplianceConclusionSubmitRequestTaskPayload } from 'cca-api';

import { ProvideDetailsComponent } from './provide-details.component';

describe('ProvideDetailsComponent', () => {
  let component: ProvideDetailsComponent;
  let fixture: ComponentFixture<ProvideDetailsComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = new ActivatedRouteStub({ taskId: '123' });

  const tasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  const initialPayload: NonComplianceConclusionSubmitRequestTaskPayload = {
    payloadType: 'NON_COMPLIANCE_CONCLUSION_SUBMIT_PAYLOAD',
    nonComplianceConclusion: null,
    nonComplianceAttachments: {},
    sectionsCompleted: {},
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProvideDetailsComponent],
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
    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_CONCLUSION_SUBMIT',
        payload: initialPayload,
      } as any,
      requestInfo: { accountId: 1 } as any,
    });
    store.setIsEditable(true);

    fixture = TestBed.createComponent(ProvideDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    tasksApiService.saveRequestTaskAction.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the provide details step content', () => {
    expect(getByText('Provide conclusion details', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Has compliance been restored?', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Has the operator paid the penalty?', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Would you like to reissue or withdraw the penalty?', fixture.nativeElement)).toBeTruthy();
    expect(
      getByText(
        'This will start a new task to upload a replacement penalty notice allowing you to reduce the penalty amount and extend the time for payment',
        fixture.nativeElement,
      ),
    ).toBeTruthy();
    expect(getByText('This will require you to upload a withdrawal notice', fixture.nativeElement)).toBeTruthy();
    expect(
      getByText(
        'You must allow at least 28 calendar days for the operator to appeal the penalty notice before concluding this non-compliance task',
        fixture.nativeElement,
      ),
    ).toBeTruthy();
  });

  it('should reject future compliance restored dates on initial submission', () => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);

    component['form'].controls.complianceRestored.setValue(true);
    component['form'].controls.complianceRestoredDate.setValue(tomorrow);

    expect(component['form'].controls.complianceRestoredDate.errors?.invalidDate).toEqual(
      'This date cannot be in the future',
    );
  });

  it('should reject future penalty payment dates on initial submission', () => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);

    component['form'].controls.penaltyPaid.setValue(true);
    component['form'].controls.penaltyPaymentDate.setValue(tomorrow);

    expect(component['form'].controls.penaltyPaymentDate.errors?.invalidDate).toEqual(
      'This date cannot be in the future',
    );
  });

  it('should save and navigate to provide-withdrawal-notice when WITHDRAW selected and no file yet', () => {
    component['form'].setValue({
      complianceRestored: false,
      complianceRestoredDate: null,
      penaltyPaid: false,
      penaltyPaymentDate: null,
      comment: 'Some comments',
      penaltyOutcome: 'WITHDRAW',
    });

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 123,
      requestTaskActionType: 'NON_COMPLIANCE_CONCLUSION_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_CONCLUSION_SAVE_PAYLOAD',
        nonComplianceConclusion: expect.objectContaining({
          details: {
            complianceRestored: false,
            complianceRestoredDate: null,
            penaltyPaid: false,
            penaltyPaymentDate: null,
            comment: 'Some comments',
            penaltyOutcome: 'WITHDRAW',
          },
        }),
        sectionsCompleted: { 'provide-conclusion': 'IN_PROGRESS' },
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../provide-withdrawal-notice'], { relativeTo: route });
  });

  it('should save and navigate to check-your-answers when REISSUE selected', () => {
    component['form'].setValue({
      complianceRestored: false,
      complianceRestoredDate: null,
      penaltyPaid: false,
      penaltyPaymentDate: null,
      comment: 'Some comments',
      penaltyOutcome: 'REISSUE',
    });

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 123,
      requestTaskActionType: 'NON_COMPLIANCE_CONCLUSION_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_CONCLUSION_SAVE_PAYLOAD',
        nonComplianceConclusion: expect.objectContaining({
          details: {
            complianceRestored: false,
            complianceRestoredDate: null,
            penaltyPaid: false,
            penaltyPaymentDate: null,
            comment: 'Some comments',
            penaltyOutcome: 'REISSUE',
          },
        }),
        sectionsCompleted: { 'provide-conclusion': 'IN_PROGRESS' },
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });

  it('should save and navigate to check-your-answers when WITHDRAW selected but file already uploaded', () => {
    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_CONCLUSION_SUBMIT',
        payload: {
          ...initialPayload,
          nonComplianceConclusion: {
            details: {
              complianceRestored: false,
              complianceRestoredDate: null,
              penaltyPaid: false,
              penaltyPaymentDate: null,
              comment: 'Existing comments',
              penaltyOutcome: 'WITHDRAW',
            },
            withdrawNotice: { file: 'uuid-1', comments: 'Notice comments' },
          },
          nonComplianceAttachments: { 'uuid-1': 'notice.pdf' },
        } as NonComplianceConclusionSubmitRequestTaskPayload,
      } as any,
      requestInfo: { accountId: 1 } as any,
    });

    fixture = TestBed.createComponent(ProvideDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    tasksApiService.saveRequestTaskAction.mockClear();

    component['form'].setValue({
      complianceRestored: false,
      complianceRestoredDate: null,
      penaltyPaid: false,
      penaltyPaymentDate: null,
      comment: 'Existing comments',
      penaltyOutcome: 'WITHDRAW',
    });

    component.onSubmit();

    expect(router.navigate).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });

  it('should clear withdrawNotice when switching from WITHDRAW to NONE', () => {
    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_CONCLUSION_SUBMIT',
        payload: {
          ...initialPayload,
          nonComplianceConclusion: {
            details: {
              complianceRestored: false,
              complianceRestoredDate: null,
              penaltyPaid: false,
              penaltyPaymentDate: null,
              comment: 'Comments',
              penaltyOutcome: 'WITHDRAW',
            },
            withdrawNotice: { file: 'uuid-1', comments: 'Notice comments' },
          },
          nonComplianceAttachments: { 'uuid-1': 'notice.pdf' },
        } as NonComplianceConclusionSubmitRequestTaskPayload,
      } as any,
      requestInfo: { accountId: 1 } as any,
    });

    fixture = TestBed.createComponent(ProvideDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    tasksApiService.saveRequestTaskAction.mockClear();

    component['form'].controls.penaltyOutcome.setValue('NONE');
    component['form'].controls.comment.setValue('Comments');

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith(
      expect.objectContaining({
        requestTaskActionPayload: expect.objectContaining({
          nonComplianceConclusion: expect.objectContaining({ withdrawNotice: null }),
        }),
      }),
    );
  });
});
