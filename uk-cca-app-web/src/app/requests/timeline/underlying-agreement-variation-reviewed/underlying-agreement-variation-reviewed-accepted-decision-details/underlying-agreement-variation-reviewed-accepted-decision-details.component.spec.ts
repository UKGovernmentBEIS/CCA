import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';

import { mockAcceptedRequestActionState } from '../testing/mock-data';
import { UnderlyingAgreementVariationReviewedAcceptedDecisionDetailsComponent } from './underlying-agreement-variation-reviewed-accepted-decision-details.component';

describe('UnderlyingAgreementVariationReviewedAcceptedDecisionDetailsComponent', () => {
  let store: RequestActionStore;
  let tree: Element;

  beforeEach(async () => {
    const renderResult = await render(UnderlyingAgreementVariationReviewedAcceptedDecisionDetailsComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(RequestActionStore);
        store.setState(mockAcceptedRequestActionState);
      },
    });
    tree = renderResult.container;
  });

  it('should match snapshot', () => {
    expect(tree).toMatchSnapshot();
  });
});
