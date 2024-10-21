import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { AccountStatusPipe } from '@shared/pipes';
import { render, screen } from '@testing-library/angular';

import { AccountsListComponent } from './accounts-list.component';
import { mockAccountSearchResults } from './testing/mock-data';

describe('AccountsListComponent', () => {
  beforeEach(async () => {
    await render(AccountsListComponent, {
      componentInputs: { accounts: mockAccountSearchResults.accounts },
      componentProviders: [
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            queryParamMap: of(convertToParamMap({ term: null, page: '1' })),
          }),
        },
      ],
    });
  });

  it('should create', async () => {
    expect(screen.getByTestId('accounts-list-table')).toBeVisible();
  });

  it('should create a table with the correct number of data rows', () => {
    const rows = screen.getAllByRole('row', { hidden: true });
    expect(rows.length).toBe(mockAccountSearchResults.accounts.length + 1); // +1 for the header row
  });

  it('should render table headers correctly', () => {
    const headers = screen.getAllByRole('columnheader');
    expect(headers[0]).toHaveTextContent('Target unit name');
    expect(headers[1]).toHaveTextContent('ID');
    expect(headers[2]).toHaveTextContent('Status');
  });

  it('should render the correct data in each row', () => {
    const rows = screen.getAllByRole('row', { hidden: true });
    rows.slice(1).forEach((row, index) => {
      const cells = row.querySelectorAll('td');
      const account = mockAccountSearchResults.accounts[index];
      const pipe = new AccountStatusPipe();

      expect(cells[0]).toHaveTextContent(account.name);
      expect(cells[1]).toHaveTextContent(account.businessId);
      expect(cells[2]).toHaveTextContent(pipe.transform(account.status));
    });
  });
});
