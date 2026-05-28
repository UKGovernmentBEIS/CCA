import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockTprRequestTaskState } from '../../../testing/mock-data';
import { EnergyFuelAmountDetailsSummaryComponent } from './energy-fuel-amount-details-summary.component';

describe('EnergyFuelAmountDetailsSummaryComponent', () => {
  let component: EnergyFuelAmountDetailsSummaryComponent;
  let fixture: ComponentFixture<EnergyFuelAmountDetailsSummaryComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnergyFuelAmountDetailsSummaryComponent],
      providers: [
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Report energy/fuel consumption' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockTprRequestTaskState);

    fixture = TestBed.createComponent(EnergyFuelAmountDetailsSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the summary section heading', () => {
    const element: HTMLElement = fixture.nativeElement;
    expect(element.textContent).toContain('Energy/fuel amount consumed');
  });

  it('should display fuel data from the store', () => {
    const element: HTMLElement = fixture.nativeElement;
    expect(element.textContent).toContain('Natural gas');
    expect(element.textContent).toContain('Grid electricity');
  });
});
