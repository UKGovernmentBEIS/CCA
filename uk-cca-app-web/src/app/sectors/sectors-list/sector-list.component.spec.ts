import { provideHttpClient } from '@angular/common/http';

import { of } from 'rxjs';

import { render } from '@testing-library/angular';
import { fireEvent, screen } from '@testing-library/dom';

import { SectorAssociationInfoViewService } from 'cca-api';

import { mockSectors } from '../specs/fixtures/mock';
import { SectorListComponent } from './sector-list.component';

describe('SectorListComponenet', () => {
  beforeEach(async () => {
    await render(SectorListComponent, {
      providers: [provideHttpClient()],
      componentProviders: [
        {
          provide: SectorAssociationInfoViewService,
          useValue: {
            getSectorAssociations: jest.fn().mockReturnValue(of(mockSectors)),
          },
        },
      ],
    });
  });

  it('should render title', () => {
    expect(screen.getByText('Manage Sectors')).toBeInTheDocument();
  });

  it('should render the sectors table', () => {
    expect(screen.getByText('Sector')).toBeInTheDocument();
    expect(screen.getByText('Main Contact')).toBeInTheDocument();
    expect(document.querySelector('govuk-table')).toBeInTheDocument();
  });

  it('should populate the table accordingly', async () => {
    const table = document.querySelector('govuk-table');
    mockSectors.forEach(async (sector) => {
      const sectorEl = await screen.findByText(sector.sector);
      const mainContactEl = await screen.findByText(sector.mainContact);
      expect(table).toContainElement(sectorEl);
      expect(table).toContainElement(mainContactEl);
    });
  });

  it('should have a table that has sortable columns', async () => {
    const sectorHeaderEl = await screen.findByText('Sector');
    expect(sectorHeaderEl.parentElement).toHaveAttribute('aria-sort', 'none');

    const mainContactHeaderEl = await screen.findByText('Main Contact');
    expect(mainContactHeaderEl.parentElement).toHaveAttribute('aria-sort', 'none');

    fireEvent.click(sectorHeaderEl);
    expect(sectorHeaderEl.parentElement).toHaveAttribute('aria-sort', 'ascending');
  });

  it('should sort columns based on sector', async () => {
    const sectorHeaderEl = await screen.findByText('Sector');
    const sectors = JSON.parse(JSON.stringify(mockSectors));

    // asc sorting
    fireEvent.click(sectorHeaderEl);
    sectors.sort((a, b) => {
      return a.sector > b.sector ? 1 : -1;
    });

    const rows = document.querySelectorAll('tr td:first-child a');
    rows.forEach((row, idx) => expect(row.innerHTML).toEqual(sectors[idx].sector));

    // desc sorting
    fireEvent.click(sectorHeaderEl);
    sectors.sort((a, b) => {
      return a.sector > b.sector ? 1 : -1;
    });

    rows.forEach((row, idx) => expect(row.innerHTML).toEqual(sectors[idx].sector));
  });

  it('should sort columns based on main contact', async () => {
    const mainContact = await screen.findByText('Main Contact');
    const sectors = JSON.parse(JSON.stringify(mockSectors));
    // asc sorting
    fireEvent.click(mainContact);
    sectors.sort((a, b) => {
      return a.mainContact > b.mainContact ? 1 : -1;
    });

    let rows = document.querySelectorAll('tr td:nth-child(2)');
    rows.forEach((row, idx) => expect(row.textContent.trim()).toEqual(sectors[idx].mainContact));

    // desc sorting

    fireEvent.click(mainContact);
    sectors.sort((a, b) => {
      return a.mainContact < b.mainContact ? 1 : -1;
    });

    rows = document.querySelectorAll('tr td:nth-child(2)');
    rows.forEach((row, idx) => expect(row.textContent.trim()).toEqual(sectors[idx].mainContact));
  });
});
