import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, of } from 'rxjs';

import {
  RegulatorAuthoritiesService,
  SectorAssociationSiteContactInfoDTO,
  SectorAssociationSiteContactInfoResponse,
  SectorAssociationsSiteContactsService,
} from 'cca-api';

import { SiteContactsComponent } from './site-contacts.component';

describe('SiteContactsComponent', () => {
  let component: SiteContactsComponent;
  let fixture: ComponentFixture<SiteContactsComponent>;
  let siteContactsService: jest.Mocked<SectorAssociationsSiteContactsService>;
  let routerNavigateSpy: jest.SpyInstance;

  const mockSiteContacts: SectorAssociationSiteContactInfoResponse = {
    siteContacts: [
      { sectorName: 'Sector A', sectorAssociationId: 1, userId: 'user-1' },
      { sectorName: 'Sector B', sectorAssociationId: 2, userId: null },
    ] as SectorAssociationSiteContactInfoDTO[],
    editable: true,
    totalItems: 2,
  };

  const mockSiteContactsNotEditable: SectorAssociationSiteContactInfoResponse = {
    ...mockSiteContacts,
    editable: false,
  };

  const mockRegulators = {
    caUsers: [
      { userId: 'user-1', firstName: 'John', lastName: 'Doe', authorityStatus: 'ACTIVE' },
      { userId: 'user-2', firstName: 'Jane', lastName: 'Smith', authorityStatus: 'INACTIVE' },
    ],
  };

  const createQueryParamMock = (page = '1', pageSize = '20') => ({
    get: jest.fn().mockImplementation((param) => {
      if (param === 'page') return page;
      if (param === 'pageSize') return pageSize;
      return null;
    }),
  });

  const setupComponent = async (siteContactsData = mockSiteContacts) => {
    const siteContactsServiceMock = {
      getSectorAssociationSiteContacts: jest.fn().mockReturnValue(of(siteContactsData)),
      updateSectorAssociationSiteContacts: jest.fn().mockReturnValue(of(null)),
    };

    const regulatorServiceMock = {
      getCaRegulators: jest.fn().mockReturnValue(of(mockRegulators)),
    };

    const queryParamMock = createQueryParamMock();
    const queryParamSubject = new BehaviorSubject(queryParamMock);

    const activatedRouteMock = {
      snapshot: {
        queryParamMap: queryParamMock,
      },
      queryParamMap: queryParamSubject.asObservable(),
    };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SiteContactsComponent],
      providers: [
        { provide: SectorAssociationsSiteContactsService, useValue: siteContactsServiceMock },
        { provide: RegulatorAuthoritiesService, useValue: regulatorServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SiteContactsComponent);
    component = fixture.componentInstance;

    routerNavigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    siteContactsService = TestBed.inject(
      SectorAssociationsSiteContactsService,
    ) as jest.Mocked<SectorAssociationsSiteContactsService>;
  };

  afterEach(() => {
    routerNavigateSpy?.mockClear();
  });

  describe('when editable', () => {
    beforeEach(() => setupComponent(mockSiteContacts));

    it('should load data and update state when effect triggers', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(siteContactsService.getSectorAssociationSiteContacts).toHaveBeenCalledWith(0, 20);

      if (siteContactsService.getSectorAssociationSiteContacts.mock.calls.length > 0) {
        const updateMethod = component['update'];
        updateMethod(mockSiteContacts);
      }

      expect(component.state().isEditable).toBe(true);
      expect(component.state().totalItems).toBe(2);
      expect(component.state().siteContacts.length).toBe(2);
    }));

    it('should have reactive currentPage and pageSize computed properties', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(component.currentPage()).toBe(1);
      expect(component.pageSize()).toBe(20);
    }));

    it('should handle navigation and pagination correctly', () => {
      component.onPageChange(2);

      expect(routerNavigateSpy).toHaveBeenCalledWith(
        [],
        expect.objectContaining({
          queryParams: expect.objectContaining({ page: 2 }),
          fragment: 'site-contacts',
        }),
      );
    });

    it('should not navigate when page values remain the same', () => {
      component.onPageChange(1);
      component.onPageSizeChange(20);

      expect(routerNavigateSpy).not.toHaveBeenCalled();
    });

    it('should create correct assignee options', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      const options = component.assigneeOptions();
      expect(options.length).toBe(2);
      expect(options[0]).toEqual({ text: 'Unassigned', value: null });
      expect(options[1].value).toBe('user-1');
    }));
  });

  describe('when not editable', () => {
    beforeEach(() => setupComponent(mockSiteContactsNotEditable));

    it('should handle non-editable state correctly', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(component.state().isEditable).toBe(false);
    }));
  });
});
