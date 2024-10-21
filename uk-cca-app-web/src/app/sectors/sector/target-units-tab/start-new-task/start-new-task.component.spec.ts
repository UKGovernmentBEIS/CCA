import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub } from '@netz/common/testing';
import { render, screen } from '@testing-library/angular';

import { RequestItemsService, RequestsService, UserStateDTO } from 'cca-api';

import { StartNewTaskComponent } from './start-new-task.component';
import { validAvailableWorkflowMock } from './testing/mock-data';

describe('StartNewTaskComponent', () => {
  let authStore;
  let requestService: jest.Mocked<Partial<RequestsService>>;
  let requestItemService: jest.Mocked<Partial<RequestItemsService>>;
  const userStateDTO: UserStateDTO = { userId: 'abcd-1234', roleType: 'REGULATOR', status: 'ENABLED' };

  beforeEach(async () => {
    const { fixture } = await render(StartNewTaskComponent, {
      providers: [provideHttpClient(), provideHttpClientTesting()],
      configureTestBed: (testbed) => {
        testbed.overrideProvider(ActivatedRoute, {
          useValue: new ActivatedRouteStub(
            {
              targetUnitId: 1,
            },
            null,
            {
              targetUnit: {
                targetUnitAccountDetails: {
                  name: 'Target unit account 1',
                },
              },
            },
          ),
        });

        requestService = {
          getAvailableAccountWorkflows: jest.fn().mockReturnValue(of(validAvailableWorkflowMock)),
        };

        requestItemService = {
          getItemsByRequest: jest.fn().mockReturnValue(of()),
        };

        testbed.overrideProvider(RequestsService, {
          useValue: requestService,
        });

        testbed.overrideProvider(RequestItemsService, {
          useValue: requestItemService,
        });
      },
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setIsLoggedIn(true);
    authStore.setUserState(userStateDTO);
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should render the page heading', async () => {
    expect(requestService.getAvailableAccountWorkflows).toHaveBeenCalledTimes(1);
    expect(screen.getByText('Start a new task')).toBeInTheDocument();
  });

  it('should display available workflows', async () => {
    const noWorkflowsMessage = screen.getByTestId('no-workflows');
    expect(noWorkflowsMessage).toBeInTheDocument();
    expect(noWorkflowsMessage.textContent).toBe('There are no available workflows to initiate.');
  });
});
