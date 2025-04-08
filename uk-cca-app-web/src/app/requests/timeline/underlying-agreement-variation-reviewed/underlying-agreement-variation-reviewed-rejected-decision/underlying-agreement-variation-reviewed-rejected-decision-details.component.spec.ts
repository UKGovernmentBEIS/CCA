import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';

import { mockRejectedRequestActionState } from '../testing/mock-data';
import { UnderlyingAgreementVariationReviewedRejectedDecisionDetailsComponent } from './underlying-agreement-variation-reviewed-rejected-decision-details.component';

describe('UnderlyingAgreementVariationReviewedRejectedDecisionDetailsComponent', () => {
  let store: RequestActionStore;
  let tree: Element;

  beforeEach(async () => {
    const renderResult = await render(UnderlyingAgreementVariationReviewedRejectedDecisionDetailsComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(RequestActionStore);
        store.setState(mockRejectedRequestActionState);
      },
    });
    tree = renderResult.container;
  });

  it('should match snapshot', () => {
    expect(tree).toMatchSnapshot();
  });
});
