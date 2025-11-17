import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { mockFacilitiesList, mockTuMoaDetails } from '../../../testing/mock-data';
import { SectorMoaTUDetailsStore } from '../../sector-moa-tu-details.store';
import { MarkInProgressComponent } from './mark-in-progress.component';

describe('MarkInProgressComponent', () => {
  let component: MarkInProgressComponent;
  let fixture: ComponentFixture<MarkInProgressComponent>;
  let sectorMoaTUDetailsStore: SectorMoaTUDetailsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MarkInProgressComponent],
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
      totalFacilityItems: 100,
      moaTUDetails: mockTuMoaDetails,
      facilities: mockFacilitiesList,
      selectedFacilities: new Map(),
    });

    fixture = TestBed.createComponent(MarkInProgressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display single facility selection confirmation when one facility is selected', () => {
    const selectedUnit = mockFacilitiesList[0];
    sectorMoaTUDetailsStore.setState({
      userRoleType: 'REGULATOR',
      totalFacilityItems: 100,
      moaTUDetails: mockTuMoaDetails,
      facilities: mockFacilitiesList,
      selectedFacilities: new Map([[selectedUnit.facilityBusinessId, selectedUnit]]),
    });

    fixture.detectChanges();

    const heading = fixture.debugElement.query(By.css('[data-testid="page-heading"]'));
    expect(heading.nativeElement.textContent).toContain(selectedUnit.facilityBusinessId);

    const targetUnitId = fixture.debugElement.query(By.css('dd')).nativeElement;
    expect(targetUnitId.textContent.trim()).toBe('ADS_53-T00001');
  });

  it('should display multiple facilities confirmation when multiple facilities are selected', () => {
    const selectedUnits = mockFacilitiesList.slice(0, 2);
    sectorMoaTUDetailsStore.setState({
      userRoleType: 'REGULATOR',
      totalFacilityItems: 100,
      moaTUDetails: mockTuMoaDetails,
      facilities: mockFacilitiesList,
      selectedFacilities: new Map(selectedUnits.map((unit) => [unit.facilityBusinessId, unit])),
    });

    fixture.detectChanges();

    const heading = fixture.debugElement.query(By.css('[data-testid="page-heading"]'));
    expect(heading.nativeElement.textContent.trim()).toBe(
      'Are you sure you want to mark the 2 selected facilities as In progress?',
    );
  });
});
