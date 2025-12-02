import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestActionStore } from '@netz/common/store';

import { mockPreAuditReviewCompletedActionState } from '../../testing/mock-data';
import { PreAuditReasonCompletedDocumentsComponent } from './pre-audit-reason-completed-documents.component';

describe('PreAuditReasonCompletedDocumentsComponent', () => {
  let component: PreAuditReasonCompletedDocumentsComponent;
  let fixture: ComponentFixture<PreAuditReasonCompletedDocumentsComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreAuditReasonCompletedDocumentsComponent],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(mockPreAuditReviewCompletedActionState);

    fixture = TestBed.createComponent(PreAuditReasonCompletedDocumentsComponent);
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
