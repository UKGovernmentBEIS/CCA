import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockPreAuditReviewState } from '../../../testing/mock-data';
import { PreAuditReviewDeterminationCheckYourAnswersComponent } from './pre-audit-review-determination-check-your-answers.component';

describe('PreAuditReviewDeterminationCheckYourAnswersComponent', () => {
  let component: PreAuditReviewDeterminationCheckYourAnswersComponent;
  let fixture: ComponentFixture<PreAuditReviewDeterminationCheckYourAnswersComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreAuditReviewDeterminationCheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockPreAuditReviewState);

    fixture = TestBed.createComponent(PreAuditReviewDeterminationCheckYourAnswersComponent);
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
