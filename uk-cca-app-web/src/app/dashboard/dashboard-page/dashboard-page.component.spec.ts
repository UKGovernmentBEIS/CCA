import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { asyncData, BasePage } from '@netz/common/testing';

import { ItemsAssignedToMeService, ItemsAssignedToOthersService, UnassignedItemsService } from 'cca-api';

import { DashboardStore } from '../+store';
import { DashboardPageComponent } from './dashboard-page.component';

class Page extends BasePage<DashboardPageComponent> {
  get assignedToOthersTabLink() {
    return this.query<HTMLAnchorElement>('#tab_assigned-to-others');
  }

  get unassignedTabLink() {
    return this.query<HTMLAnchorElement>('#tab_unassigned');
  }

  get assignedToMeTabLink() {
    return this.query<HTMLAnchorElement>('#tab_assigned-to-me');
  }

  get assignedToMeTab() {
    return this.query<HTMLDivElement>('#assigned-to-me');
  }

  get assignedToOthersTab() {
    return this.query<HTMLDivElement>('#assigned-to-others');
  }

  get unassignedTab() {
    return this.query<HTMLDivElement>('#unassigned');
  }
}

describe('DashboardPageComponent', () => {
  let authStore: AuthStore;
  let component: DashboardPageComponent;
  let fixture: ComponentFixture<DashboardPageComponent>;
  let page: Page;
  let itemsAssignedToMeService: Partial<jest.Mocked<ItemsAssignedToMeService>>;
  let itemsAssignedToOthersService: Partial<jest.Mocked<ItemsAssignedToOthersService>>;
  let unassignedItemsService: Partial<jest.Mocked<UnassignedItemsService>>;

  const mockTasks = {
    items: [
      {
        taskType: 'APPLY_FOR_AN_UNDERLYING_AGREEMENT',
        requestType: 'UNDERLYING_AGREEMENT',
        taskAssigneeType: 'REGULATOR',
        daysRemaining: null,
        taskAssignee: null,
        creationDate: new Date('2020-11-13T13:00:00Z').toISOString(),
        requestId: '1',
        taskId: 2,
        isNew: true,
        accountName: 'DUMMY_ACCOUNT_NAME',
        businessId: 'DUMMY_BUSINESS_ID',
        sectorAcronym: 'ADS_1',
        sectorName: 'Sector 1',
      },
      {
        taskType: 'APPLY_FOR_AN_UNDERLYING_AGREEMENT',
        requestType: 'UNDERLYING_AGREEMENT',
        taskAssigneeType: 'OPERATOR',
        daysRemaining: 13,
        taskAssignee: { firstName: 'Sasha', lastName: 'Baron Cohen' },
        creationDate: new Date('2020-11-13T15:00:00Z').toISOString(),
        requestId: '3',
        taskId: 4,
        isNew: false,
        accountName: 'DUMMY_ACCOUNT_NAME2',
        businessId: 'DUMMY_BUSINESS_ID2',
        sectorAcronym: 'ADS_2',
        sectorName: 'Sector 2',
      },
    ],
    totalItems: 2,
  };

  const unassignedItems = {
    items: [
      {
        taskType: 'APPLY_FOR_AN_UNDERLYING_AGREEMENT',
        requestType: 'UNDERLYING_AGREEMENT',
        taskAssigneeType: 'REGULATOR',
        creationDate: new Date('2020-11-27T10:13:49Z').toISOString(),
        requestId: '40',
        taskId: 19,
        daysRemaining: 3,
        accountName: 'DUMMY_ACCOUNT_NAME3',
        businessId: 'DUMMY_BUSINESS_ID3',
        sectorAcronym: 'ADS_3',
        sectorName: 'Sector 3',
      },
    ],
    totalItems: 1,
  };

  beforeEach(async () => {
    itemsAssignedToMeService = {
      getAssignedItems: jest.fn().mockReturnValue(of(mockTasks)),
    };

    itemsAssignedToOthersService = {
      getAssignedToOthersItems: jest
        .fn()
        .mockReturnValue(asyncData({ items: mockTasks.items.slice(1, 2), totalPages: mockTasks.totalItems })),
    };

    unassignedItemsService = {
      getUnassignedItems: jest.fn().mockReturnValue(asyncData(unassignedItems)),
    };

    await TestBed.configureTestingModule({
      imports: [DashboardPageComponent, RouterTestingModule.withRoutes([], { paramsInheritanceStrategy: 'always' })],
      providers: [
        DashboardStore,
        { provide: ItemsAssignedToMeService, useValue: itemsAssignedToMeService },
        { provide: ItemsAssignedToOthersService, useValue: itemsAssignedToOthersService },
        { provide: UnassignedItemsService, useValue: unassignedItemsService },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({
      roleType: 'OPERATOR',
      userId: 'opTestId',
    });

    fixture = TestBed.createComponent(DashboardPageComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render assigned to others table rows', () => {
    page.assignedToOthersTabLink.click();
    fixture.detectChanges();

    const cells = Array.from(page.assignedToOthersTab.querySelectorAll('td'));
    const anchors = Array.from(page.assignedToOthersTab.querySelectorAll('td'))
      .map((cell) => cell.querySelector('a'))
      .filter((anchor) => !!anchor);

    expect(anchors.map((anchor) => anchor.href).length).toEqual(1);
    expect(cells.map((cell) => cell.textContent.trim())).toEqual([
      ...['', 'Sasha Baron Cohen', '13', 'DUMMY_BUSINESS_ID2', 'DUMMY_ACCOUNT_NAME2', 'ADS_2', 'Sector 2'],
    ]);
  });

  describe('for operators', () => {
    beforeEach(() => {
      authStore.setUserState({ roleType: 'OPERATOR', userId: '331' });
      fixture.detectChanges();
    });

    it('should display the unassigned items', () => {
      expect(page.unassignedTabLink).toBeTruthy();
    });
  });

  it('should NOT render assigned to me table rows', () => {
    expect(page.assignedToMeTabLink).toBeFalsy();
  });

  describe('for regulators', () => {
    beforeEach(() => {
      authStore.setUserState({ roleType: 'REGULATOR', userId: '332' });
      fixture.detectChanges();
    });

    it('should display the unassigned items', async () => {
      expect(page.unassignedTabLink).toBeTruthy();
      page.unassignedTabLink.click();
      fixture.detectChanges();

      const cells = Array.from(page.unassignedTab.querySelectorAll('td'));
      const anchors = Array.from(page.unassignedTab.querySelectorAll('td'))
        .map((cell) => cell.querySelector('a'))
        .filter((anchor) => !!anchor);

      expect(anchors.map((anchor) => anchor.href).length).toEqual(1);
      expect(cells.map((cell) => cell.textContent.trim())).toEqual([
        ...['', '3', 'DUMMY_BUSINESS_ID3', 'DUMMY_ACCOUNT_NAME3', 'ADS_3', 'Sector 3'],
      ]);
    });

    it('should display the assigned to me items', () => {
      expect(page.assignedToMeTabLink).toBeTruthy();
      page.assignedToMeTabLink.click();
      fixture.detectChanges();

      const cells = Array.from(page.assignedToMeTab.querySelectorAll('td'));
      const anchors = Array.from(page.assignedToMeTab.querySelectorAll('td'))
        .map((cell) => cell.querySelector('a'))
        .filter((anchor) => !!anchor);

      expect(page.assignedToMeTab).toBeTruthy();
      expect(anchors.map((anchor) => anchor.href).length).toEqual(2);
      expect(cells.map((cell) => cell.textContent.trim())).toEqual([
        'New',
        '',
        'DUMMY_BUSINESS_ID',
        'DUMMY_ACCOUNT_NAME',
        'ADS_1',
        'Sector 1',
        '',
        '13',
        'DUMMY_BUSINESS_ID2',
        'DUMMY_ACCOUNT_NAME2',
        'ADS_2',
        'Sector 2',
      ]);
    });
  });
});
