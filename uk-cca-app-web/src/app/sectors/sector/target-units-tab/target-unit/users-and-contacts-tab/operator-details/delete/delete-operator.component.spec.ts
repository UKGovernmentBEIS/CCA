import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { transformUsername } from '@netz/common/pipes';
import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { mockTargetUnitOperatorDetails } from 'src/app/sectors/specs/fixtures/mock';

import { ActiveOperatorStore } from '../active-operator.store';
import { DeleteOperatorComponent } from './delete-operator.component';

describe('Delete Operator Component', () => {
  let store: ActiveOperatorStore;

  beforeEach(async () => {
    await render(DeleteOperatorComponent, {
      providers: [ActiveOperatorStore, provideHttpClient(), provideHttpClientTesting()],
      configureTestBed: (testbed) => {
        testbed.overrideProvider(ActivatedRoute, {
          useValue: new ActivatedRouteStub({ targetUnitId: 1, userId: '1123asd12b4' }),
        });

        store = testbed.inject(ActiveOperatorStore);
        store.setState({
          details: mockTargetUnitOperatorDetails,
          editable: true,
        });
      },
    });
  });

  it('should render contexnt appropriately', () => {
    expect(
      screen.getByText(
        `Confirm that the user account of ${transformUsername(mockTargetUnitOperatorDetails)} will be removed`,
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText(
        "All tasks currently assigned to this user will be automatically unassigned after you select 'Confirm removal'.",
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText("If you need to reassign any of these tasks before removing this user, select 'Cancel'."),
    ).toBeInTheDocument();

    expect(screen.getByText('Confirm removal')).toBeInTheDocument();
    expect(screen.getByText('Cancel')).toBeInTheDocument();
  });
});
