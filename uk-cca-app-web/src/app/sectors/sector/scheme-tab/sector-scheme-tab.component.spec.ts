import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

import { SectorAssociationSchemeService } from 'cca-api';

import { mockSectorScheme } from '../../specs/fixtures/mock';
import { SectorSchemeTabComponent } from './sector-scheme-tab.component';

describe('SectorSchemeTabComponent', () => {
  let fixture: ComponentFixture<SectorSchemeTabComponent>;
  let sectorAssociationAuthoritiesService: Partial<jest.Mocked<SectorAssociationSchemeService>>;

  beforeEach(async () => {
    sectorAssociationAuthoritiesService = {
      getSectorAssociationSchemeBySectorAssociationId: jest.fn().mockReturnValue(of(mockSectorScheme)),
    };

    await TestBed.configureTestingModule({
      imports: [SectorSchemeTabComponent],
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ id: 1 }) },
        { provide: SectorAssociationSchemeService, useValue: sectorAssociationAuthoritiesService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SectorSchemeTabComponent);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('should render all titles', () => {
    expect(getByText('Scheme')).toBeTruthy();
    expect(getByText('CCA2 (2013-2024)')).toBeTruthy();
    expect(getByText('Subsectors')).toBeTruthy();
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
    expect((hint as HTMLElement | null)?.textContent ?? '').toContain(
      'You can find more information about currency and sector commitment in the subsector',
    );
  });

  it('should populate the subsector table accordingly', async () => {
    const table = document.querySelector('govuk-table');

    for (const subsector of mockSectorScheme.subsectorAssociations) {
      const schemeEl = getByText(subsector.name);
      expect(table?.contains(schemeEl)).toBe(true);
    }
  });
});
