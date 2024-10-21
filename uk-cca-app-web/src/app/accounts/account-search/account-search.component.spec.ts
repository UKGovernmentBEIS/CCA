import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { ActivatedRouteStub } from '@netz/common/testing';
import { render, screen } from '@testing-library/angular';

import { TargetUnitAccountInfoViewService } from 'cca-api';

import { mockAccountSearchResults } from '../accounts-list/testing/mock-data';
import { AccountSearchComponent } from './account-search.component';

type MockService = Partial<jest.Mocked<TargetUnitAccountInfoViewService>>;
async function createComponent(mockService: MockService) {
  await render(AccountSearchComponent, {
    imports: [ReactiveFormsModule],
    providers: [
      {
        provide: ActivatedRoute,
        useValue: new ActivatedRouteStub(null, null, {
          queryParamMap: of(convertToParamMap({ term: null, page: '1' })),
        }),
      },
      { provide: TargetUnitAccountInfoViewService, useValue: mockService },
      DestroySubject,
    ],
  });
}

describe('AccountSearchComponent', () => {
  let mockTargetUnitAccountInfoViewService: MockService;

  describe('No data test suite', () => {
    beforeEach(async () => {
      mockTargetUnitAccountInfoViewService = {
        searchUserAccounts: jest.fn().mockReturnValue(of({ accounts: [], total: 0 })),
      };
      await createComponent(mockTargetUnitAccountInfoViewService);
    });

    it('should create', async () => {
      expect(screen.getByTestId('account-search')).toBeVisible();
    });

    it('should render target unit accounts search heading', async () => {
      expect(screen.getByTestId('page-heading')).toHaveTextContent('Target unit accounts');
    });

    it('should render search form', async () => {
      expect(screen.getByLabelText('Enter target unit name, ID or facility code')).toBeVisible();
      expect(screen.getByRole('button', { name: /search/i })).toBeVisible();
    });

    it('should render no results message when accounts list is empty', async () => {
      expect(screen.getByText('There are no results to show')).toBeVisible();
    });
  });

  describe('Data included test suite', () => {
    beforeEach(async () => {
      mockTargetUnitAccountInfoViewService = {
        searchUserAccounts: jest.fn().mockReturnValue(of(mockAccountSearchResults)),
      };
      await createComponent(mockTargetUnitAccountInfoViewService);
    });

    it('should render accounts component', async () => {
      expect(screen.getByTestId('accounts-component')).toBeInTheDocument();
      expect(screen.getByTestId('accounts-component')).toBeVisible();
    });

    it('should call fetchAccounts once explicitly', async () => {
      const spy = jest.spyOn(mockTargetUnitAccountInfoViewService, 'searchUserAccounts');
      expect(spy).toHaveBeenCalledTimes(1);
    });
  });
});
