import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockPreAuditReviewState } from '../../../testing/mock-data';
import { PreAuditReviewAuditReasonSummaryComponent } from './pre-audit-review-audit-reason-summary.component';

describe('PreAuditReviewSummaryComponent', () => {
  let component: PreAuditReviewAuditReasonSummaryComponent;
  let fixture: ComponentFixture<PreAuditReviewAuditReasonSummaryComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreAuditReviewAuditReasonSummaryComponent],
      providers: [RequestTaskStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockPreAuditReviewState);

    fixture = TestBed.createComponent(PreAuditReviewAuditReasonSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct content', () => {
    expect(fixture).toMatchSnapshot();
  });
});
