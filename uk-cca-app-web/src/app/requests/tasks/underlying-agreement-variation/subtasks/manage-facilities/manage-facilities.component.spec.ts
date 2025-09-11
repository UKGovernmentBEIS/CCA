import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { mockRequestTaskState, TaskItemStatus } from '@requests/common';

import { FacilityStatus, ManageFacilitiesComponent } from './manage-facilities.component';

describe('ManageFacilitiesComponent', () => {
  let component: ManageFacilitiesComponent;
  let fixture: ComponentFixture<ManageFacilitiesComponent>;
  let router: Router;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageFacilitiesComponent],
      providers: [RequestTaskStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    router = TestBed.inject(Router);
    store = TestBed.inject(RequestTaskStore);

    store.setState(mockRequestTaskState);

    fixture = TestBed.createComponent(ManageFacilitiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should handle form submission with filters', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const route = TestBed.inject(ActivatedRoute);
    const formValues = {
      term: 'Facility',
      status: 'LIVE' as FacilityStatus,
      workflowStatus: 'IN_PROGRESS' as TaskItemStatus,
    };

    component.searchForm.patchValue(formValues);
    component.onApplyFilters();

    expect(navigateSpy).toHaveBeenCalledWith([], {
      queryParams: { page: 1, ...formValues },
      queryParamsHandling: 'merge',
      relativeTo: route,
    });
  });

  it('should handle filter clearing', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const route = TestBed.inject(ActivatedRoute);

    component.onClearFilters();

    expect(navigateSpy).toHaveBeenCalledWith([], {
      queryParams: { page: 1, term: null, status: null, workflowStatus: null },
      queryParamsHandling: 'merge',
      relativeTo: route,
    });
  });

  it('should sort facilities correctly', () => {
    component.sorting.set({ column: 'name', direction: 'ascending' });
    fixture.detectChanges();

    const items = component.paginatedItems();
    expect(items[0].name).toBe('Facility 1');
    expect(items[1].name).toBe('Facility 2');
  });

  it('should not call navigate when invalid form is submitted', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    component.searchForm.controls.term.setValue('a'); // Less than min length of 3

    component.onApplyFilters();

    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should not trigger page change if same page is requested', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    component.currentPage.set(2);

    component.onPageChange(2);

    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should not trigger page size change if same size is requested', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    component.pageSize.set(25);

    component.onPageSizeChange(25);

    expect(navigateSpy).not.toHaveBeenCalled();
  });
});
