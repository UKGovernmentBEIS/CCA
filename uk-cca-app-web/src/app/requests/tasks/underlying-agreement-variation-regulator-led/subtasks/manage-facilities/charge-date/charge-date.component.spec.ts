import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';

import { mockRequestTaskState } from '../../../testing/mock-data';
import { ChargeDateComponent } from './charge-date.component';

describe('ChargeDateComponent', () => {
  let component: ChargeDateComponent;
  let fixture: ComponentFixture<ChargeDateComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChargeDateComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              params: { facilityId: 'FACILITY_1' },
            },
          },
        },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockRequestTaskState);

    fixture = TestBed.createComponent(ChargeDateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should disable chargeStartDate when hasChargeStartDate is false', () => {
    component.form.controls.hasChargeStartDate.setValue(false);

    expect(component.form.controls.chargeStartDate.disabled).toBe(true);
    expect(component.form.controls.chargeStartDate.value).toBeNull();
  });

  it('should enable chargeStartDate when hasChargeStartDate is true', () => {
    component.form.controls.hasChargeStartDate.setValue(false);
    component.form.controls.hasChargeStartDate.setValue(true);

    expect(component.form.controls.chargeStartDate.enabled).toBe(true);
  });
});
