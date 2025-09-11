import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { mockFacilitiesList, mockSentSubsistenceFeesDetails, mockTuMoaDetails } from '@shared/components';

import { TuMoaDetailsComponent } from './tu-moa-details.component';
import { TuMoaDetailsStore } from './tu-moa-details.store';

describe('TuMoaDetailsComponent', () => {
  let component: TuMoaDetailsComponent;
  let fixture: ComponentFixture<TuMoaDetailsComponent>;
  let tuMoaDetailsStore: TuMoaDetailsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TuMoaDetailsComponent],
      providers: [
        TuMoaDetailsStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            subFeesDetails: mockSentSubsistenceFeesDetails,
          }),
        },
      ],
    }).compileComponents();

    tuMoaDetailsStore = TestBed.inject(TuMoaDetailsStore);
    tuMoaDetailsStore.setState({
      userRoleType: 'REGULATOR',
      totalFacilityItems: 0,
      moaTUDetails: mockTuMoaDetails,
      facilities: mockFacilitiesList,
      selectedFacilities: new Map(),
    });

    fixture = TestBed.createComponent(TuMoaDetailsComponent);
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
          '185',
          '0 out of 2',
          'Mark all as paid',
          '370',
          '370',
          '0',
        ],
      ],
    ]);
  });

  it('should populate with correct number of table rows', () => {
    expect(document.querySelectorAll('.govuk-table__row')).toHaveLength(component.state().facilities.length + 1);
  });
});
