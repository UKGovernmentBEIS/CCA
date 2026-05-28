import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { Mocked } from 'vitest';

import { TasksService } from 'cca-api';

import { NoticeOfIntentPeerReviewStore } from '../+state';
import { mockNoticeOfIntentPeerReviewRequestTaskState } from '../testing/mock-data';
import { CheckYourAnswersComponent } from './check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let component: CheckYourAnswersComponent;
  let fixture: ComponentFixture<CheckYourAnswersComponent>;
  let requestTaskStore: RequestTaskStore;
  let peerReviewStore: NoticeOfIntentPeerReviewStore;
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
        NoticeOfIntentPeerReviewStore,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: route },
        { provide: Router, useValue: router },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    requestTaskStore = TestBed.inject(RequestTaskStore);
    requestTaskStore.setState(mockNoticeOfIntentPeerReviewRequestTaskState);

    peerReviewStore = TestBed.inject(NoticeOfIntentPeerReviewStore);
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

  it('should submit the peer review decision with the correct payload type', () => {
    component.onSubmit();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PEER_REVIEW_DECISION',
      requestTaskId: 123,
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_DECISION_PAYLOAD',
        decision: {
          type: 'AGREE',
          notes: 'Peer review notes',
          files: ['uuid-2'],
        },
        referencedAttachmentIds: ['uuid-2'],
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../confirmation'], { relativeTo: route });
  });
});
