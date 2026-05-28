import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText, queryByText } from '@testing';
import { Mocked } from 'vitest';

import { NonComplianceConclusionSubmitRequestTaskPayload, TasksService } from 'cca-api';

import { ProvideAppealDetailsStore } from '../+state';
import { CheckYourAnswersComponent } from './check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let component: CheckYourAnswersComponent;
  let fixture: ComponentFixture<CheckYourAnswersComponent>;
  let store: RequestTaskStore;
  let appealDetailsStore: ProvideAppealDetailsStore;
  let router: Router;
  let tasksService: Mocked<Partial<TasksService>>;

  const route = new ActivatedRouteStub({ taskId: '123' });

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
      } as any,
      requestInfo: { accountId: 1 } as any,
    });
    store.setIsEditable(isEditable);
    appealDetailsStore.setState({
      appealDetails: {
        registrationDate: '2026-01-01',
        files: ['uuid-1'],
        comments: 'Appeal comments',
      },
      attachments: { 'uuid-1': 'appeal.pdf' },
    });

    fixture = TestBed.createComponent(CheckYourAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    tasksService = {
      processRequestTaskAction: vi.fn().mockReturnValue(of({})),
    };

    await TestBed.configureTestingModule({
      imports: [CheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        ProvideAppealDetailsStore,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Provide conclusion of non-compliance' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    appealDetailsStore = TestBed.inject(ProvideAppealDetailsStore);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
  });

  it('should create', () => {
    createComponent();

    expect(component).toBeTruthy();
  });

  it('should render the check your answers page with confirm button for editable tasks', () => {
    createComponent();

    expect(queryByText('Provide conclusion of non-compliance', fixture.nativeElement)).toBeNull();
    expect(getByText('Check your answers', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Confirm and complete', fixture.nativeElement)).toBeTruthy();
  });

  it('should submit appeal details and navigate to confirmation', () => {
    createComponent();

    component.onSubmit();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'NON_COMPLIANCE_PROVIDE_APPEAL_DETAILS',
      requestTaskId: 123,
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_PROVIDE_APPEAL_DETAILS_PAYLOAD',
        appealDetails: {
          registrationDate: '2026-01-01',
          files: ['uuid-1'],
          comments: 'Appeal comments',
        },
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../confirmation'], { relativeTo: route, replaceUrl: true });
  });

  it('should hide the confirm button and not submit when task is read only', () => {
    createComponent(false);

    expect(queryByText('Confirm and complete', fixture.nativeElement)).toBeNull();

    component.onSubmit();

    expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();
  });
});
