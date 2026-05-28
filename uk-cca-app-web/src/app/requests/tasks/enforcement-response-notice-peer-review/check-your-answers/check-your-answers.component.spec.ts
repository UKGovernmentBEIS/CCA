import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { Mocked } from 'vitest';

import { TasksService } from 'cca-api';

import { EnforcementResponseNoticePeerReviewStore } from '../+state';
import { mockEnforcementResponseNoticePeerReviewRequestTaskState } from '../testing/mock-data';
import { CheckYourAnswersComponent } from './check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let component: CheckYourAnswersComponent;
  let fixture: ComponentFixture<CheckYourAnswersComponent>;
  let requestTaskStore: RequestTaskStore;
  let peerReviewStore: EnforcementResponseNoticePeerReviewStore;
  let tasksService: Mocked<Partial<TasksService>>;
  let router: Mocked<Partial<Router>>;

  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    tasksService = {
      processRequestTaskAction: vi.fn().mockReturnValue(of({})),
    };
    router = {
      navigate: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [CheckYourAnswersComponent],
      providers: [
        RequestTaskStore,
        EnforcementResponseNoticePeerReviewStore,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: route },
        { provide: Router, useValue: router },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    requestTaskStore = TestBed.inject(RequestTaskStore);
    requestTaskStore.setState(mockEnforcementResponseNoticePeerReviewRequestTaskState);

    peerReviewStore = TestBed.inject(EnforcementResponseNoticePeerReviewStore);
    peerReviewStore.setState({
      decision: {
        type: 'AGREE',
        notes: 'Peer review notes',
        files: ['uuid-2'],
      },
      attachments: {
        'uuid-2': 'peer-review-file.pdf',
      },
    });

    fixture = TestBed.createComponent(CheckYourAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit the enforcement response notice peer review decision with the correct payload type', () => {
    component.onSubmit();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PEER_REVIEW_DECISION',
      requestTaskId: 123,
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_PEER_REVIEW_DECISION_PAYLOAD',
        decision: {
          type: 'AGREE',
          notes: 'Peer review notes',
          files: ['uuid-2'],
        },
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../confirmation'], { relativeTo: route });
  });
});
