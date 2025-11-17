import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockPreAuditReviewState } from '../../../testing/mock-data';
import { PreAuditReviewRequestedDocumentsCheckYourAnswersComponent } from './pre-audit-review-requested-documents-check-your-answers.component';

describe('PreAuditReviewRequestedDocumentsCheckYourAnswersComponent', () => {
  let component: PreAuditReviewRequestedDocumentsCheckYourAnswersComponent;
  let fixture: ComponentFixture<PreAuditReviewRequestedDocumentsCheckYourAnswersComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreAuditReviewRequestedDocumentsCheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockPreAuditReviewState);

    fixture = TestBed.createComponent(PreAuditReviewRequestedDocumentsCheckYourAnswersComponent);
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
