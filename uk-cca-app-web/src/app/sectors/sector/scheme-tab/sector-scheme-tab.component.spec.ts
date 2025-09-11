import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { SectorAssociationSchemeService } from 'cca-api';

import { mockSectorScheme } from '../../specs/fixtures/mock';
import { SectorSchemeTabComponent } from './sector-scheme-tab.component';

describe('SectorSchemeTabComponent', () => {
  let sectorAssociationAuthoritiesService: Partial<jest.Mocked<SectorAssociationSchemeService>>;

  beforeEach(async () => {
    sectorAssociationAuthoritiesService = {
      getSectorAssociationSchemeBySectorAssociationId: jest.fn().mockReturnValue(of(mockSectorScheme)),
    };

    await render(SectorSchemeTabComponent, {
      configureTestBed: (testbed) => {
        testbed.overrideProvider(ActivatedRoute, { useValue: new ActivatedRouteStub({ id: 1 }) });
        testbed.overrideProvider(SectorAssociationSchemeService, {
          useValue: sectorAssociationAuthoritiesService,
        });
      },
    });
  });

  it('should render all titles', () => {
    expect(screen.getByText('Scheme')).toBeInTheDocument();
    expect(screen.getByText('CCA2 (2013-2024)')).toBeInTheDocument();
    expect(screen.getByText('Subsectors')).toBeInTheDocument();
  });

  it('should render "Umbrella agreement" section', () => {
    const summaryList = document.querySelector('dl[govuk-summary-list]');
    const umbrellaRow = Array.from(summaryList.querySelectorAll('div[govuksummarylistrow]')).find(
      (div) => div.querySelector('dt').textContent === 'Umbrella agreement CCA2',
    );

    expect(umbrellaRow).toBeTruthy();
    expect(umbrellaRow.querySelector('dt').textContent).toBe('Umbrella agreement CCA2');
    expect(umbrellaRow.querySelector('dd').textContent).toContain('file-name (pdf, 20KB)');
  });

  it('should render the subsector hint message', () => {
    const hint = document.querySelector('p');
    expect(hint).toHaveTextContent(
      'You can find more information about currency and sector commitment in the subsector',
    );
  });

  it('should populate the subsector table accordingly', async () => {
    const table = document.querySelector('govuk-table');

    mockSectorScheme.subsectorAssociations.forEach(async (subsector) => {
      const schemeEl = await screen.findByText(subsector.name);

      expect(table).toContainElement(schemeEl);
    });
  });
});
