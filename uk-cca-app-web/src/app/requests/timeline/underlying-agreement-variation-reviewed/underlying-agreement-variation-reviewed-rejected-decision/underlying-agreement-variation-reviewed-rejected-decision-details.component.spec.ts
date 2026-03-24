import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockRejectedRequestActionState } from '../testing/mock-data';
import { UnderlyingAgreementVariationReviewedRejectedDecisionDetailsComponent } from './underlying-agreement-variation-reviewed-rejected-decision-details.component';

describe('UnderlyingAgreementVariationReviewedRejectedDecisionDetailsComponent', () => {
  let fixture: ComponentFixture<UnderlyingAgreementVariationReviewedRejectedDecisionDetailsComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnderlyingAgreementVariationReviewedRejectedDecisionDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockRejectedRequestActionState);

    fixture = TestBed.createComponent(UnderlyingAgreementVariationReviewedRejectedDecisionDetailsComponent);
    fixture.detectChanges();
  });

  it('should match snapshot', () => {
    expect(fixture.nativeElement).toMatchSnapshot();
  });
});
