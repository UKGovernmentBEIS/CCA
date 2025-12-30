import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';

import { ItemsAssignedToMeService } from 'cca-api';

import { AssignedToMeComponent } from './assigned-to-me.component';

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
      facilityBusinessId: 'ADS_1-F00001',
      siteName: 'fac1-1',
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
      facilityBusinessId: 'ADS_1-F00002',
      siteName: 'fac1-2',
    },
  ],
  totalItems: 2,
};

describe('AssignedToMeComponent', () => {
  let component: AssignedToMeComponent;
  let fixture: ComponentFixture<AssignedToMeComponent>;
  let itemsAssignedToMeService: Partial<jest.Mocked<ItemsAssignedToMeService>>;

  beforeEach(async () => {
    itemsAssignedToMeService = {
      getAssignedItems: jest.fn().mockReturnValue(of(mockTasks)),
    };

    await TestBed.configureTestingModule({
      imports: [AssignedToMeComponent],
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: ItemsAssignedToMeService, useValue: itemsAssignedToMeService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AssignedToMeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the assigned to me items', () => {
    const cells = Array.from(fixture.nativeElement.querySelectorAll('td') as HTMLTableCellElement[]);
    const anchors = Array.from(fixture.nativeElement.querySelectorAll('td') as HTMLTableCellElement[])
      .map((cell) => cell.querySelector('a'))
      .filter((anchor) => !!anchor);

    expect(anchors.map((anchor) => anchor.href).length).toEqual(2);
    expect(cells.map((cell) => cell.textContent.trim())).toEqual([
      'New',
      'DUMMY_BUSINESS_ID',
      'DUMMY_ACCOUNT_NAME',
      'ADS_1-F00001 - fac1-1',
      '',
      'ADS_1',
      '',
      'DUMMY_BUSINESS_ID2',
      'DUMMY_ACCOUNT_NAME2',
      'ADS_1-F00002 - fac1-2',
      '13',
      'ADS_2',
    ]);
  });
});
