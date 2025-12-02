import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestActionStore } from '@netz/common/store';

import { mockPreAuditReviewCompletedActionState } from '../../testing/mock-data';
import { PreAuditReviewCompletedReasonComponent } from './pre-audit-review-completed-reason.component';

describe('PreAuditReviewCompletedReasonComponent', () => {
  let component: PreAuditReviewCompletedReasonComponent;
  let fixture: ComponentFixture<PreAuditReviewCompletedReasonComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreAuditReviewCompletedReasonComponent],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(mockPreAuditReviewCompletedActionState);

    fixture = TestBed.createComponent(PreAuditReviewCompletedReasonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show proper view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
