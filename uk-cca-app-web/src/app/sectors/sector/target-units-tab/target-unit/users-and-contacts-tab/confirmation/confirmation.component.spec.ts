import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { mockTargetUnitAccountDetails } from 'src/app/sectors/specs/fixtures/mock';

import { ActiveTargetUnitStore } from '../../../active-target-unit.store';
import { AddOperatorConfirmationComponent } from './confirmation.component';

describe('Invite operator confirmation ', () => {
  let targetUnitStore: ActiveTargetUnitStore;

  beforeEach(async () => {
    const { fixture } = await render(AddOperatorConfirmationComponent, {
      providers: [ActiveTargetUnitStore, provideHttpClient(), provideHttpClientTesting()],
      configureTestBed: (testbed) => {
        targetUnitStore = testbed.inject(ActiveTargetUnitStore);
        targetUnitStore.setState({ targetUnitAccountDetails: mockTargetUnitAccountDetails });
      },
    });
    fixture.detectChanges();
  });

  it('should render content correctly', () => {
    const text = `You have successfully added an operator user for ${mockTargetUnitAccountDetails.businessId} - ${mockTargetUnitAccountDetails.name}`;
    expect(screen.getByText(text)).toBeInTheDocument();
  });

  it('should render return link correctly', () => {
    expect(screen.getByRole('link')).toHaveTextContent('Go to my dashboard');
    expect(screen.getByRole('link')).toHaveAttribute('href', '/dashboard');
  });
});
