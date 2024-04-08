import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { DashboardPageComponent, DashboardStore } from '@shared/dashboard';
import { asyncData,BasePage } from '@testing';

import {
  ItemDTOResponse,
  ItemsAssignedToMeService,
  ItemsAssignedToOthersService,
  UnassignedItemsService,
} from 'cca-api';


class Page extends BasePage<DashboardPageComponent> {
  get assignedToOthersTabLink() {
    return this.query<HTMLAnchorElement>('#tab_assigned-to-others');
  }

  get unassignedTabLink() {
    return this.query<HTMLAnchorElement>('#tab_unassigned');
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

  const mockTasks: ItemDTOResponse = {
    items: [
      {
        taskType: 'DUMMY_REQUEST_TASK_TYPE2',
        requestType: 'DUMMY_REQUEST_TYPE',
        taskAssigneeType: 'REGULATOR',
        daysRemaining: null,
        taskAssignee: null,
        creationDate: new Date('2020-11-13T13:00:00Z').toISOString(),
        requestId: '1',
        taskId: 2,
        isNew: true,
      },
      {
        taskType: 'DUMMY_REQUEST_TASK_TYPE2',
        requestType: 'DUMMY_REQUEST_TYPE',
        taskAssigneeType: 'OPERATOR',
        daysRemaining: 13,
        taskAssignee: { firstName: 'Sasha', lastName: 'Baron Cohen' },
        creationDate: new Date('2020-11-13T15:00:00Z').toISOString(),
        requestId: '3',
        taskId: 4,
        isNew: false,
      },
    ],
    totalItems: 2,
  };
  const unassignedItems: ItemDTOResponse = {
    items: [
      {
        taskType: 'DUMMY_REQUEST_TASK_TYPE2',
        requestType: 'DUMMY_REQUEST_TYPE',
        taskAssigneeType: 'REGULATOR',
        creationDate: new Date('2020-11-27T10:13:49Z').toISOString(),
        requestId: '40',
        taskId: 19,
        daysRemaining: 3,
      },
    ],
    totalItems: 1,
  };

  const createComponent = () => {
    fixture = TestBed.createComponent(DashboardPageComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
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
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render assigned to me table rows', () => {
    fixture.detectChanges();

    const anchors = Array.from(page.assignedToMeTab.querySelectorAll('td'))
      .map((cell) => cell.querySelector('a'))
      .filter((anchor) => !!anchor);
    expect(anchors.map((anchor) => anchor.href).length).toEqual(2);
  });

  it('should render assigned to others table rows', () => {
    page.assignedToOthersTabLink.click();
    fixture.detectChanges();

    const anchors = Array.from(page.assignedToOthersTab.querySelectorAll('td'))
      .map((cell) => cell.querySelector('a'))
      .filter((anchor) => !!anchor);
    expect(anchors.map((anchor) => anchor.href).length).toEqual(1);
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

  describe('for regulators', () => {
    beforeEach(() => {
      authStore.setUserState({ roleType: 'REGULATOR', userId: '332' });
      fixture.detectChanges();
    });

    it('should display the unassigned items', async () => {
      expect(page.unassignedTabLink).toBeTruthy();
      page.unassignedTabLink.click();
      fixture.detectChanges();

      const anchors = Array.from(page.unassignedTab.querySelectorAll('td'))
        .map((cell) => cell.querySelector('a'))
        .filter((anchor) => !!anchor);
      expect(anchors.map((anchor) => anchor.href).length).toEqual(1);
    });
  });

  describe('for verifiers', () => {
    beforeEach(() => {
      authStore.setUserState({ roleType: 'VERIFIER', userId: '332' });
      fixture.detectChanges();
    });

    it('should allow view of unassigned items', () => {
      expect(page.unassignedTabLink).toBeTruthy();
    });
  });
});
