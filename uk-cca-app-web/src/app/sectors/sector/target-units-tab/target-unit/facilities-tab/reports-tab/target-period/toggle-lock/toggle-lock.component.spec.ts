import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { FacilityTargetPeriodReportStore } from '../../../facility-target-period-report.store';
import { mockFacilityTPRStore } from '../../testing/mock-data';
import { ToggleLockComponent } from './toggle-lock.component';

describe('UnlockPerformanceReportComponent', () => {
  let component: ToggleLockComponent;
  let fixture: ComponentFixture<ToggleLockComponent>;
  let store: FacilityTargetPeriodReportStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToggleLockComponent],
      providers: [
        FacilityTargetPeriodReportStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(FacilityTargetPeriodReportStore);
    store.setState(mockFacilityTPRStore);

    fixture = TestBed.createComponent(ToggleLockComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render form', () => {
    const formElement = fixture.nativeElement.querySelector('form');
    expect(formElement).toBeTruthy();
  });

  it('should have a submit button', () => {
    const buttonElement = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(buttonElement).toBeTruthy();
  });

  it('should call onSubmit when form is submitted', () => {
    vi.spyOn(component, 'onSubmit');
    const formElement = fixture.nativeElement.querySelector('form');
    formElement.dispatchEvent(new Event('submit'));
    fixture.detectChanges();
    expect(component.onSubmit).toHaveBeenCalled();
  });
});
