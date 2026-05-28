import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockPreAuditReviewState } from '../../../testing/mock-data';
import { PreAuditReviewDeterminationSummaryComponent } from './pre-audit-review-determination-summary.component';

describe('PreAuditReviewDeterminationSummaryComponent', () => {
  let component: PreAuditReviewDeterminationSummaryComponent;
  let fixture: ComponentFixture<PreAuditReviewDeterminationSummaryComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreAuditReviewDeterminationSummaryComponent],
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Dashboard' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockPreAuditReviewState);

    fixture = TestBed.createComponent(PreAuditReviewDeterminationSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct content', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });
});
