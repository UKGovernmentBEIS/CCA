import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { SectorAssociationInfoViewService } from 'cca-api';

import { mockSectors } from '../specs/fixtures/mock';
import { SectorListComponent } from './sector-list.component';

describe('SectorListComponenet', () => {
  let fixture: ComponentFixture<SectorListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorListComponent],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        {
          provide: SectorAssociationInfoViewService,
          useValue: {
            getSectorAssociations: jest.fn().mockReturnValue(of(mockSectors)),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SectorListComponent);
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should render title', () => {
    expect(fixture.nativeElement.textContent).toContain('Manage Sectors');
  });

  it('should render the sectors table', () => {
    const content = fixture.nativeElement.textContent;
    expect(content).toContain('Sector');
    expect(content).toContain('Main Contact');
    expect(fixture.nativeElement.querySelector('govuk-table')).toBeTruthy();
  });

  it('should populate the table accordingly', async () => {
    fixture.detectChanges();
    await fixture.whenStable();

    const table = fixture.nativeElement.querySelector('govuk-table');
    mockSectors.forEach((sector) => {
      const sectorEl = fixture.nativeElement.textContent;
      const mainContactEl = fixture.nativeElement.textContent;
      expect(sectorEl).toContain(sector.sector);
      expect(mainContactEl).toContain(sector.mainContact);
      expect(table.textContent).toContain(sector.sector);
    });
  });

  it('should have a table that has sortable columns', async () => {
    const allHeaders = Array.from(fixture.nativeElement.querySelectorAll('th'));
    const sectorHeaderEl = allHeaders.find((el: any) => el.textContent.includes('Sector'));
    expect(sectorHeaderEl).toBeTruthy();
    const mainContactHeaderEl = allHeaders.find((el: any) => el.textContent.includes('Main Contact'));
    expect(mainContactHeaderEl).toBeTruthy();
  });

  it('should sort columns based on sector', async () => {
    const component = fixture.componentInstance as any;
    const sectors = JSON.parse(JSON.stringify(mockSectors));

    // asc sorting
    component.sortBy({ column: 'sector', direction: 'ascending' });
    fixture.detectChanges();
    await fixture.whenStable();
    sectors.sort((a, b) => a.sector.localeCompare(b.sector, 'en-GB', { numeric: true, sensitivity: 'base' }));

    let rows = fixture.nativeElement.querySelectorAll('tr td:first-child a');
    rows.forEach((row, idx) => expect(row.textContent.trim()).toEqual(sectors[idx].sector));

    // desc sorting
    component.sortBy({ column: 'sector', direction: 'descending' });
    fixture.detectChanges();
    await fixture.whenStable();
    sectors.sort((a, b) => b.sector.localeCompare(a.sector, 'en-GB', { numeric: true, sensitivity: 'base' }));

    rows = fixture.nativeElement.querySelectorAll('tr td:first-child a');
    rows.forEach((row, idx) => expect(row.textContent.trim()).toEqual(sectors[idx].sector));
  });

  it('should sort columns based on main contact', async () => {
    const component = fixture.componentInstance as any;
    const sectors = JSON.parse(JSON.stringify(mockSectors));

    // asc sorting
    component.sortBy({ column: 'mainContact', direction: 'ascending' });
    fixture.detectChanges();
    await fixture.whenStable();
    sectors.sort((a, b) => a.mainContact.localeCompare(b.mainContact, 'en-GB', { numeric: true, sensitivity: 'base' }));

    let rows = fixture.nativeElement.querySelectorAll('tr td:nth-child(2)');
    rows.forEach((row, idx) => expect(row.textContent.trim()).toEqual(sectors[idx].mainContact));

    // desc sorting
    component.sortBy({ column: 'mainContact', direction: 'descending' });
    fixture.detectChanges();
    await fixture.whenStable();
    sectors.sort((a, b) => b.mainContact.localeCompare(a.mainContact, 'en-GB', { numeric: true, sensitivity: 'base' }));

    rows = fixture.nativeElement.querySelectorAll('tr td:nth-child(2)');
    rows.forEach((row, idx) => expect(row.textContent.trim()).toEqual(sectors[idx].mainContact));
  });
});
