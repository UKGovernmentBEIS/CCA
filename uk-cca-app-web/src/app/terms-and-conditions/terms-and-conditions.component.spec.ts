import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { AuthService, LatestTermsStore } from '@shared/services';
import { render } from '@testing-library/angular';

import { UsersService } from 'cca-api';

import { TermsAndConditionsComponent } from './terms-and-conditions.component';

describe('TermsAndConditionsComponent', () => {
  let httpTestingController: HttpTestingController;
  let latestTermsStore: LatestTermsStore;
  let container: Element;

  const authService: Partial<jest.Mocked<AuthService>> = {
    loadUserTerms: jest.fn(() => of({})),
  };

  beforeEach(async () => {
    const renderResult = await render(TermsAndConditionsComponent, {
      providers: [
        UsersService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authService },
      ],
      configureTestBed: (testbed) => {
        latestTermsStore = testbed.inject(LatestTermsStore);
        latestTermsStore.setLatestTerms({ url: '/test', version: 2 });

        httpTestingController = testbed.inject(HttpTestingController);
      },
    });

    container = renderResult.container;
  });

  afterEach(() => httpTestingController.verify());

  it('should create', () => {
    expect(container).toMatchSnapshot('terms-and-conditions ');
  });
});
