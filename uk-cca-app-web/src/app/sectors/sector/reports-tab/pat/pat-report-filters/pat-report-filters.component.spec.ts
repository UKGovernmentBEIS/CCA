import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, convertToParamMap, ParamMap, Router } from '@angular/router';

import { BehaviorSubject, of } from 'rxjs';

import { getByLabelText, getByRole, getByText, queryByLabelText } from '@testing';

import { PatReportFiltersComponent } from './pat-report-filters.component';

describe('PatReportFiltersComponent', () => {
  let component: PatReportFiltersComponent;
  let fixture: ComponentFixture<PatReportFiltersComponent>;
  let queryParamMap$: BehaviorSubject<ParamMap>;

  const facilityQueryParams = {
    reportType: 'PAT',
    targetPeriodYear: '2026',
    term: 'ADS-F00040',
    status: 'SUBMITTED',
  };

  async function setup(queryParams: Record<string, string> = facilityQueryParams) {
    queryParamMap$ = new BehaviorSubject(convertToParamMap(queryParams));
    const mockActivatedRoute = {
      snapshot: {
        queryParams,
        paramMap: convertToParamMap({ page: '1', sectorId: '1' }),
        queryParamMap: convertToParamMap(queryParams),
      },
      queryParams: of(queryParams),
      queryParamMap: queryParamMap$,
    };

    await TestBed.configureTestingModule({
      imports: [PatReportFiltersComponent],
      providers: [
        FormBuilder,
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PatReportFiltersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create', async () => {
    await setup();

    expect(component).toBeTruthy();
  });

  it('should initialize filtersForm with correct query parameters', async () => {
    await setup();

    expect(component.filtersForm.value).toEqual({
      term: 'ADS-F00040',
      status: 'SUBMITTED',
    });
  });

  it('should render the "Filters" section', async () => {
    await setup();

    expect(getByText('Filters')).toBeTruthy();
  });

  it('should sync filters and the label when query parameters change', async () => {
    await setup();

    queryParamMap$.next(
      convertToParamMap({
        ...facilityQueryParams,
        targetPeriodYear: '2024',
        term: 'ADS-T00040',
        status: 'OUTSTANDING',
      }),
    );
    fixture.detectChanges();

    expect(component.filtersForm.value).toEqual({ term: 'ADS-T00040', status: 'OUTSTANDING' });
    expect(getByLabelText('TU ID')).toBeTruthy();
  });

  it('should search on facility or target unit id for CCA3 years', async () => {
    await setup();

    expect(getByLabelText('Facility ID or TU ID')).toBeTruthy();
    expect(queryByLabelText('TU ID')).toBeNull();
  });

  it('should search on target unit id only for 2024', async () => {
    await setup({ ...facilityQueryParams, targetPeriodYear: '2024', term: 'ADS-T00040' });

    expect(getByLabelText('TU ID')).toBeTruthy();
    expect(queryByLabelText('Facility ID or TU ID')).toBeNull();
  });

  it('should have a select dropdown for "Status"', async () => {
    await setup();

    const statusSelect = getByLabelText('Status') as HTMLSelectElement;
    const optionTexts = Array.from(statusSelect.options).map((option) => option.textContent?.trim());

    expect(optionTexts).toEqual(['All', 'Submitted', 'Outstanding']);
  });

  it('should ignore a status that is not a PAT status', async () => {
    await setup({ ...facilityQueryParams, status: 'TARGET_MET' });

    expect(component.filtersForm.value.status).toBeNull();
  });

  it('should have a submit button with "Apply"', async () => {
    await setup();

    expect(getByRole('button', { name: /Apply/i })).toBeTruthy();
  });

  it('should have a clear button with "Clear"', async () => {
    await setup();

    expect(getByRole('button', { name: /Clear/i })).toBeTruthy();
  });

  it('should apply the ID and status filters and reset pagination', async () => {
    await setup();
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    component.filtersForm.setValue({ term: 'ADS-T00040', status: 'OUTSTANDING' });

    component.apply();

    expect(navigate).toHaveBeenCalledWith([], {
      queryParams: { reportType: 'PAT', term: 'ADS-T00040', status: 'OUTSTANDING', page: 1 },
      queryParamsHandling: 'merge',
      relativeTo: TestBed.inject(ActivatedRoute),
      fragment: 'reports',
    });
  });

  it('should clear the filters while preserving the selected year', async () => {
    await setup();
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

    component.clear();

    expect(component.filtersForm.value).toEqual({ term: null, status: null });
    expect(navigate).toHaveBeenCalledWith([], {
      queryParams: { reportType: 'PAT', term: null, status: null, page: 1 },
      queryParamsHandling: 'merge',
      relativeTo: TestBed.inject(ActivatedRoute),
      fragment: 'reports',
    });
  });
});
