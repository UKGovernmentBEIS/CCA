import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';

import { TargetPeriodPerformanceDataReportOfTheFacilityService } from 'cca-api';

import { FacilityTargetPeriodReportStore } from '../../../facility-target-period-report.store';
import { mockFacilityTPRStore } from '../../testing/mock-data';
import { VariationSubmissionComponent } from './variation-submission.component';

describe('VariationSubmissionComponent', () => {
  let component: VariationSubmissionComponent;
  let fixture: ComponentFixture<VariationSubmissionComponent>;
  let store: FacilityTargetPeriodReportStore;
  let router: Router;
  let navigateSpy: ReturnType<typeof vi.spyOn>;
  let mockApiService: Partial<TargetPeriodPerformanceDataReportOfTheFacilityService>;

  const targetPeriodYear = mockFacilityTPRStore.statusInfo[0].targetPeriodYear;

  beforeEach(async () => {
    mockApiService = {
      updateFacilityPerformanceDataVariationIndicator: vi.fn().mockReturnValue(of(undefined)),
    };

    await TestBed.configureTestingModule({
      imports: [VariationSubmissionComponent],
      providers: [
        FacilityTargetPeriodReportStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub({ facilityId: '123', targetPeriodYear: String(targetPeriodYear) }),
        },
        {
          provide: TargetPeriodPerformanceDataReportOfTheFacilityService,
          useValue: mockApiService,
        },
      ],
    }).compileComponents();

    store = TestBed.inject(FacilityTargetPeriodReportStore);
    store.setState(structuredClone(mockFacilityTPRStore));

    router = TestBed.inject(Router);
    navigateSpy = vi.spyOn(router, 'navigate');

    fixture = TestBed.createComponent(VariationSubmissionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    navigateSpy.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    const heading = fixture.nativeElement.querySelector('netz-page-heading');
    expect(heading).toBeTruthy();
    expect(heading.textContent).toContain('Variation submission');
  });

  it('should render the form', () => {
    const formElement = fixture.nativeElement.querySelector('form');
    expect(formElement).toBeTruthy();
  });

  it('should not submit when the form is invalid (no option selected)', () => {
    // Reset the form to have no value selected
    component.form.controls.toggle.setValue(null);
    fixture.detectChanges();

    component.onSubmit();

    expect(mockApiService.updateFacilityPerformanceDataVariationIndicator).not.toHaveBeenCalled();
  });

  it('should call the API with the correct payload on valid submission', () => {
    component.form.controls.toggle.setValue(true);
    fixture.detectChanges();

    component.onSubmit();

    expect(mockApiService.updateFacilityPerformanceDataVariationIndicator).toHaveBeenCalledWith(123, {
      targetPeriodYear,
      variationIndicator: true,
    });
  });

  it('should update the store replacing only the matching statusInfo entry on success', () => {
    // Add a second target period to the store to verify selective replacement
    const secondPeriod = {
      ...mockFacilityTPRStore.statusInfo[0],
      targetPeriodYear: 2028,
      targetPeriodName: 'TP9 (2028)',
      variationIndicator: true,
    };
    store.setState({
      ...structuredClone(mockFacilityTPRStore),
      statusInfo: [...structuredClone(mockFacilityTPRStore.statusInfo), secondPeriod],
    });

    // Re-create the component so it picks up the new store state
    fixture = TestBed.createComponent(VariationSubmissionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    component.form.controls.toggle.setValue(false);
    fixture.detectChanges();

    component.onSubmit();

    const updatedState = store.state;
    expect(updatedState.statusInfo.length).toBe(2);

    const updatedEntry = updatedState.statusInfo.find((i) => i.targetPeriodYear === targetPeriodYear);
    expect(updatedEntry.variationIndicator).toBe(false);

    const untouchedEntry = updatedState.statusInfo.find((i) => i.targetPeriodYear === 2028);
    expect(untouchedEntry.variationIndicator).toBe(true);
  });
});
