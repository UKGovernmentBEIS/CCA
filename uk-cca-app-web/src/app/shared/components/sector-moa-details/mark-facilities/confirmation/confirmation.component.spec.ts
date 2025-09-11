import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { SectorMoaDetailsStore } from '../../sector-moa-details.store';
import { mockSectorMoaDetails, mockTargetUnitsList } from '../../testing/mock-data';
import { ConfirmationComponent } from './confirmation.component';

describe('ConfirmationComponent', () => {
  let component: ConfirmationComponent;
  let fixture: ComponentFixture<ConfirmationComponent>;
  let sectorMoaDetailsStore: SectorMoaDetailsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmationComponent],
      providers: [
        SectorMoaDetailsStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ type: 'paid' }) },
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
