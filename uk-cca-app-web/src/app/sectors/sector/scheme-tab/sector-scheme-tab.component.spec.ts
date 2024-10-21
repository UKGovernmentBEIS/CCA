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
    expect(screen.getByText('Umbrella')).toBeInTheDocument();
    expect(screen.getByText('Target currency')).toBeInTheDocument();
    expect(screen.getByText('Sector commitment')).toBeInTheDocument();
    expect(screen.getByText('Subsectors')).toBeInTheDocument();
  });

  it('should render "Umbrella" section', () => {
    const umbrellaList = document.querySelectorAll("[data-testid='umbrella-list'] div");

    const elements = [];

    umbrellaList.forEach((div) => {
      elements.push([div.querySelector('dt').textContent, div.querySelector('dd').textContent]);
    });

    expect(elements).toEqual([['Current umbrella agreement', ' file-name (pdf, 20KB) ']]);
  });

  it('should render the details hint in "Target currency" and "Sector commitment" section', () => {
    expect(screen.getAllByText("You can see more information in the subsector's details.")).toHaveLength(2);
  });

  it('should populate the table accordingly', async () => {
    const table = document.querySelector('govuk-table');

    mockSectorScheme.subsectorAssociationSchemes.forEach(async (scheme) => {
      const schemeEl = await screen.findByText(scheme.subsectorAssociation.name);

      expect(table).toContainElement(schemeEl);
    });
  });
});
