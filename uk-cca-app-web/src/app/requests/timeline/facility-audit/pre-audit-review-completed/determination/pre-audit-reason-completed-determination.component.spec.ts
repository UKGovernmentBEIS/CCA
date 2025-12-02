import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestActionStore } from '@netz/common/store';

import { mockPreAuditReviewCompletedActionState } from '../../testing/mock-data';
import { PreAuditReasonCompletedDeterminationComponent } from './pre-audit-reason-completed-determination.component';

describe('PreAuditReasonCompletedDeterminationComponent', () => {
  let component: PreAuditReasonCompletedDeterminationComponent;
  let fixture: ComponentFixture<PreAuditReasonCompletedDeterminationComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreAuditReasonCompletedDeterminationComponent],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(mockPreAuditReviewCompletedActionState);

    fixture = TestBed.createComponent(PreAuditReasonCompletedDeterminationComponent);
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
