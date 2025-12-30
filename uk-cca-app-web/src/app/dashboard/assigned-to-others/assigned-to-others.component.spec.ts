import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';

import { ItemsAssignedToOthersService } from 'cca-api';

import { AssignedToOthersComponent } from './assigned-to-others.component';

const mockTasks = {
  items: [
    {
      taskType: 'APPLY_FOR_AN_UNDERLYING_AGREEMENT',
      requestType: 'UNDERLYING_AGREEMENT',
      taskAssigneeType: 'REGULATOR',
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
  totalItems: 1,
};

describe('AssignedToOthersComponent', () => {
  let component: AssignedToOthersComponent;
  let fixture: ComponentFixture<AssignedToOthersComponent>;
  let itemsAssignedToOthersService: Partial<jest.Mocked<ItemsAssignedToOthersService>>;

  beforeEach(async () => {
    itemsAssignedToOthersService = {
      getAssignedToOthersItems: jest.fn().mockReturnValue(of(mockTasks)),
    };

    await TestBed.configureTestingModule({
      imports: [AssignedToOthersComponent],
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: ItemsAssignedToOthersService, useValue: itemsAssignedToOthersService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AssignedToOthersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the assigned to others items', () => {
    const cells = Array.from(fixture.nativeElement.querySelectorAll('td') as HTMLTableCellElement[]);
    const anchors = Array.from(fixture.nativeElement.querySelectorAll('td') as HTMLTableCellElement[])
      .map((cell) => cell.querySelector('a'))
      .filter((anchor) => !!anchor);

    expect(anchors.map((anchor) => anchor.href).length).toEqual(1);
    expect(cells.map((cell) => cell.textContent.trim())).toEqual([
      '',
      'DUMMY_BUSINESS_ID2',
      'DUMMY_ACCOUNT_NAME2',
      'ADS_1-F00002 - fac1-2',
      '13',
      'ADS_2',
    ]);
  });
});
