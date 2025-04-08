import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { OperatorAuthoritiesInfoDTO, OperatorAuthoritiesService } from 'cca-api';

import { mockOperatorAuthorities, mockOperatorAuthoritiesNotEditable } from 'src/app/sectors/specs/fixtures/mock';

import { UsersAndContactsTabComponent } from './users-and-contacts-tab.component';

describe('Target unit Users component', () => {
  let operatorAuthoritiesService: jest.Mocked<Partial<OperatorAuthoritiesService>>;

  async function setup(mockData: OperatorAuthoritiesInfoDTO) {
    operatorAuthoritiesService = {
      getAccountOperatorAuthorities: jest.fn().mockReturnValue(of(mockData)),
    };

    const { fixture } = await render(UsersAndContactsTabComponent, {
      providers: [provideHttpClient(), provideHttpClientTesting()],
      configureTestBed: (testbed) => {
        testbed.overrideProvider(OperatorAuthoritiesService, { useValue: operatorAuthoritiesService });
      },
    });

    fixture.detectChanges();
  }

  it('should render users', async () => {
    await setup(mockOperatorAuthorities);
    expect(screen.getByTestId('target-unit-users-form')).toBeInTheDocument();
    expect(document.querySelectorAll('.govuk-table__row')).toHaveLength(mockOperatorAuthorities.authorities.length + 1);
  });

  it('should NOT show add operator button if NOT editable (only Regulator user allowed)', async () => {
    await setup(mockOperatorAuthoritiesNotEditable);
    expect(screen.queryByText('Add a new operator')).not.toBeInTheDocument();
  });
});
