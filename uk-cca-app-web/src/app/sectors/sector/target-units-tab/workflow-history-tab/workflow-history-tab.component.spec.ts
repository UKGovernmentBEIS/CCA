import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { RequestsService } from 'cca-api';

import { filterByRequestType, mockRequestDetailsSearchResultsData } from './testing/mock-data';
import { WorkflowHistoryTabComponent } from './workflow-history-tab.component';

describe('WorkflowHistoryTabComponent', () => {
  let requestService: jest.Mocked<Partial<RequestsService>>;
  beforeEach(async () => {
    const { fixture } = await render(WorkflowHistoryTabComponent, {
      providers: [provideHttpClient(), provideHttpClientTesting()],
      configureTestBed: (testbed) => {
        requestService = {
          getRequestDetailsByResource: jest.fn().mockReturnValue(of(mockRequestDetailsSearchResultsData)),
        };

        testbed.overrideProvider(ActivatedRoute, { useValue: new ActivatedRouteStub({ targetUnitId: 7 }) });
        testbed.overrideProvider(RequestsService, {
          useValue: requestService,
        });
      },
    });

    fixture.detectChanges();
  });

  it('should render items when fetched', async () => {
    expect(screen.getByTestId('workflow-history-form')).toBeVisible();
    expect(document.querySelectorAll('.search-results-list_item')).toHaveLength(
      mockRequestDetailsSearchResultsData.total,
    );
  });

  it('should send a request when a checkbox is clicked', async () => {
    const user = UserEvent.setup();
    requestService.getRequestDetailsByResource = jest.fn().mockReturnValue(of(filterByRequestType('SECTOR_MOA')));

    await user.click(screen.getByLabelText('Subsistence fees'));

    mockRequestDetailsSearchResultsData.requestDetails
      .filter((d) => d.requestType === 'SECTOR_MOA')
      .forEach((rd) => {
        expect(screen.getByText(rd.id)).toBeVisible();
      });

    mockRequestDetailsSearchResultsData.requestDetails
      .filter((d) => d.requestType !== 'SECTOR_MOA')
      .forEach((rd) => {
        expect(screen.queryByText(rd.id)).not.toBeInTheDocument();
      });
  });
});
