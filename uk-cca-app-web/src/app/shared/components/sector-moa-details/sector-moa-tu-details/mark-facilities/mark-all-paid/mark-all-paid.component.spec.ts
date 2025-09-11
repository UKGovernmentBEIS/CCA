import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { mockFacilitiesList, mockTuMoaDetails } from '../../../testing/mock-data';
import { SectorMoaTUDetailsStore } from '../../sector-moa-tu-details.store';
import { MarkAllPaidComponent } from './mark-all-paid.component';

describe('MarkAllPaidComponent', () => {
  let component: MarkAllPaidComponent;
  let fixture: ComponentFixture<MarkAllPaidComponent>;
  let sectorMoaTUDetailsStore: SectorMoaTUDetailsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MarkAllPaidComponent],
      providers: [
        SectorMoaTUDetailsStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    sectorMoaTUDetailsStore = TestBed.inject(SectorMoaTUDetailsStore);
    sectorMoaTUDetailsStore.setState({
      userRoleType: 'REGULATOR',
      totalFacilityItems: 0,
      moaTUDetails: mockTuMoaDetails,
      facilities: mockFacilitiesList,
      selectedFacilities: undefined,
    });

    fixture = TestBed.createComponent(MarkAllPaidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all TUs confirmation when all "Mark all as paid" link is selected', () => {
    sectorMoaTUDetailsStore.setState({
      userRoleType: 'REGULATOR',
      totalFacilityItems: 100,
      moaTUDetails: mockTuMoaDetails,
      facilities: mockFacilitiesList,
      selectedFacilities: new Map(mockFacilitiesList.map((unit) => [unit.facilityId, unit])),
    });

    fixture.detectChanges();

    const heading = fixture.debugElement.query(By.css('[data-testid="page-heading"]'));
    expect(heading.nativeElement.textContent.trim()).toBe(
      `Are you sure you want to mark all the facilities of ${mockTuMoaDetails.businessId} target unit as Paid?`,
    );
  });
});
