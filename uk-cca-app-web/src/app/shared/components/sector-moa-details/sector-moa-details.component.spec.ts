import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { SectorMoaDetailsComponent } from './sector-moa-details.component';
import { SectorMoaDetailsStore } from './sector-moa-details.store';
import { mockSectorMoaDetails, mockSentSubsistenceFeesDetails, mockTargetUnitsList } from './testing/mock-data';

describe('SectorMoaDetailsComponent', () => {
  let component: SectorMoaDetailsComponent;
  let fixture: ComponentFixture<SectorMoaDetailsComponent>;
  let sectorMoaDetailsStore: SectorMoaDetailsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorMoaDetailsComponent],
      providers: [
        SectorMoaDetailsStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub({
            subFeesDetails: mockSentSubsistenceFeesDetails,
          }),
        },
      ],
    }).compileComponents();

    sectorMoaDetailsStore = TestBed.inject(SectorMoaDetailsStore);
    sectorMoaDetailsStore.setState({
      userRoleType: '',
      sectorMoaDetails: mockSectorMoaDetails,
      targetUnits: mockTargetUnitsList,
      totalTUItems: 0,
      selectedTUs: new Map(),
    });

    fixture = TestBed.createComponent(SectorMoaDetailsComponent);
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
          'ADS_1 - Aerospace_1',
          '2025 Sector MoA - ADS_1 - CCACM01201.pdf',
          '27 Feb 2025',
          'Awaiting payment',
          '0 out of 1',
          'Mark all as paid',
          '185',
          '185',
          '0',
        ],
      ],
    ]);
  });

  it('should populate with correct number of table rows', () => {
    sectorMoaDetailsStore.updateState({
      targetUnits: mockTargetUnitsList,
      totalTUItems: mockTargetUnitsList.length,
    });

    fixture.detectChanges();

    expect(document.querySelectorAll('.govuk-table__row')).toHaveLength(component.state().totalTUItems + 1);
  });
});
