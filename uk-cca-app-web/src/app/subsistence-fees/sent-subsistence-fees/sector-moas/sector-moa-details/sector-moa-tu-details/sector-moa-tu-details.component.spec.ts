import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

import { SubsistenceFeesMoATargetUnitInfoViewService } from 'cca-api';

import {
  mockFacilitiesList,
  mockMoATargetUnitDetails,
  mockSectorMoaDetails,
  mockSentSubsistenceFeesDetails,
  mockTargetUnitFacilitiesListSearchResult,
} from '../../../testing/mock-data';
import { SectorMoaTuDetailsComponent } from './sector-moa-tu-details.component';
import { toSectorMoaTUDetailsSummary } from './sector-moa-tu-details-summary';

describe('SectorMoaTuDetailsComponent', () => {
  let component: SectorMoaTuDetailsComponent;
  let fixture: ComponentFixture<SectorMoaTuDetailsComponent>;
  let subsistenceFeesMoATargetUnitInfoViewService: Partial<jest.Mocked<SubsistenceFeesMoATargetUnitInfoViewService>>;

  beforeEach(async () => {
    subsistenceFeesMoATargetUnitInfoViewService = {
      getSubsistenceFeesMoaTargetUnitDetailsById: jest.fn().mockReturnValue(of(mockMoATargetUnitDetails)),
      getSubsistenceFeesMoaFacilities: jest.fn().mockReturnValue(of(mockTargetUnitFacilitiesListSearchResult)),
    };

    await TestBed.configureTestingModule({
      imports: [SectorMoaTuDetailsComponent],
      providers: [
        { provide: SubsistenceFeesMoATargetUnitInfoViewService, useValue: subsistenceFeesMoATargetUnitInfoViewService },
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            subFeesDetails: mockSentSubsistenceFeesDetails,
            sectorMoaDetails: mockSectorMoaDetails,
          }),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SectorMoaTuDetailsComponent);
    component = fixture.componentInstance;
    component.data = toSectorMoaTUDetailsSummary(mockMoATargetUnitDetails);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct data', () => {
    const detailsValues = screen
      .getAllByText((_, el) => el.tagName.toLowerCase() === 'dl')
      .map((el) => [
        Array.from(el.querySelectorAll('dt')).map((dt) => dt.textContent.trim()),
        Array.from(el.querySelectorAll('dd'))
          .filter((dt) => dt.textContent.trim() !== 'Change')
          .map((dt) => dt.textContent.trim()),
      ]);

    expect(detailsValues).toEqual([
      [
        [
          'Target unit ID',
          'Operator',
          'Payment request date',
          'Marking of facilities',
          'Facilities marked as paid',
          'Amount per facility (GBP)',
          'Initial total amount (GBP)',
          'Current total amount (GBP)',
        ],
        ['ADS_52-T00001', 'tu52-oper1', '13 Mar 2025', 'In progress', '0 out of 2', '185', '370', '370'],
      ],
    ]);
  });

  it('should populate with correct number of table rows', () => {
    component.state.set({
      currentPage: 0,
      pageSize: 30,
      facilities: mockFacilitiesList,
      totalItems: mockFacilitiesList.length,
    });

    fixture.detectChanges();

    expect(document.querySelectorAll('.govuk-table__row')).toHaveLength(component.state().totalItems + 1);
  });
});
