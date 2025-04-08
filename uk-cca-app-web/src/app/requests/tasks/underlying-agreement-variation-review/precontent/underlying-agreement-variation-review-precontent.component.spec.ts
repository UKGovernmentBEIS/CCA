import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { mockClass } from '@netz/common/testing';
import { mockUNAReviewRequestTaskState } from '@requests/common';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { UnderlyingAgreementVariationReviewPrecontentComponent } from './underlying-agreement-variation-review-precontent.component';

describe('UnderlyingAgreementReviewPrecontentComponent', () => {
  let router: Router;

  const mockRoute = mockClass(ActivatedRoute);
  const mockRouter: jest.Mocked<Partial<Router>> = { navigate: jest.fn().mockReturnValue(of(null)) };

  beforeEach(async () => {
    await render(UnderlyingAgreementVariationReviewPrecontentComponent, {
      providers: [provideHttpClient(), provideHttpClientTesting(), RequestTaskStore, provideRouter([])],
      configureTestBed: (testbed) => {
        testbed.overrideProvider(Router, { useValue: mockRouter });
        testbed.overrideProvider(ActivatedRoute, { useValue: mockRoute });

        const store = testbed.inject(RequestTaskStore);
        store.setState(mockUNAReviewRequestTaskState);
        store.setState({ ...store.state, isEditable: true });

        router = testbed.inject(Router);
      },
    });
  });

  it('should render notify button', () => {
    expect(screen.getByText('Notify operator of decision')).toBeVisible();
  });

  it('should navigate to correct url', async () => {
    const spy = jest.spyOn(router, 'navigate');
    const user = UserEvent.setup();

    await user.click(screen.getByText('Notify operator of decision'));
    expect(spy).toHaveBeenCalledWith(['underlying-agreement-variation-review', 'notify-operator'], {
      relativeTo: mockRoute,
    });
  });
});
