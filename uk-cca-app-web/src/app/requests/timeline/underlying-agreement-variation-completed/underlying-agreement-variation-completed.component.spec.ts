import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';

import { mockCompletedRequestActionState } from './testing/mock-data';
import { UnderlyingAgreementVariationCompletedComponent } from './underlying-agreement-variation-completed.component';

describe('UnderlyingAgreementVariationCompletedComponent', () => {
  let store: RequestActionStore;
  let tree: Element;

  beforeEach(async () => {
    const renderResult = await render(UnderlyingAgreementVariationCompletedComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(RequestActionStore);
        store.setState(mockCompletedRequestActionState);
      },
    });

    tree = renderResult.container;
  });

  it('should match snapshot', () => {
    expect(tree).toMatchSnapshot();
  });
});
