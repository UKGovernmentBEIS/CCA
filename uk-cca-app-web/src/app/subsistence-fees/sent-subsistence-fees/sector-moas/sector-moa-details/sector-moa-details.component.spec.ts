import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

import { SubsistenceFeesMoAInfoViewService } from 'cca-api';

import {
  mockSectorMoaDetails,
  mockSentSubsistenceFeesDetails,
  mockTargetUnitsList,
  mockTargetUnitsListSearchResult,
} from '../../testing/mock-data';
import { SectorMoaDetailsComponent } from './sector-moa-details.component';

describe('SectorMoaDetailsComponent', () => {
  let component: SectorMoaDetailsComponent;
  let fixture: ComponentFixture<SectorMoaDetailsComponent>;
  let subsistenceFeesMoAInfoViewService: Partial<jest.Mocked<SubsistenceFeesMoAInfoViewService>>;

  beforeEach(async () => {
    subsistenceFeesMoAInfoViewService = {
      getSubsistenceFeesMoaTargetUnits: jest.fn().mockReturnValue(of(mockTargetUnitsListSearchResult)),
    };

    await TestBed.configureTestingModule({
      imports: [SectorMoaDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: SubsistenceFeesMoAInfoViewService, useValue: subsistenceFeesMoAInfoViewService },
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            subFeesDetails: mockSentSubsistenceFeesDetails,
            sectorMoaDetails: mockSectorMoaDetails,
          }),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SectorMoaDetailsComponent);
    component = fixture.componentInstance;
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
          'Sector',
          'Sector MoA file',
          'Payment request date',
          'Payment status',
          'Facilities marked as paid',
          'Initial total amount (GBP)',
          'Current total amount (GBP)',
          'Received amount (GBP)',
        ],
        [
          'Aerospace_1',
          '2025 Sector MoA - ADS_1 - CCACM01201.pdf',
          '27 Feb 2025',
          'Awaiting payment',
          '0 out of 1',
          '185',
          '185',
          '0',
        ],
      ],
    ]);
  });

  it('should populate with correct number of table rows', () => {
    component.state.set({
      currentPage: 0,
      pageSize: 30,
      targetUnits: mockTargetUnitsList,
      totalItems: mockTargetUnitsList.length,
    });

    fixture.detectChanges();

    expect(document.querySelectorAll('.govuk-table__row')).toHaveLength(component.state().totalItems + 1);
  });
});
