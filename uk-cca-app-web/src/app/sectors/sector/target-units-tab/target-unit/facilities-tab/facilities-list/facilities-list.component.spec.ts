import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, of } from 'rxjs';

import { FacilityInfoViewService, FacilitySearchResults } from 'cca-api';

import { FacilitiesListComponent } from './facilities-list.component';

describe('FacilitiesListComponent', () => {
  let component: FacilitiesListComponent;
  let fixture: ComponentFixture<FacilitiesListComponent>;
  let facilityInfoViewService: jest.Mocked<FacilityInfoViewService>;
  let routerNavigateSpy: jest.SpyInstance;

  const mockFacilities: FacilitySearchResults = {
    facilities: [
      {
        id: 'ADS_1-F00001',
        siteName: 'fac1-1',
        schemeExitDate: new Date().toISOString(),
        status: 'LIVE',
        certificationStatus: 'CERTIFIED',
      },
      {
        id: 'ADS_1-F00002',
        siteName: 'fac1-2',
        schemeExitDate: new Date().toISOString(),
        status: 'INACTIVE',
        certificationStatus: 'DECERTIFIED',
      },
    ],
    total: 2,
  };

  const createQueryParamMock = (page = '1', pageSize = '50', term: string | null = null) => ({
    get: jest.fn().mockImplementation((param) => {
      if (param === 'page') return page;
      if (param === 'pageSize') return pageSize;
      if (param === 'term') return term;
      return null;
    }),
  });

  const setupComponent = (searchResults = mockFacilities) => {
    const facilityServiceMock = {
      searchFacilities: jest.fn().mockReturnValue(of(searchResults)),
    };

    const queryParamMock = createQueryParamMock();
    const queryParamSubject = new BehaviorSubject(queryParamMock);

    const activatedRouteMock = {
      snapshot: {
        paramMap: { get: jest.fn().mockReturnValue('123') },
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

    routerNavigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    facilityInfoViewService = TestBed.inject(FacilityInfoViewService) as jest.Mocked<FacilityInfoViewService>;

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

    it('should load data and update state when effect triggers', fakeAsync(() => {
      setupComponent();

      fixture.detectChanges();
      tick();

      expect(facilityInfoViewService.searchFacilities).toHaveBeenCalledWith(123, 0, 50, null);
      expect(component.state().facilities.length).toBe(2);
      expect(component.state().totalItems).toBe(2);
    }));

    it('should have reactive computed properties', fakeAsync(() => {
      setupComponent();

      fixture.detectChanges();
      tick();

      expect(component.currentPage()).toBe(1);
      expect(component.pageSize()).toBe(50);
      expect(component.searchTerm()).toBe(null);
    }));
  });

  describe('search functionality', () => {
    beforeEach(() => {
      setupComponent();
    });

    it('should perform search', fakeAsync(() => {
      fixture.detectChanges();
      tick();

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
    }));

    it('should not search when form is invalid', () => {
      component.form.get('term')?.setValue('ab'); // Less than 3 characters
      component.onSearch();

      expect(routerNavigateSpy).not.toHaveBeenCalled();
    });

    it('should handle null search term', () => {
      component.form.get('term')?.setValue('');
      component.onSearch();

      expect(routerNavigateSpy).not.toHaveBeenCalled();
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
    it('should update computed properties when query params change', fakeAsync(() => {
      const { queryParamSubject } = setupComponent();

      fixture.detectChanges();
      tick();

      // Update query params
      const newQueryParams = createQueryParamMock('3', '25', 'search-term');
      queryParamSubject.next(newQueryParams);
      tick();

      expect(component.currentPage()).toBe(3);
      expect(component.pageSize()).toBe(25);
      expect(component.searchTerm()).toBe('search-term');
    }));

    it('should trigger data loading when query params change', fakeAsync(() => {
      const { queryParamSubject } = setupComponent();

      fixture.detectChanges();
      tick();

      facilityInfoViewService.searchFacilities.mockClear();

      // Update query params
      const newQueryParams = createQueryParamMock('2', '25', 'test');
      queryParamSubject.next(newQueryParams);
      tick();

      expect(facilityInfoViewService.searchFacilities).toHaveBeenCalledWith(123, 1, 25, 'test');
    }));
  });

  describe('sorting', () => {
    it('should sort facilities correctly', fakeAsync(() => {
      setupComponent();

      fixture.detectChanges();
      tick();

      const sortedFacilities = component.facilities();
      expect(sortedFacilities).toBeDefined();
      expect(sortedFacilities.length).toBe(2);
      expect(sortedFacilities[0].id).toBe('ADS_1-F00001');
      expect(sortedFacilities[1].id).toBe('ADS_1-F00002');
    }));

    it('should handle empty facilities array', fakeAsync(() => {
      setupComponent({ facilities: [], total: 0 });

      fixture.detectChanges();
      tick();

      expect(component.facilities()).toEqual([]);
    }));
  });
});
