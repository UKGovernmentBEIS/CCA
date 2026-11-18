import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockRequestActionState } from '../testing/mock-data';
import { UnderlyingAgreementReviewedRejectedDecisionDetailsComponent } from './underlying-agreement-reviewed-rejected-decision-details.component';

describe('UnderlyingAgreementReviewRejectedDecisionComponent', () => {
  let component: UnderlyingAgreementReviewedRejectedDecisionDetailsComponent;
  let fixture: ComponentFixture<UnderlyingAgreementReviewedRejectedDecisionDetailsComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnderlyingAgreementReviewedRejectedDecisionDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockRequestActionState);

    fixture = TestBed.createComponent(UnderlyingAgreementReviewedRejectedDecisionDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
