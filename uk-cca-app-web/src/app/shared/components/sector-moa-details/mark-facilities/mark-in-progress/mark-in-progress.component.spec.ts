import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { SectorMoaDetailsStore } from '../../sector-moa-details.store';
import { mockSectorMoaDetails, mockTargetUnitsList } from '../../testing/mock-data';
import { MarkInProgressComponent } from './mark-in-progress.component';

describe('MarkInProgressComponent', () => {
  let component: MarkInProgressComponent;
  let fixture: ComponentFixture<MarkInProgressComponent>;
  let sectorMoaDetailsStore: SectorMoaDetailsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MarkInProgressComponent],
      providers: [
        SectorMoaDetailsStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    sectorMoaDetailsStore = TestBed.inject(SectorMoaDetailsStore);
    sectorMoaDetailsStore.setState({
      userRoleType: 'REGULATOR',
      sectorMoaDetails: mockSectorMoaDetails,
      targetUnits: mockTargetUnitsList,
      totalTUItems: 0,
      selectedTUs: new Map(),
    });

    fixture = TestBed.createComponent(MarkInProgressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display single TU selection confirmation when one TU is selected', () => {
    const selectedUnit = mockTargetUnitsList[0];
    sectorMoaDetailsStore.setState({
      userRoleType: 'REGULATOR',
      sectorMoaDetails: mockSectorMoaDetails,
      targetUnits: mockTargetUnitsList,
      totalTUItems: 100,
      selectedTUs: new Map([[selectedUnit.businessId, selectedUnit]]),
    });

    fixture.detectChanges();

    const heading = fixture.debugElement.query(By.css('[data-testid="page-heading"]'));
    expect(heading.nativeElement.textContent).toContain(selectedUnit.businessId);

    const targetUnitId = fixture.debugElement.query(By.css('dd')).nativeElement;
    expect(targetUnitId.textContent.trim()).toBe('ADS_1 - Aerospace_1');
  });

  it('should display multiple TUs confirmation when multiple TUs are selected', () => {
    const selectedUnits = mockTargetUnitsList.slice(0, 2);
    sectorMoaDetailsStore.setState({
      userRoleType: 'REGULATOR',
      sectorMoaDetails: mockSectorMoaDetails,
      targetUnits: mockTargetUnitsList,
      totalTUItems: 100,
      selectedTUs: new Map(selectedUnits.map((unit) => [unit.businessId, unit])),
    });

    fixture.detectChanges();

    const heading = fixture.debugElement.query(By.css('[data-testid="page-heading"]'));
    expect(heading.nativeElement.textContent.trim()).toBe(
      'Are you sure you want to mark the 2 selected target units and all their facilities as In progress?',
    );
  });
});
