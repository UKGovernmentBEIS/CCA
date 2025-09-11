import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { SectorMoaDetailsStore } from '../sector-moa-details.store';
import {
  mockFacilitiesList,
  mockSectorMoaDetails,
  mockSentSubsistenceFeesDetails,
  mockTargetUnitsList,
  mockTuMoaDetails,
} from '../testing/mock-data';
import { SectorMoaTuDetailsComponent } from './sector-moa-tu-details.component';
import { SectorMoaTUDetailsStore } from './sector-moa-tu-details.store';

describe('SectorMoaTuDetailsComponent', () => {
  let component: SectorMoaTuDetailsComponent;
  let fixture: ComponentFixture<SectorMoaTuDetailsComponent>;
  let sectorMoaDetailsStore: SectorMoaDetailsStore;
  let sectorMoaTUDetailsStore: SectorMoaTUDetailsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorMoaTuDetailsComponent],
      providers: [
        SectorMoaDetailsStore,
        SectorMoaTUDetailsStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, { subFeesDetails: mockSentSubsistenceFeesDetails }),
        },
      ],
    }).compileComponents();

    sectorMoaTUDetailsStore = TestBed.inject(SectorMoaTUDetailsStore);
    sectorMoaTUDetailsStore.setState({
      userRoleType: 'REGULATOR',
      totalFacilityItems: 0,
      moaTUDetails: mockTuMoaDetails,
      facilities: mockFacilitiesList,
      selectedFacilities: new Map(),
    });

    sectorMoaDetailsStore = TestBed.inject(SectorMoaDetailsStore);
    sectorMoaDetailsStore.setState({
      userRoleType: 'REGULATOR',
      sectorMoaDetails: mockSectorMoaDetails,
      targetUnits: mockTargetUnitsList,
      selectedTUs: new Map(),
      totalTUItems: 0,
    });

    fixture = TestBed.createComponent(SectorMoaTuDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct data', () => {
    const definitionLists = fixture.debugElement.queryAll(By.css('dl'));

    const detailsValues = definitionLists.map((dl) => {
      const terms = dl.queryAll(By.css('dt')).map((dt) => dt.nativeElement.textContent.trim());
      const descriptions = dl
        .queryAll(By.css('dd'))
        .filter((dd) => dd.nativeElement.textContent.trim() !== 'Change')
        .map((dd) => dd.nativeElement.textContent.trim());

      return [terms, descriptions];
    });

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
        [
          'ADS_53-T00001',
          'tu53-oper1',
          '27 Feb 2025',
          'In progress',
          '0 out of 2',
          'Mark all as paid',
          '185',
          '370',
          '370',
        ],
      ],
    ]);
  });

  it('should populate with correct number of table rows', () => {
    expect(document.querySelectorAll('.govuk-table__row')).toHaveLength(component.state().facilities.length + 1);
  });
});
