import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { mockFacilitiesList, mockTuMoaDetails } from '../../../testing/mock-data';
import { SectorMoaTUDetailsStore } from '../../sector-moa-tu-details.store';
import { ConfirmationComponent } from './confirmation.component';

describe('ConfirmationComponent', () => {
  let component: ConfirmationComponent;
  let fixture: ComponentFixture<ConfirmationComponent>;
  let sectorMoaTUDetailsStore: SectorMoaTUDetailsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmationComponent],
      providers: [
        SectorMoaTUDetailsStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ type: 'paid' }) },
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

    fixture = TestBed.createComponent(ConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display confirmation when the selected facilities are successfully marked', () => {
    const message = fixture.debugElement.query(By.css('strong'));
    expect(message.nativeElement.textContent).toBe('The selected facilities have been marked as Paid');
  });
});
