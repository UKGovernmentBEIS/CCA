import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

import { SubsistenceFeesMoAInfoViewService, SubsistenceFeesMoATargetUnitInfoViewService } from 'cca-api';

import {
  mockFacilitiesList,
  mockSentSubsistenceFeesDetails,
  mockTargetUnitFacilitiesListSearchResult,
  mockTuMoaDetails,
} from '../../testing/mock-data';
import { TuMoaDetailsComponent } from './tu-moa-details.component';
import { toTuMoaDetailsSummary } from './tu-moa-details-summary';

describe('TuMoaDetailsComponent', () => {
  let component: TuMoaDetailsComponent;
  let fixture: ComponentFixture<TuMoaDetailsComponent>;
  let subsistenceFeesMoAInfoViewService: Partial<jest.Mocked<SubsistenceFeesMoAInfoViewService>>;
  let subsistenceFeesMoATargetUnitInfoViewService: Partial<jest.Mocked<SubsistenceFeesMoATargetUnitInfoViewService>>;

  beforeEach(async () => {
    subsistenceFeesMoAInfoViewService = {
      getSubsistenceFeesMoaDetailsById: jest.fn().mockReturnValue(of(mockTuMoaDetails)),
    };

    subsistenceFeesMoATargetUnitInfoViewService = {
      getSubsistenceFeesMoaFacilities: jest.fn().mockReturnValue(of(mockTargetUnitFacilitiesListSearchResult)),
    };

    await TestBed.configureTestingModule({
      imports: [TuMoaDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: SubsistenceFeesMoAInfoViewService, useValue: subsistenceFeesMoAInfoViewService },
        { provide: SubsistenceFeesMoATargetUnitInfoViewService, useValue: subsistenceFeesMoATargetUnitInfoViewService },
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            subFeesDetails: mockSentSubsistenceFeesDetails,
          }),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TuMoaDetailsComponent);
    component = fixture.componentInstance;
    component.data = toTuMoaDetailsSummary(mockTuMoaDetails);
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
          'Target unit MoA file',
          'Payment request date',
          'Payment status',
          'Amount per facility (GBP)',
          'Facilities marked as paid',
          'Initial total amount (GBP)',
          'Current total amount (GBP)',
          'Received amount (GBP)',
        ],
        [
          'ADS_53-T00001',
          'tu53-oper1',
          '2025 Target Unit MoA - ADS_53-T00001 - CCATM01200.pdf',
          '27 Feb 2025',
          'Awaiting payment',
          '370',
          '0',
          '370',
          '370',
          '0',
        ],
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
