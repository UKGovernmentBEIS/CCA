import { signal } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';

import { SectorAssociationAuthoritiesService, SectorAssociationTargetUnitAccountsInfoService } from 'cca-api';

import { mockSectorAuthorities, mockTargetUnits, mockTargetUnitsNotEditable } from '../../specs/fixtures/mock';
import { SectorTargetUnitsTabComponent } from './target-units-tab.component';

describe('SectorTargetUnitsTabComponent', () => {
  let component: SectorTargetUnitsTabComponent;
  let fixture: ComponentFixture<SectorTargetUnitsTabComponent>;
  let targetUnitService: jest.Mocked<SectorAssociationTargetUnitAccountsInfoService>;
  let routerNavigateSpy: jest.SpyInstance;

  const createQueryParamMock = (page = '1', pageSize = '50') => ({
    get: jest.fn().mockImplementation((param) => {
      if (param === 'page') return page;
      if (param === 'pageSize') return pageSize;
      return null;
    }),
  });

  const setupComponent = async (targetUnitsData = mockTargetUnits) => {
    const authStoreMock = {
      select: jest.fn().mockReturnValue(signal('test-user-id')),
    };

    const authoritiesServiceMock = {
      getSectorUserAuthoritiesBySectorAssociationId: jest.fn().mockReturnValue(of(mockSectorAuthorities)),
    };

    const targetUnitServiceMock = {
      getTargetUnitAccountsWithSiteContacts: jest.fn().mockReturnValue(of(targetUnitsData)),
      updateTargetUnitAccountSiteContacts: jest.fn().mockReturnValue(of(null)),
    };

    const queryParamMock = createQueryParamMock();
    const queryParamSubject = new BehaviorSubject(queryParamMock);

    const activatedRouteMock = {
      snapshot: {
        paramMap: { get: jest.fn().mockReturnValue('1') },
        queryParamMap: queryParamMock,
        fragment: null,
      },
      queryParamMap: queryParamSubject.asObservable(),
    };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SectorTargetUnitsTabComponent],
      providers: [
        { provide: SectorAssociationTargetUnitAccountsInfoService, useValue: targetUnitServiceMock },
        { provide: SectorAssociationAuthoritiesService, useValue: authoritiesServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: AuthStore, useValue: authStoreMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SectorTargetUnitsTabComponent);
    component = fixture.componentInstance;

    routerNavigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    targetUnitService = TestBed.inject(
      SectorAssociationTargetUnitAccountsInfoService,
    ) as jest.Mocked<SectorAssociationTargetUnitAccountsInfoService>;
  };

  afterEach(() => {
    routerNavigateSpy?.mockClear();
  });

  describe('when editable', () => {
    beforeEach(() => setupComponent(mockTargetUnits));

    it('should load data and update state when effect triggers', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(targetUnitService.getTargetUnitAccountsWithSiteContacts).toHaveBeenCalledWith(1, 0, 50);

      // The component's constructor subscribes to queryParams and updates state automatically
      expect(component.state().editable).toBe(true);
      expect(component.state().totalItems).toBe(3);
      expect(component.state().targetUnits.length).toBe(3);
    }));

    it('should have reactive currentPage and pageSize computed properties', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(component.currentPage()).toBe(1);
      expect(component.pageSize()).toBe(50);
    }));

    it('should handle navigation and pagination correctly', () => {
      component.onAddNewTargetUnit();
      component.onPageChange(2);

      expect(routerNavigateSpy).toHaveBeenCalledWith(
        ['target-units', 'create'],
        expect.objectContaining({
          relativeTo: expect.anything(),
        }),
      );
      expect(routerNavigateSpy).toHaveBeenCalledWith(
        [],
        expect.objectContaining({
          queryParams: expect.objectContaining({ page: 2 }),
        }),
      );
    });

    it('should not navigate when page values remain the same', () => {
      component.onPageChange(1);
      component.onPageSizeChange(50);

      expect(routerNavigateSpy).not.toHaveBeenCalled();
    });
  });

  describe('when not editable', () => {
    beforeEach(() => setupComponent(mockTargetUnitsNotEditable));

    it('should handle non-editable state correctly', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(component.state().editable).toBe(false);
    }));
  });
});
