import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { SectorMoaDetailsStore } from '../../sector-moa-details.store';
import { mockSectorMoaDetails, mockTargetUnitsList } from '../../testing/mock-data';
import { MarkAllPaidComponent } from './mark-all-paid.component';

describe('MarkAllPaidComponent', () => {
  let component: MarkAllPaidComponent;
  let fixture: ComponentFixture<MarkAllPaidComponent>;
  let sectorMoaDetailsStore: SectorMoaDetailsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MarkAllPaidComponent],
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

    fixture = TestBed.createComponent(MarkAllPaidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all TUs confirmation when "Mark all as paid" link is selected', () => {
    sectorMoaDetailsStore.setState({
      userRoleType: 'REGULATOR',
      sectorMoaDetails: mockSectorMoaDetails,
      targetUnits: mockTargetUnitsList,
      totalTUItems: 100,
      selectedTUs: undefined,
    });

    fixture.detectChanges();

    const heading = fixture.debugElement.query(By.css('[data-testid="page-heading"]'));
    expect(heading.nativeElement.textContent.trim()).toBe(
      `Are you sure you want to mark all target units of ${mockSectorMoaDetails.businessId} - ${mockSectorMoaDetails.name} sector and all their facilities as Paid?`,
    );
  });
});
