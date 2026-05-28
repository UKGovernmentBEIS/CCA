import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, of } from 'rxjs';

import { Mocked, MockInstance } from 'vitest';

import { FacilityInfoViewService, FacilitySearchResults } from 'cca-api';

import { FacilitiesListComponent } from './facilities-list.component';

describe('FacilitiesListComponent', () => {
  let component: FacilitiesListComponent;
  let fixture: ComponentFixture<FacilitiesListComponent>;
  let facilityInfoViewService: Mocked<FacilityInfoViewService>;
  let routerNavigateSpy: MockInstance;

  const mockFacilities: FacilitySearchResults = {
    facilities: [
      {
        id: 1,
        facilityBusinessId: 'ADS_1-F00001',
        siteName: 'fac1-1',
        schemeExitDate: new Date().toISOString(),
        status: 'LIVE',
        certificationStatus: 'CERTIFIED',
      },
      {
        id: 2,
        facilityBusinessId: 'ADS_1-F00002',
        siteName: 'fac1-2',
        schemeExitDate: new Date().toISOString(),
        status: 'INACTIVE',
        certificationStatus: 'DECERTIFIED',
      },
    ],
    total: 2,
  };

  const createQueryParamMock = (page = '1', pageSize = '50', term: string | null = null) => ({
    get: vi.fn().mockImplementation((param) => {
      if (param === 'page') return page;
      if (param === 'pageSize') return pageSize;
      if (param === 'term') return term;
      return null;
    }),
  });

  const setupComponent = (searchResults = mockFacilities) => {
    const facilityServiceMock = {
      searchFacilities: vi.fn().mockReturnValue(of(searchResults)),
    };

    const queryParamMock = createQueryParamMock();
    const queryParamSubject = new BehaviorSubject(queryParamMock);

    const activatedRouteMock = {
      snapshot: {
        paramMap: { get: vi.fn().mockReturnValue('123') },
        queryParamMap: queryParamMock,
        fragment: 'facilities',
      },
      queryParamMap: queryParamSubject.asObservable(),
    };

    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, FacilitiesListComponent],
      providers: [
        { provide: FacilityInfoViewService, useValue: facilityServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    fixture = TestBed.createComponent(FacilitiesListComponent);
    component = fixture.componentInstance;

    routerNavigateSpy = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    facilityInfoViewService = TestBed.inject(FacilityInfoViewService) as Mocked<FacilityInfoViewService>;

    return { queryParamSubject, queryParamMock };
  };

  afterEach(() => {
    routerNavigateSpy?.mockClear();
    TestBed.resetTestingModule();
  });

  describe('component initialization', () => {
    it('should create', () => {
      setupComponent();
      expect(component).toBeTruthy();
    });

    it('should load data and update state when effect triggers', () => {
      setupComponent();

      fixture.detectChanges();

      expect(facilityInfoViewService.searchFacilities).toHaveBeenCalledWith(123, 0, 50, null);
      expect(component.state().facilities.length).toBe(2);
      expect(component.state().totalItems).toBe(2);
    });

    it('should have reactive computed properties', () => {
      setupComponent();

      fixture.detectChanges();

      expect(component.currentPage()).toBe(1);
      expect(component.pageSize()).toBe(50);
      expect(component.searchTerm()).toBe(null);
    });
  });

  describe('search functionality', () => {
    beforeEach(() => {
      setupComponent();
    });

    it('should perform search', () => {
      fixture.detectChanges();

      component.form.get('term')?.setValue('test-search');
      component.onSearch();

      expect(routerNavigateSpy).toHaveBeenCalledWith(
        [],
        expect.objectContaining({
          queryParams: expect.objectContaining({
            term: 'test-search',
          }),
          queryParamsHandling: 'merge',
          relativeTo: expect.any(Object),
          fragment: 'facilities',
        }),
      );
    });

    it('should not search when form is invalid', () => {
      component.form.get('term')?.setValue('ab'); // Less than 3 characters
      component.onSearch();

      expect(routerNavigateSpy).not.toHaveBeenCalled();
    });

    it('should handle null search term', () => {
      component.form.get('term')?.setValue('');
      component.onSearch();

      expect(routerNavigateSpy).toHaveBeenCalledTimes(1);
    });
  });

  describe('pagination', () => {
    beforeEach(() => {
      setupComponent();
    });

    it('should handle page change', () => {
      component.onPageChange(2);

      expect(routerNavigateSpy).toHaveBeenCalledWith(
        [],
        expect.objectContaining({
          queryParams: expect.objectContaining({
            page: 2,
          }),
          queryParamsHandling: 'merge',
          relativeTo: expect.any(Object),
          fragment: 'facilities',
        }),
      );
    });

    it('should handle page size change', () => {
      component.onPageSizeChange(100);

      expect(routerNavigateSpy).toHaveBeenCalledWith(
        [],
        expect.objectContaining({
          queryParams: expect.objectContaining({
            pageSize: 100,
          }),
          queryParamsHandling: 'merge',
          relativeTo: expect.any(Object),
          fragment: 'facilities',
        }),
      );
    });

    it('should not navigate when page values remain the same', () => {
      component.onPageChange(1);
      component.onPageSizeChange(50);

      expect(routerNavigateSpy).not.toHaveBeenCalled();
    });
  });

  describe('query parameter reactivity', () => {
    it('should update computed properties when query params change', () => {
      const { queryParamSubject } = setupComponent();

      fixture.detectChanges();

      // Update query params
      const newQueryParams = createQueryParamMock('3', '25', 'search-term');
      queryParamSubject.next(newQueryParams);

      expect(component.currentPage()).toBe(3);
      expect(component.pageSize()).toBe(25);
      expect(component.searchTerm()).toBe('search-term');
    });

    it('should trigger data loading when query params change', () => {
      const { queryParamSubject } = setupComponent();

      fixture.detectChanges();

      facilityInfoViewService.searchFacilities.mockClear();

      // Update query params
      const newQueryParams = createQueryParamMock('2', '25', 'test');
      queryParamSubject.next(newQueryParams);

      expect(facilityInfoViewService.searchFacilities).toHaveBeenCalledWith(123, 1, 25, 'test');
    });
  });
});
