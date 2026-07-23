import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionState, RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { NonComplianceNoticeOfIntentPeerReviewerDecisionComponent } from './non-compliance-notice-of-intent-peer-reviewer-decision.component';

describe('NonComplianceNoticeOfIntentPeerReviewerDecisionComponent', () => {
  let component: NonComplianceNoticeOfIntentPeerReviewerDecisionComponent;
  let fixture: ComponentFixture<NonComplianceNoticeOfIntentPeerReviewerDecisionComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonComplianceNoticeOfIntentPeerReviewerDecisionComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState({
      action: {
        id: 1,
        type: 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEWER_ACCEPTED',
        payload: {
          decision: {
            type: 'AGREE',
            notes: 'Detailed supporting notes',
            files: ['uuid-1'],
          },
          peerReviewAttachments: {
            'uuid-1': 'peer-review-file.pdf',
          },
        },
      },
    } as RequestActionState);
    fixture = TestBed.createComponent(NonComplianceNoticeOfIntentPeerReviewerDecisionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the peer review decision details', () => {
    expect(getSummaryListData(fixture.nativeElement)).toEqual([
      [
        ['Peer review decision', 'Supporting notes', 'Uploaded files'],
        ['I agree with the determination', 'Detailed supporting notes', 'peer-review-file.pdf'],
      ],
    ]);
  });
});
