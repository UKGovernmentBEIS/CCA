import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockAcceptedRequestActionState } from '../testing/mock-data';
import { UnderlyingAgreementVariationReviewedAcceptedDecisionDetailsComponent } from './underlying-agreement-variation-reviewed-accepted-decision-details.component';

describe('UnderlyingAgreementVariationReviewedAcceptedDecisionDetailsComponent', () => {
  let fixture: ComponentFixture<UnderlyingAgreementVariationReviewedAcceptedDecisionDetailsComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnderlyingAgreementVariationReviewedAcceptedDecisionDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockAcceptedRequestActionState);

    fixture = TestBed.createComponent(UnderlyingAgreementVariationReviewedAcceptedDecisionDetailsComponent);
    fixture.detectChanges();
  });

  it('should match snapshot', () => {
    expect(fixture.nativeElement).toMatchSnapshot();
  });
});
