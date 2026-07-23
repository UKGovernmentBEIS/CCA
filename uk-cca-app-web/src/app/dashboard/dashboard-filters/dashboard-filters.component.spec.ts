import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';

import { AuthStore } from '@netz/common/auth';
import { getByRole, getByText } from '@testing';

import { DashboardFiltersComponent } from './dashboard-filters.component';

describe('DashboardFiltersComponent', () => {
  let component: DashboardFiltersComponent;
  let fixture: ComponentFixture<DashboardFiltersComponent>;
  let authStore: AuthStore;
  let mockRouter: { navigate: ReturnType<typeof vi.fn> };
  let mockActivatedRoute: {
    snapshot: {
      queryParamMap: ReturnType<typeof convertToParamMap>;
      fragment: string | null;
    };
  };

  beforeEach(async () => {
    mockRouter = { navigate: vi.fn() };
    mockActivatedRoute = {
      snapshot: {
        queryParamMap: convertToParamMap({
          searchTerm: 'steel',
          requestType: 'ADMIN_TERMINATION',
          orderBy: 'OLDEST_FIRST',
        }),
        fragment: null,
      },
    };

    await TestBed.configureTestingModule({
      imports: [DashboardFiltersComponent],
      providers: [
        FormBuilder,
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'REGULATOR' });

    fixture = TestBed.createComponent(DashboardFiltersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize filtersForm with valid query parameters', () => {
    expect(component.filtersForm.getRawValue()).toEqual({
      searchTerm: 'steel',
      requestType: 'ADMIN_TERMINATION',
      orderBy: 'OLDEST_FIRST',
    });
  });

  it('should render the filter controls', () => {
    expect(getByText('Filters', fixture.nativeElement)).toBeTruthy();
    expect(
      getByText('Target unit ID, target unit name, facility ID, site name or sector ID', fixture.nativeElement),
    ).toBeTruthy();
    expect(getByText('Filter by workflow', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Sort by', fixture.nativeElement)).toBeTruthy();
    expect(getByRole('button', { name: /Apply/i }, fixture.nativeElement)).toBeTruthy();
    expect(getByRole('button', { name: /Clear/i }, fixture.nativeElement)).toBeTruthy();
  });

  it('should render regulator workflow filter options alphabetically', () => {
    const workflowSelect = fixture.nativeElement.querySelector('select') as HTMLSelectElement;
    const optionLabels = Array.from(workflowSelect.options).map((option) => option.textContent.trim());

    expect(optionLabels).toEqual([
      'All',
      'Admin termination',
      'CCA3 migration',
      'Facility audit',
      'Non-compliance',
      'Underlying agreement application',
      'Variation',
    ]);
  });

  it('should render sector user workflow filter options alphabetically', () => {
    authStore.setUserState({ roleType: 'SECTOR_USER' });
    fixture.detectChanges();

    const workflowSelect = fixture.nativeElement.querySelector('select') as HTMLSelectElement;
    const optionLabels = Array.from(workflowSelect.options).map((option) => option.textContent.trim());

    expect(optionLabels).toEqual([
      'All',
      'Target unit account creation',
      'TP reporting (TP6) - Download spreadsheets',
      'TP reporting (TP6) - Upload spreadsheets',
      'TP reporting (TP7, TP8, TP9) - Submit form',
      'TP reporting (TP7, TP8, TP9) - Upload CSV file',
      'Underlying agreement application',
      'Variation',
    ]);
  });

  it('should render no workflow filter options for other user roles', () => {
    authStore.setUserState({ roleType: 'OPERATOR' });
    fixture.detectChanges();

    const workflowSelect = fixture.nativeElement.querySelector('select') as HTMLSelectElement;

    expect(workflowSelect.options).toHaveLength(0);
  });

  it('should navigate with form values and reset page on apply', () => {
    component.filtersForm.setValue({
      searchTerm: 'cement',
      requestType: 'PERFORMANCE_DATA_UPLOAD',
      orderBy: 'OLDEST_FIRST',
    });

    component.apply();

    expect(mockRouter.navigate).toHaveBeenCalledWith([], {
      queryParams: {
        searchTerm: 'cement',
        requestType: 'PERFORMANCE_DATA_UPLOAD',
        orderBy: 'OLDEST_FIRST',
        page: 1,
      },
      queryParamsHandling: 'merge',
      relativeTo: mockActivatedRoute,
      fragment: null,
    });
  });

  it('should clear filters and reset page', () => {
    component.filtersForm.setValue({
      searchTerm: 'cement',
      requestType: 'PERFORMANCE_DATA_UPLOAD',
      orderBy: 'OLDEST_FIRST',
    });

    component.clear();

    expect(component.filtersForm.getRawValue()).toEqual({
      searchTerm: null,
      requestType: null,
      orderBy: 'NEWEST_FIRST',
    });
    expect(mockRouter.navigate).toHaveBeenCalledWith([], {
      queryParams: {
        searchTerm: null,
        requestType: null,
        orderBy: 'NEWEST_FIRST',
        page: 1,
      },
      queryParamsHandling: 'merge',
      relativeTo: mockActivatedRoute,
      fragment: null,
    });
  });
});
